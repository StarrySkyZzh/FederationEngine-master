import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import fr.lirmm.graphik.graal.api.backward_chaining.QueryRewriter;
import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.Ontology;
import fr.lirmm.graphik.graal.api.core.Query;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.kb.Approach;
import fr.lirmm.graphik.graal.api.kb.KnowledgeBase;
import fr.lirmm.graphik.graal.backward_chaining.pure.PureRewriter;
import fr.lirmm.graphik.graal.core.DefaultConjunctiveQuery;
import fr.lirmm.graphik.graal.core.ruleset.DefaultOntology;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.graal.io.dlp.DlgpWriter;
import fr.lirmm.graphik.graal.io.owl.OWL2Parser;
import fr.lirmm.graphik.graal.kb.KBBuilder;
import fr.lirmm.graphik.graal.store.rdbms.driver.SqliteDriver;
import fr.lirmm.graphik.graal.store.rdbms.natural.NaturalRDBMSStore;
import fr.lirmm.graphik.util.Prefix;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;

public class ThirdTest {

    private static String rootDir = "./GraalTest/";
    private static final String dbFilepath = "ThirdTest.db";
    private static final String ruleFilepath = "ThirdTest.dlp";
    private static final String ontoFilepath = "univ-bench.owl";


    public static void main(String args[]) throws Exception {
//        test2();
//        test4();
        test1();
    }

    public static void test4() throws Exception {
        File f = new File(rootDir, ontoFilepath);
        System.out.println(f.exists());
        OWL2Parser owlparser = new OWL2Parser(f);
        do {
            Object o = owlparser.next();
            System.out.println(o.getClass());
            System.out.println(o.toString());
        } while (owlparser.hasNext());
        owlparser.close();
    }

    public static void test3() throws Exception {
        DlgpParser parser = new DlgpParser("human(socrate). mortal(X,Y) :- human(X), test(Y). ?(X) :- mortal(X).");
        while (parser.hasNext()) {
            Object o = parser.next();
            if (o instanceof Atom) {
                System.out.println("Atom: " + ((Atom) o));
            } else if (o instanceof Rule) {
                System.out.println("Rule: " + ((Rule) o));
            } else if (o instanceof ConjunctiveQuery) {
                System.out.println("ConjunctiveQuery: " + ((Query) o));
            }
        }
        parser.close();
    }

    public static void test2() throws Exception {
        String host = "103.61.226.39:5432";
        String dbName = "datalog_test";
        String user = "postgres";
        String password = "unisa";
        String uri = "jdbc:postgresql://" + host
                + "/" + dbName + "?user=" + user + "&password=" + password;
        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager.getConnection(uri);

        String sql = "select * from persons";
        Statement stmt = c.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnsNumber = rsmd.getColumnCount();
        while (rs.next()) {
            for (int i = 1; i <= columnsNumber; i++) {
                if (i > 1) System.out.print(",  ");
                String columnValue = rs.getString(i);
                System.out.print(rsmd.getColumnName(i) + " " + columnValue);
            }
            System.out.println("");
        }
        rs.close();
    }


    public static void test1() throws Exception {

        // 0 - Create a DLGP writer to display the rewritings
        DlgpWriter writer = new DlgpWriter();
//		// 1 - Define some prefixes
        writer.write(new Prefix("class", "http://unisa.edu.au/KSE.owl/class#"));
        writer.write(new Prefix("attribute", "http://unisa.edu.au/KSE.owl/attribute#"));
        writer.write(new Prefix("relationship", "http://unisa.edu.au/KSE.owl/relationship#"));
        writer.write(new Prefix("", ""));
        // 2 - Load rules from a DLGP File
        File f = new File(rootDir, ruleFilepath);
        Ontology onto = new DefaultOntology(new DlgpParser(f));
        // 3 - Create a query from a Java string
        ConjunctiveQuery query = DlgpParser.parseQuery("@prefix class: <http://unisa.edu.au/KSE.owl/class#>\n"
                + "        @prefix attribute: <http://unisa.edu.au/KSE.owl/attribute#>\n"
                + "        @prefix relationship: <http://unisa.edu.au/KSE.owl/relationship#>\n"
                + "        @prefix : <>\n"
                + "?(FN, LN, Age, Z) :- attribute:Age(X,Age), attribute:FN(X,FN), attribute:LN(X,LN), relationship:works_at(X,Y), attribute:Address(Y, Z).");

        // 4 - Print the query
        writer.write("\n= Query =\n");
        writer.write(query);
        // 5 - Initialize the rewriter
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

        KBBuilder kbb = new KBBuilder();
//        kbb.setStore(new NaturalRDBMSStore(new PostgreSQLDriver("103.61.226.39:5432", "datalog_test", "unisa", "unisa")));
        kbb.setStore(new NaturalRDBMSStore(new SqliteDriver(new File(rootDir, dbFilepath))));
        kbb.addRules(new DlgpParser(f));
        kbb.setApproach(Approach.REWRITING_FIRST);
        KnowledgeBase kb = kbb.build();

        // Print KB
        writer.write("= FACTS =\n");
        writer.write(kb.getFacts());
        writer.write("= RULES =\n");
        writer.write(kb.getOntology());

        writer.write("\n= Answers =\n");
        writer.write(lastConjunctiveQuery);
        lastConjunctiveQuery = DlgpParser.parseQuery(DlgpWriter.writeToString(lastConjunctiveQuery));
        writer.write(lastConjunctiveQuery);

        long start = System.nanoTime();
        CloseableIterator<Substitution> results = kb.query(lastConjunctiveQuery);
        long usedTime = System.nanoTime() - start;
        System.out.println("time cost of the query: " + usedTime);


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

