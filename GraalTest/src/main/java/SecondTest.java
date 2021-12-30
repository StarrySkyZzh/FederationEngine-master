import java.io.File;

import fr.lirmm.graphik.graal.api.backward_chaining.QueryRewriter;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.Ontology;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.kb.Approach;
import fr.lirmm.graphik.graal.api.kb.KnowledgeBase;
import fr.lirmm.graphik.graal.backward_chaining.pure.PureRewriter;
import fr.lirmm.graphik.graal.core.HashMapSubstitution;
import fr.lirmm.graphik.graal.core.ruleset.DefaultOntology;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.graal.io.dlp.DlgpWriter;
import fr.lirmm.graphik.graal.kb.KBBuilder;
import fr.lirmm.graphik.graal.store.rdbms.driver.SqliteDriver;
import fr.lirmm.graphik.graal.store.rdbms.natural.NaturalRDBMSStore;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;

public class SecondTest {

    private static String rootDir = "./GraalTest/";
    private static final String dbFilepath = "SecondTest.db";
    private static final String ontoFilepath = "SecondTest.dlp";
    private static DlgpWriter writer;


    public static void main(String args[]) throws Exception {

        KBBuilder kbb = new KBBuilder();
        kbb.setStore(new NaturalRDBMSStore(new SqliteDriver(new File(rootDir, dbFilepath))));
//        kbb.setStore(new NaturalRDBMSStore(new PostgreSQLDriver("103.61.226.39:5432", "datalog_test", "unisa", "unisa")));

        kbb.addRules(new DlgpParser(new File(rootDir, ontoFilepath)));
        kbb.setApproach(Approach.REWRITING_FIRST);
        KnowledgeBase kb = kbb.build();
        writer = new DlgpWriter();
        ConjunctiveQuery query = DlgpParser.parseQuery("?(X0,X1,X2,X3,X4) :- persons(X0,X1,X2,X3,X4).");
//        ConjunctiveQuery query = DlgpParser.parseQuery("?(X0,X1,X2,X3,X4) :- <PersonProfile>(X0,X1,X2,X3,X4).");
//        ConjunctiveQuery query = DlgpParser.parseQuery("?(X0,X1,X2) :- <WorksAt>(X0,X1), <Location>(X1,X2).");
//        ConjunctiveQuery query = DlgpParser.parseQuery("?(X0,X1,X2,X3,X4,X5,X6,X7,X8) :- <PersonProfile>(X0,X1,X2,X3,X4), <LivesIn>(X0,X5), <Location>(X5,X6,X7,X8,X9,X10).");
//        ConjunctiveQuery query = DlgpParser.parseQuery("?(X2,X7) :- persons(<1>,X2,X3,X4,X5), locations(<101>,X6,X7,X8,X9,X10).");
//        ConjunctiveQuery query = DlgpParser.parseQuery("?(X2,X5) :- <Person>(<1>,X1,X2), <Location>(<102>,X5).");

        writer.write("\n= Query =\n");
        writer.write(query);
        System.out.println("query: " + query.toString());

        //get the last conjunctive query from the rewritings
        Ontology onto = new DefaultOntology(new DlgpParser(new File(rootDir, ontoFilepath)));
        QueryRewriter rewriter = new PureRewriter();
        CloseableIteratorWithoutException<ConjunctiveQuery> it = rewriter.execute(query, onto);
        writer.write("\n= Rewritings =\n");
        ConjunctiveQuery lastConjunctiveQuery = null;
        while (it.hasNext()) {
            lastConjunctiveQuery = it.next();
            writer.write(lastConjunctiveQuery);
            writer.flush();
            System.out.println("lastQuery: " + lastConjunctiveQuery.toString());
        }
        it.close();

        //conduct query using the last conjunctive query and print results
        writer.write("\n= Answers =\n");
        lastConjunctiveQuery = DlgpParser.parseQuery(DlgpWriter.writeToString(lastConjunctiveQuery));
        CloseableIterator<Substitution> results = kb.query(lastConjunctiveQuery);
        if (results.hasNext()) {
            do {
                HashMapSubstitution newMap = (HashMapSubstitution) results.next();
//                for (Term key : newMap.getTerms()) {
//                    System.out.println(key.toString());
//                    System.out.println(newMap.createImageOf(key));
//                }
                writer.write(newMap);
            } while (results.hasNext());
        } else {
            writer.write("No answers.\n");
        }


        results.close();
        writer.close();
        kb.close();
    }

}
