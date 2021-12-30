import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import java.io.File;
import java.io.FileReader;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import fr.lirmm.graphik.graal.api.backward_chaining.QueryRewriter;
import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Ontology;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Query;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.UnionOfConjunctiveQueries;
import fr.lirmm.graphik.graal.api.store.Store;
import fr.lirmm.graphik.graal.backward_chaining.pure.PureRewriter;
import fr.lirmm.graphik.graal.core.DefaultRule;
import fr.lirmm.graphik.graal.core.DefaultUnionOfConjunctiveQueries;
import fr.lirmm.graphik.graal.core.ruleset.DefaultOntology;
import fr.lirmm.graphik.graal.homomorphism.SmartHomomorphism;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.graal.io.dlp.DlgpWriter;
import fr.lirmm.graphik.graal.io.owl.OWL2Parser;
import fr.lirmm.graphik.graal.store.rdbms.driver.PostgreSQLDriver;
import fr.lirmm.graphik.graal.store.rdbms.driver.SqliteDriver;
import fr.lirmm.graphik.graal.store.rdbms.natural.NaturalRDBMSStore;
import fr.lirmm.graphik.util.Prefix;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.converter.ConverterCloseableIterator;

public class ForthTestForDemo {


    private static final String CaseMagementSystemURL = "jdbc:postgresql://103.61.226.39:5432/CaseManagementSystem";
    private static final String VehicleManagementSystemURL = "jdbc:postgresql://103.61.226.39:5432/VehicleManagementSystem";
    private static final String CMSRulePath = "./DatalogRules/CMSRules.dlp";
    private static final String VMSRulePath = "./DatalogRules/VMSRules.dlp";

    public static void main(String args[]) throws Exception {
        Logger logger = LogManager.getLogger();
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        File file = new File("log4j2.xml");
        // this will force a reconfiguration
        context.setConfigLocation(file.toURI());

        test();
    }


    public static void test() throws Exception {

        // Create a DLGP writer to display the rewritings
        DlgpWriter writer = new DlgpWriter();

        // Define some prefixes
        writer.write(new Prefix("class", "http://unisa.edu.au/KSE.owl/class#"));
        writer.write(new Prefix("attribute", "http://unisa.edu.au/KSE.owl/attribute#"));
        writer.write(new Prefix("relationship", "http://unisa.edu.au/KSE.owl/relationship#"));
        writer.write(new Prefix("", ""));

        // 2 - Load rules from a DLGP File
        File f = new File(CMSRulePath);
        Ontology onto = new DefaultOntology(new DlgpParser(f));

        // 3 - Create a query from a Java string
        ConjunctiveQuery query = DlgpParser.parseQuery("@prefix class: <http://unisa.edu.au/KSE.owl/class#>\n"
                + "        @prefix attribute: <http://unisa.edu.au/KSE.owl/attribute#>\n"
                + "        @prefix relationship: <http://unisa.edu.au/KSE.owl/relationship#>\n"
                + "        @prefix : <>\n"
                + "?(X,Y) :- relationship:hasPN(X,Y).");

        // Print the query
        writer.write("\n= Query =\n");
        writer.write(query);

        // Initialize the rewriter
        QueryRewriter rewriter = new PureRewriter();

        // Print the rewrited query in a ucq
        CloseableIterator<ConjunctiveQuery> it = rewriter.execute(query, onto);
        List<Term> answerVariables = query.getAnswerVariables();
        LinkedList<Term> newAnswerVariables = new LinkedList<Term>();
        for (Term term : answerVariables) {
            if (!term.getLabel().contains("ADDONCONS_")) {
                newAnswerVariables.add(term);
            }
        }
        UnionOfConjunctiveQueries ucq = new DefaultUnionOfConjunctiveQueries(newAnswerVariables, it);

        writer.write("\n= Rewritings =\n");
        writer.write(ucq);

        // Connect to the DB
//        Store store = new NaturalRDBMSStore(new SqliteDriver(new File(rootDir, dbFilepath)));
        Store store = new NaturalRDBMSStore(new PostgreSQLDriver("103.61.226.39:5432", "datalog_test", "postgres", "unisa"));


        Set<Predicate> dbPredicates = store.getPredicates();
        Set<Predicate> normalisedDbPredicates = new HashSet<Predicate>();
        Iterator<Predicate> iterator = dbPredicates.iterator();
        while (iterator.hasNext()) {
            Predicate p = iterator.next();
            int arity = p.getArity();
            String identifier = (String) p.getIdentifier();
            identifier.toLowerCase();
            Predicate newP = new Predicate(identifier.toLowerCase(), arity);
            normalisedDbPredicates.add(newP);
        }
        System.out.println();

        //get a new iterator for the rewriting result
//        it = rewriter.execute(query, onto);
        it = ucq.iterator();

        //get the optimized rewritings for db query
        writer.write("\n= Optimized Rewritings =\n");
        ConjunctiveQuery lastConjunctiveQuery = null;
        Collection<ConjunctiveQuery> optimisedQueries = new LinkedList<ConjunctiveQuery>();
        while (it.hasNext()) {
            lastConjunctiveQuery = it.next();

            Pair<ConjunctiveQuery, Substitution> pair = EqualityUtils.processEquality(lastConjunctiveQuery);
            lastConjunctiveQuery = pair.getLeft();
            Substitution sub = pair.getRight();
            writer.write("sub: \n");
            writer.write(sub);
            Set terms = sub.getTerms();
            Set values = sub.getValues();
            writer.write("Terms: \n");
            Object[] termArray = terms.toArray();
            for (Object o : termArray) {
                writer.write(o);
            }
            writer.write("Values: \n");
            Object[] valueArray = values.toArray();
            for (Object o : valueArray) {
                writer.write(o);
            }
            writer.write("lastConjunctiveQuery: \n");
            writer.write(lastConjunctiveQuery);

            List<Term> src = lastConjunctiveQuery.getAnswerVariables();
            writer.write("old src");
            writer.write(src);
            src = sub.createImageOf(src);
            writer.write("new src");
            writer.write(src);
            lastConjunctiveQuery.setAnswerVariables(src);

//            Set<Predicate> queryPredicates = lastConjunctiveQuery.getAtomSet().getPredicates();
//            Iterator<Predicate> iterator2 = queryPredicates.iterator();
//            while(iterator2.hasNext()){
//                Predicate p = iterator2.next();
//                writer.write(p.toString()+"  ");
//            }
//            writer.write("\n");

            Set<Predicate> cqPredicates = lastConjunctiveQuery.getAtomSet().getPredicates();
            Set<Predicate> normalisedCqPredicates = new HashSet<Predicate>();
            Iterator<Predicate> cqPredicatesIterator = cqPredicates.iterator();
            while (cqPredicatesIterator.hasNext()) {
                Predicate p = cqPredicatesIterator.next();
                int arity = p.getArity();
                String identifier = p.getIdentifier().toString();
                identifier.toLowerCase();
                Predicate newP = new Predicate(identifier.toLowerCase(), arity);
                normalisedCqPredicates.add(newP);
            }


            if (normalisedDbPredicates.containsAll(normalisedCqPredicates)) {
                optimisedQueries.add(lastConjunctiveQuery);
                writer.flush();
                writer.write("Match\n");
            } else {
                writer.write("Doesn't match\n");
            }
        }

        // Query the DB
        writer.write("\n= Answers =\n");
        UnionOfConjunctiveQueries opUcq = new DefaultUnionOfConjunctiveQueries(ucq.getAnswerVariables(), optimisedQueries);
        long start = System.nanoTime();
        CloseableIterator results = SmartHomomorphism.instance().execute(opUcq, store);

//        CloseableIterator<Substitution> results = SmartHomomorphism.instance().execute(ucq, store);
        long usedTime = System.nanoTime() - start;
        writer.write("time cost of the query: " + usedTime + "\n");


//		// Display results
        if (results.hasNext()) {
            do {
                Substitution newMap = (Substitution) results.next();
//                newMap = newMap.aggregate(sub);
                for (Term key : newMap.getTerms()) {
                    writer.write(key.toString() + "\n");
                    writer.write(newMap.createImageOf(key) + "\n");
                }
                writer.write(newMap);
            } while (results.hasNext());

        } else {
            writer.write("No answers.\n");
        }

        // Close all resources
        results.close();
        it.close();
        writer.close();
    }

}
