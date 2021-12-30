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
import fr.lirmm.graphik.graal.api.kb.Approach;
import fr.lirmm.graphik.graal.api.kb.KnowledgeBase;
import fr.lirmm.graphik.graal.api.store.Store;
import fr.lirmm.graphik.graal.backward_chaining.pure.PureRewriter;
import fr.lirmm.graphik.graal.core.DefaultConjunctiveQuery;
import fr.lirmm.graphik.graal.core.DefaultRule;
import fr.lirmm.graphik.graal.core.DefaultUnionOfConjunctiveQueries;
import fr.lirmm.graphik.graal.core.ruleset.DefaultOntology;
import fr.lirmm.graphik.graal.homomorphism.SmartHomomorphism;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.graal.io.dlp.DlgpWriter;
import fr.lirmm.graphik.graal.kb.KBBuilder;
import fr.lirmm.graphik.graal.store.rdbms.RdbmsAtomIterator;
import fr.lirmm.graphik.graal.store.rdbms.driver.PostgreSQLDriver;
import fr.lirmm.graphik.graal.store.rdbms.driver.SqliteDriver;
import fr.lirmm.graphik.graal.store.rdbms.natural.NaturalRDBMSStore;
import fr.lirmm.graphik.util.Prefix;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;
import fr.lirmm.graphik.util.stream.converter.ConverterCloseableIterator;


public class TestForUnderstandingSourceCode {
    private static final String CaseMagementSystemURL = "jdbc:postgresql://103.61.226.39:5432/CaseManagementSystem";
    private static final String VehicleManagementSystemURL = "jdbc:postgresql://103.61.226.39:5432/VehicleManagementSystem";
    private static final String CMSRulePath = "./DatalogRules/CMSRules.dlp";
    private static final String VMSRulePath = "./DatalogRules/VMSRules.dlp";

    private static final String dbFilepath = "./GraalTest/ThirdTest.db";
    private static final String ruleFilepath = "./GraalTest/TestForUnderstandingSourceCode.dlp";

    public static void main(String args[]) throws Exception {
        Logger logger = LogManager.getLogger();
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        File file = new File("log4j2.xml");
        // this will force a reconfiguration
        context.setConfigLocation(file.toURI());
        new TestForUnderstandingSourceCode().runWithKB();
//        runWithHomophism();
    }

    public void runWithKB() throws Exception {

        DlgpWriter writer = new DlgpWriter();

        writer.write(new Prefix("class", "http://unisa.edu.au/KSE.owl/class#"));
        writer.write(new Prefix("attribute", "http://unisa.edu.au/KSE.owl/attribute#"));
        writer.write(new Prefix("relationship", "http://unisa.edu.au/KSE.owl/relationship#"));
        writer.write(new Prefix("", ""));

        File f = new File(ruleFilepath);
        Ontology onto = new DefaultOntology(new DlgpParser(f));
//
        ConjunctiveQuery query = DlgpParser.parseQuery("@prefix class: <http://unisa.edu.au/KSE.owl/class#>\n"
                                                       + "        @prefix attribute: <http://unisa.edu.au/KSE.owl/attribute#>\n"
                                                       + "        @prefix relationship: <http://unisa.edu.au/KSE.owl/relationship#>\n"
                                                       + "        @prefix : <>\n"
//                                                       + "?(X,Age) :- attribute:Age(X,Age).");
//                                                       + "?(X,Y,FN) :- attribute:FN(X,FN), relationship:works_at(X,Y).");
                                                       + "?(X,Y,Age,FN,Address) :- attribute:Age(X,Age), attribute:FN(X,FN), relationship:works_at(X,Y), attribute:Address(Y, Address).");

        writer.write("\n= Query =\n");
        writer.write(query);

        QueryRewriter rewriter = new PureRewriter();
        CloseableIteratorWithoutException<ConjunctiveQuery> it = rewriter.execute(query, onto);

        writer.write("\n= Rewritings =\n");
        ConjunctiveQuery lastConjunctiveQuery = new DefaultConjunctiveQuery();
        while (it.hasNext()) {
            lastConjunctiveQuery = it.next();
            writer.write(lastConjunctiveQuery);
            writer.flush();
//            InMemoryAtomSet set = lastConjunctiveQuery.getAtomSet();
//            Set s = set.getPredicates();
//            for (Object o:s){
//                System.out.println(o.toString());
//            }
        }

//        PostgreSQLDriver driver = new PostgreSQLDriver("103.61.226.39:5432", "CaseManagementSystem", "postgres", "unisa");
//        NaturalRDBMSStore store = new NaturalRDBMSStore(driver);
        NaturalRDBMSStore store = new NaturalRDBMSStore(new SqliteDriver(new File(dbFilepath)));
//        NaturalRDBMSStore store = new NaturalRDBMSStore(new SqliteDriver(new File("./GraalTest/CaseManagementSystem.db")));
//        RdbmsAtomIterator storeIterator = new RdbmsAtomIterator(store);
//        while (storeIterator.hasNext()){
//           System.out.println(storeIterator.next());
//        }
//        System.out.println();


        KBBuilder kbb = new KBBuilder();
        kbb.setStore(store);
        kbb.addRules(new DlgpParser(f));
        kbb.setApproach(Approach.REWRITING_FIRST);
        KnowledgeBase kb = kbb.build();

        // Print KB
//        writer.write("= FACTS =\n");
//        writer.write(kb.getFacts());
//        writer.write("= RULES =\n");
//        writer.write(kb.getOntology());

        writer.write("\n= Answers =\n");
        writer.write(lastConjunctiveQuery);
        lastConjunctiveQuery = DlgpParser.parseQuery(DlgpWriter.writeToString(lastConjunctiveQuery));
        writer.write(lastConjunctiveQuery);

        long start = System.nanoTime();
        CloseableIterator<Substitution> results = kb.query(lastConjunctiveQuery);
        long usedTime = System.nanoTime() - start;
        writer.write("time cost of the query: " + usedTime);


        if (results.hasNext()) {
            do {
                writer.write(results.next());
            } while (results.hasNext());
        } else {
            writer.write("No answers.\n");
        }

        results.close();
        kb.close();
        it.close();
        writer.close();
    }
}
