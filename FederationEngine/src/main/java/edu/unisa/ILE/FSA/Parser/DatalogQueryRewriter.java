package edu.unisa.ILE.FSA.Parser;

import org.json.simple.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import fr.lirmm.graphik.graal.api.backward_chaining.QueryRewriter;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.Ontology;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.RuleSetException;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.UnionOfConjunctiveQueries;
import fr.lirmm.graphik.graal.api.io.ParseException;
import fr.lirmm.graphik.graal.api.store.Store;
import fr.lirmm.graphik.graal.backward_chaining.pure.PureRewriter;
import fr.lirmm.graphik.graal.core.DefaultUnionOfConjunctiveQueries;
import fr.lirmm.graphik.graal.core.ruleset.DefaultOntology;
import fr.lirmm.graphik.graal.homomorphism.SmartHomomorphism;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.graal.io.dlp.DlgpWriter;
import fr.lirmm.graphik.graal.store.rdbms.driver.PostgreSQLDriver;
import fr.lirmm.graphik.graal.store.rdbms.natural.NaturalRDBMSStore;
import fr.lirmm.graphik.util.Prefix;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;

public class DatalogQueryRewriter {
    private DlgpWriter writer;
    private Ontology onto;
    private LinkedList<Prefix> prefixList;
    public Ontology getOnto() {
        return onto;
    }
    public void setOnto(Ontology onto) {
        this.onto = onto;
    }
    public void setPrefixList(LinkedList<Prefix> prefixList) {
        this.prefixList = prefixList;
    }
    public LinkedList<Prefix> getPrefixList() {
        return prefixList;
    }

    public static LinkedList<Prefix> getDefaultPrefixList(){
        LinkedList<Prefix> defaultPrefixList = new LinkedList<>();
        defaultPrefixList.add(new Prefix("class", "http://unisa.edu.au/KSE.owl/class#"));
        defaultPrefixList.add(new Prefix("attribute", "http://unisa.edu.au/KSE.owl/attribute#"));
        defaultPrefixList.add(new Prefix("relationship", "http://unisa.edu.au/KSE.owl/relationship#"));
        defaultPrefixList.add(new Prefix("", ""));
        return defaultPrefixList;
    }

    public DatalogQueryRewriter(File DLGPRules) throws IOException, RuleSetException{
        this(getDefaultPrefixList(),DLGPRules);
//        //create a default prefixList
//        LinkedList<Prefix> defaultPrefixList = new LinkedList<>();
//        defaultPrefixList.add(new Prefix("class", "http://unisa.edu.au/KSE.owl/class#"));
//        defaultPrefixList.add(new Prefix("attribute", "http://unisa.edu.au/KSE.owl/attribute#"));
//        defaultPrefixList.add(new Prefix("relationship", "http://unisa.edu.au/KSE.owl/relationship#"));
//        defaultPrefixList.add(new Prefix("", ""));
//
//        //initialise the dlgp writer
//        this.prefixList = defaultPrefixList;
//        this.writer = new DlgpWriter();
//        for (Prefix prefix : this.prefixList){
//            this.writer.write(prefix);
//        }
//
//        //initialise the Ontology
//        this.onto = new DefaultOntology(new DlgpParser(DLGPRules));
    }

    //initialise the query rewriter
    public DatalogQueryRewriter(LinkedList<Prefix> prefixList, File DLGPRules) throws IOException, RuleSetException{
        //initialise the dlgp writer
        this.prefixList = prefixList;
        this.writer = new DlgpWriter();
        for (Prefix prefix : this.prefixList){
            this.writer.write(prefix);
        }
        //initialise the Ontology
        this.onto = new DefaultOntology(new DlgpParser(DLGPRules));
    }

    public DatalogQueryRewriter(JSONObject prefixJSON, File DLGPRules) throws IOException, RuleSetException{
        //convert prefixJSON to prefixList
        LinkedList<Prefix> prefixList = new LinkedList<>();
        for (Object o : prefixJSON.entrySet()){
            Map.Entry e = (Map.Entry) o;
            String key = (String) e.getKey();
            String value = (String) e.getValue();
            Prefix prefix = new Prefix(key, value);
            prefixList.add(prefix);
        }
        //initialise the dlgp writer
        this.prefixList = prefixList;
        this.writer = new DlgpWriter();
        for (Prefix prefix : this.prefixList){
            this.writer.write(prefix);
        }
        //initialise the Ontology
        this.onto = new DefaultOntology(new DlgpParser(DLGPRules));
    }

    public UnionOfConjunctiveQueries rewrite(String queryString) throws IOException{
        //parse the query
        ConjunctiveQuery query = DlgpParser.parseQuery(queryString);

        // Initialize the rewriter
        PureRewriter rewriter = new PureRewriter();

        // Print the rewrited query in a ucq
        CloseableIterator<ConjunctiveQuery> it = rewriter.execute(query, this.onto);
        UnionOfConjunctiveQueries ucq = new DefaultUnionOfConjunctiveQueries(query.getAnswerVariables(), it);

//        this.writer.write("\n= Rewritings =\n");
//        this.writer.write(ucq);
//        this.writer.flush();

        it.close();
        return ucq;
    }

    //optimise the union of conjunctive query according to a specific store's schema
    public UnionOfConjunctiveQueries optimise(UnionOfConjunctiveQueries ucq, Store store) throws IOException,
                                                                                                 AtomSetException{
        //get db predicates and convert all to lowercases.
        Set<Predicate> dbPredicates = store.getPredicates();
        Set<Predicate> normalisedDbPredicates = new HashSet<Predicate>();
        Iterator<Predicate> storePredicateIterator = dbPredicates.iterator();
        while(storePredicateIterator.hasNext()){
            Predicate p = storePredicateIterator.next();
            int arity = p.getArity();
            String identifier = (String) p.getIdentifier();
            identifier.toLowerCase();
            Predicate newP = new Predicate(identifier.toLowerCase(),arity);
            normalisedDbPredicates.add(newP);
        }

        //compare each conjunctive query's predicates and db predicates, if all query predicates are db predicates
        //then the conjunctive query is db executable and added to the optimised query set.
        CloseableIterator<ConjunctiveQuery> ucqConjunctiveQueryIterator = ucq.iterator();
        ConjunctiveQuery lastConjunctiveQuery = null;
        Collection<ConjunctiveQuery> optimisedQueries = new LinkedList<ConjunctiveQuery>();
        while (ucqConjunctiveQueryIterator.hasNext()) {
            lastConjunctiveQuery = ucqConjunctiveQueryIterator.next();
//            Set<Predicate> queryPredicates = lastConjunctiveQuery.getAtomSet().getPredicates();
//            Iterator<Predicate> queryPredicateIterator = queryPredicates.iterator();
//            while(queryPredicateIterator.hasNext()){
//                Predicate p = queryPredicateIterator.next();
//                System.out.print(p.toString()+"  ");
//            }
//            System.out.println();
            if(normalisedDbPredicates.containsAll(lastConjunctiveQuery.getAtomSet().getPredicates())) {
                optimisedQueries.add(lastConjunctiveQuery);
            }
        }

        //create the optimisedUcq
        UnionOfConjunctiveQueries optimisedUcq = new DefaultUnionOfConjunctiveQueries(lastConjunctiveQuery.getAnswerVariables(), optimisedQueries);

        //print the optimised ucq
//        this.writer.write("\n= Optimised rewritings =\n");
//        this.writer.write(optimisedUcq);
//        this.writer.flush();

        ucqConjunctiveQueryIterator.close();
        return optimisedUcq;
    }

}
