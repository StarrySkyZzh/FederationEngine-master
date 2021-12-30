package FederatedDataAccess;

import fr.lirmm.graphik.graal.api.core.*;
import fr.lirmm.graphik.graal.backward_chaining.pure.PureRewriter;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.DefaultConjunctiveQuery;
import fr.lirmm.graphik.graal.core.DefaultUnionOfConjunctiveQueries;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.ruleset.DefaultOntology;
import fr.lirmm.graphik.graal.homomorphism.SmartHomomorphism;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.graal.io.dlp.DlgpWriter;
import fr.lirmm.graphik.graal.rulesetanalyser.property.RuleSetProperty;
import fr.lirmm.graphik.graal.store.rdbms.RdbmsAtomIterator;
import fr.lirmm.graphik.graal.store.rdbms.driver.AbstractRdbmsDriver;
import fr.lirmm.graphik.graal.store.rdbms.natural.NaturalRDBMSStore;
import fr.lirmm.graphik.util.Prefix;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;
import fr.lirmm.graphik.util.stream.IteratorException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import java.io.Closeable;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class FDAFunctions {

    public static void main(String[] args) {

        Logger logger = LogManager.getLogger();
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        File file = new File("log4j2.xml");
        context.setConfigLocation(file.toURI());

        SourceRepository repo = SourceRepository.defaultInstance("FDARepo");


        try {
            Ontology onto = createRDBMSOntology(repo.getSourcePool());
            DlgpWriter writer = createDefaultWriter();
            ConjunctiveQuery query = buildQuery("?(ID1,ID2) :- class:Person(ID1), relationship:Person_Case(ID1,ID2).");
            writer.write(query);

            PureRewriter rewriter = new PureRewriter();
            CloseableIteratorWithoutException<ConjunctiveQuery> it = rewriter.execute(query, onto);
            UnionOfConjunctiveQueries ucq = new DefaultUnionOfConjunctiveQueries(query.getAnswerVariables(), it);

            writer.write("\n= Rewritings =\n");
            writer.write(ucq);

            writer.write("\n= optimised rewritings =\n");
            Collection<ConjunctiveQuery> optimisedQueries = UCQOptimisation(ucq);
            writer.write(optimisedQueries);

            UnionOfConjunctiveQueries opUcq = new DefaultUnionOfConjunctiveQueries(query.getAnswerVariables(), optimisedQueries);
            Map<String, LinkedList<String>> sourceTableMap = getDBTableNamesFromOptimisedQueries(opUcq);
            sourceTableMap.forEach( (s,t) -> {
                System.out.println(s);
                for (String string:t){
                    System.out.println("   "+string);
                }
            });

            CloseableIterator<Substitution> results = null;
            if (sourceTableMap.keySet().size() == 1) {
                //default query approach
                System.out.println("single source involved");
                String sourceName = null;
                Iterator<String> sourceNameIt = sourceTableMap.keySet().iterator();
                while (sourceNameIt.hasNext()) {
                    sourceName = sourceNameIt.next();
                }
                results = querySingleRDBMSSource(opUcq, sourceName, repo.getSourcePool());
            } else if (sourceTableMap.keySet().size() > 1) {
                //federated query approach
                System.out.println("multiple source involved");
                results = queryMultipleRDBMSSource(opUcq, sourceTableMap, repo.getSourcePool());
            }

//            AtomSet atomSet = createRDBMSStore(repo.getSourcePool());
//            CloseableIterator<Substitution> results = SmartHomomorphism.instance().execute(ucq, atomSet);

            // Display results
            if (results != null && results.hasNext()) {
                while (results.hasNext()) {
                    writer.write(results.next());

                }
                results.close();
            } else {
                writer.write("No answers.\n");
            }

            // Close all resources

            it.close();
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static CloseableIterator<Substitution> querySingleRDBMSSource(UnionOfConjunctiveQueries ucq, String sourceName, Set<Source> sourcePool) throws Exception {
        //filter RDBMS source
        Map<String, RDBMSSource> rdbmsSourceMap = new HashMap<>();
        for (Source source : sourcePool) {
            if (source instanceof RDBMSSource) {
                rdbmsSourceMap.put(source.getSourceName(), (RDBMSSource) source);
            }
        }
        //remove sourceName prefix from all predicates
        CloseableIterator<ConjunctiveQuery> ucqIt = ucq.iterator();
        LinkedList<ConjunctiveQuery> newCqSet = new LinkedList<>();
        while(ucqIt.hasNext()){
            ConjunctiveQuery cq= ucqIt.next();
            CloseableIterator<Atom> cqIt = cq.iterator();
            LinkedListAtomSet newAtomSet = new LinkedListAtomSet();
            while(cqIt.hasNext()){
                Atom atom = cqIt.next();
                Predicate p = atom.getPredicate();
                String newName = p.getIdentifier().toString().split("_",2)[1];
                Predicate newP = new Predicate(newName, p.getArity());
                DefaultAtom newAtom = new DefaultAtom(newP, atom.getTerms());
                newAtomSet.add(newAtom);
                }
            ConjunctiveQuery newCq = new DefaultConjunctiveQuery(newAtomSet, cq.getAnswerVariables());
            newCqSet.add(newCq);
        }
        UnionOfConjunctiveQueries newUcq = new DefaultUnionOfConjunctiveQueries(ucq.getAnswerVariables(), newCqSet);

        //query
        RDBMSSource source = rdbmsSourceMap.get(sourceName);
        NaturalRDBMSStore store = new NaturalRDBMSStore(source.getDriver());
        CloseableIterator<Substitution> results = SmartHomomorphism.instance().execute(newUcq, store);
        return results;
    }

    public static CloseableIterator<Substitution> queryMultipleRDBMSSource(UnionOfConjunctiveQueries ucq, Map<String, LinkedList<String>> sourceTableMap, Set<Source> sourcePool) throws Exception {
        AtomSet atomSet = createRDBMSStore(sourceTableMap, sourcePool);
        CloseableIterator results = SmartHomomorphism.instance().execute(ucq, atomSet);
        return results;
    }

    public static DlgpWriter createDefaultWriter() throws Exception {
        DlgpWriter writer = new DlgpWriter();
        writer.write(new Prefix("class", "http://unisa.edu.au/KSE.owl/class#"));
        writer.write(new Prefix("attribute", "http://unisa.edu.au/KSE.owl/attribute#"));
        writer.write(new Prefix("relationship", "http://unisa.edu.au/KSE.owl/relationship#"));
        writer.write(new Prefix("", ""));
        return writer;
    }

    public static ConjunctiveQuery buildQuery(String query) throws Exception {
        String prefixes = "@prefix class: <http://unisa.edu.au/KSE.owl/class#>\n"
                + "        @prefix attribute: <http://unisa.edu.au/KSE.owl/attribute#>\n"
                + "        @prefix relationship: <http://unisa.edu.au/KSE.owl/relationship#>\n"
                + "        @prefix : <>\n";
        String wholeString = prefixes + query;
        ConjunctiveQuery conjunctiveQuery = DlgpParser.parseQuery(wholeString);
        return conjunctiveQuery;
    }

    public static Ontology createRDBMSOntology(Set<Source> sourcePool) throws Exception {
        Set<RDBMSSource> rdbmsSourceSet = new HashSet<RDBMSSource>();
        for (Source source : sourcePool) {
            if (source instanceof RDBMSSource) {
                rdbmsSourceSet.add((RDBMSSource) source);
            }
        }

        Set<String> prefixes = new HashSet<String>();
        Set<String> rules = new HashSet<String>();
        for (RDBMSSource source : rdbmsSourceSet) {
            String ruleFilePath = source.getRuleFilePath();
            List<String> contents = Files.readAllLines(Paths.get(ruleFilePath));
            for (String s : contents) {
                if (s.startsWith("@prefix")) {
                    prefixes.add(s);
                } else {
                    rules.add(s);
                }
            }
        }
        String allRules = "";
        for (String prefix : prefixes) {
            allRules += prefix + "\n";
        }
        for (String rule : rules) {
            allRules += rule + "\n";
        }
        return new DefaultOntology(new DlgpParser(allRules));
    }

    //function version that collects all facts from all RDBMS sources
    public static AtomSet createRDBMSStore(Set<Source> sourcePool) throws Exception {
        //filter RDBMS source
        Set<RDBMSSource> rdbmsSourceSet = new HashSet<RDBMSSource>();
        for (Source source : sourcePool) {
            if (source instanceof RDBMSSource) {
                rdbmsSourceSet.add((RDBMSSource) source);
            }
        }
        //gather facts from stores
        //rename predicates with sourceName_predicateName
        LinkedListAtomSet atomSet = new LinkedListAtomSet();
        for (RDBMSSource source : rdbmsSourceSet) {
            NaturalRDBMSStore store = new NaturalRDBMSStore(source.getDriver());
            CloseableIterator<Atom> atomIt = store.iterator();
            while (atomIt.hasNext()) {
                Atom atom = atomIt.next();
                Predicate p = atom.getPredicate();
                String predicateName = (String) p.getIdentifier();
                String sourceName = source.getSourceName();
                Predicate newPredicate = new Predicate(sourceName + "_" + predicateName, p.getArity());
                DefaultAtom newAtom = new DefaultAtom(newPredicate, atom.getTerms());
//                System.out.println(newAtom);
                atomSet.add(newAtom);
            }
        }
        return atomSet;
    }
    //function version that only collects addressed tables in sourceTableMap in corresponding RDBMS sources
    public static AtomSet createRDBMSStore(Map<String, LinkedList<String>> sourceTableMap, Set<Source> sourcePool) throws Exception {
        LinkedListAtomSet atomSet = new LinkedListAtomSet();
        //filter RDBMS source
        Map<String, RDBMSSource> rdbmsSourceMap = new HashMap<>();
        for (Source source : sourcePool) {
            if (source instanceof RDBMSSource) {
                rdbmsSourceMap.put(source.getSourceName(), (RDBMSSource) source);
            }
        }
        //identify tables from sources
        Set<String> sourceNames = sourceTableMap.keySet();
        for (String sourceName : sourceNames) {
            RDBMSSource source = rdbmsSourceMap.get(sourceName);
            AbstractRdbmsDriver driver = source.getDriver();
            NaturalRDBMSStore store = new NaturalRDBMSStore(driver);
            CloseableIterator<Predicate> predicateIt = store.predicatesIterator();
            LinkedList<String> tableNameList = sourceTableMap.get(sourceName);

            //gather facts from tables
            FilteredPredicateIterator newIt = new FilteredPredicateIterator(tableNameList,predicateIt);
            RdbmsAtomIterator atomIt = new RdbmsAtomIterator(newIt, store);

            while(atomIt.hasNext()){
                Atom atom = atomIt.next();
                Predicate p = atom.getPredicate();
                String predicateName = (String) p.getIdentifier();
                Predicate newPredicate = new Predicate(sourceName + "_" + predicateName, p.getArity());
                DefaultAtom newAtom = new DefaultAtom(newPredicate, atom.getTerms());
//                System.out.println(newAtom);
                atomSet.add(newAtom);
            }

        }

        //rename predicates with sourceName_predicateName
        return atomSet;
    }

    public static Collection<ConjunctiveQuery> UCQOptimisation(UnionOfConjunctiveQueries ucq) throws Exception {
        //get a new iterator for the rewriting result
        CloseableIterator<ConjunctiveQuery> it = ucq.iterator();

        //get the optimized rewritings for db query
        ConjunctiveQuery lastConjunctiveQuery = null;
        Collection<ConjunctiveQuery> optimisedQueries = new LinkedList<>();
        while (it.hasNext()) {
            boolean match = true;
            lastConjunctiveQuery = it.next();
            Set<Predicate> cqPredicates = lastConjunctiveQuery.getAtomSet().getPredicates();
            Iterator<Predicate> cqPredicatesIterator = cqPredicates.iterator();
            while (cqPredicatesIterator.hasNext()) {
                Predicate p = cqPredicatesIterator.next();
                String identifier = p.getIdentifier().toString();
                if (identifier.contains("#")) {
                    match = false;
                }
            }
            if (match) {
                optimisedQueries.add(lastConjunctiveQuery);
            }
        }

        return optimisedQueries;
    }

    public static Map<String, LinkedList<String>> getDBTableNamesFromOptimisedQueries(UnionOfConjunctiveQueries ucq) throws Exception {
        Map<String, LinkedList<String>> sourceTableMap = new HashMap<>();
        CloseableIterator<ConjunctiveQuery> it = ucq.iterator();
        ConjunctiveQuery lastConjunctiveQuery = null;
        Set<Predicate> allPredicates = new HashSet();
        while (it.hasNext()) {
            lastConjunctiveQuery = it.next();
            Set<Predicate> predicates = lastConjunctiveQuery.getAtomSet().getPredicates();
            allPredicates.addAll(predicates);
        }

        for (Predicate p : allPredicates) {
            String name = p.getIdentifier().toString();
            String[] strings = name.split("_", 2);
            String sourceName = strings[0];
            String tableName = strings[1];
            if (sourceTableMap.containsKey(sourceName)) {
                LinkedList<String> tableList = sourceTableMap.get(sourceName);
                tableList.add(tableName);
            } else {
                LinkedList<String> tableList = new LinkedList<>();
                tableList.add(tableName);
                sourceTableMap.put(sourceName, tableList);
            }
        }
        return sourceTableMap;
    }


}
