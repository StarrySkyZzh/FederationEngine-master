import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

import fr.lirmm.graphik.graal.api.backward_chaining.QueryRewriter;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.Ontology;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.io.Parser;
import fr.lirmm.graphik.graal.api.kb.Approach;
import fr.lirmm.graphik.graal.api.kb.KnowledgeBase;
import fr.lirmm.graphik.graal.api.kb.KnowledgeBaseException;
import fr.lirmm.graphik.graal.api.store.Store;
import fr.lirmm.graphik.graal.backward_chaining.pure.PureRewriter;
import fr.lirmm.graphik.graal.core.mapper.MappedStore;
import fr.lirmm.graphik.graal.core.mapper.PrefixMapper;
import fr.lirmm.graphik.graal.core.ruleset.DefaultOntology;
import fr.lirmm.graphik.graal.core.stream.filter.AtomFilterIterator;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.graal.io.dlp.DlgpWriter;
import fr.lirmm.graphik.graal.kb.KBBuilder;
import fr.lirmm.graphik.graal.kb.KBBuilderException;
import fr.lirmm.graphik.graal.store.rdbms.driver.SqliteDriver;
import fr.lirmm.graphik.graal.store.rdbms.natural.NaturalRDBMSStore;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;

public class FirstTest {

    private static String rootDir = "./GraalTest/";

    private static final String dbFilepath = "FirstTest.db";
    private static final String ontoFilepath = "FirstTest.dlp";
    private static DlgpWriter writer;

    public static void main(String args[]) throws Exception {

        if (args.length > 0) {
            rootDir = args[0];
        }

        // 0 - initialize the database if needed
        if (!new File(rootDir, dbFilepath).exists()) {
            System.out.println("db do not exist");
            init();
        }

        // 1 - create a KBBuilder
        KBBuilder kbb = new KBBuilder();
        // 2 - set the connection to the database
        kbb.setStore(new NaturalRDBMSStore(new SqliteDriver(new File(rootDir, dbFilepath))));
        // 3 - set the ontology
        kbb.addRules(new DlgpParser(new File(rootDir, ontoFilepath)));
        // 4 - set the privileged mechanism
        kbb.setApproach(Approach.REWRITING_FIRST);
        // 5 - build the KB
        KnowledgeBase kb = kbb.build();

        // 6 - Create a DLGP writer to print results
        writer = new DlgpWriter();

        // 7 - parse and print a query
        ConjunctiveQuery query = DlgpParser.parseQuery("?(X0,X1,X2,X3,X4) :- <Persons>(X0, X1, X2, X3, X4).");
//        ConjunctiveQuery query = DlgpParser.parseQuery("?(X0, X1, X2, X3, X4) :- <Person>(X0, X1, X2), <PersonName>(X0, X3, X4).");
//        ConjunctiveQuery query = DlgpParser.parseQuery("?(X0,X1,X2,X3,X4) :- <Persons>(X0, X1, X2, X3, X4).");
        writer.write("\n= Query =\n");
        writer.write(query);

        Ontology onto = new DefaultOntology(new DlgpParser(new File(rootDir, ontoFilepath)));
        QueryRewriter rewriter = new PureRewriter();
        CloseableIteratorWithoutException<ConjunctiveQuery> it = rewriter.execute(query, onto);
        writer.write("\n= Rewritings =\n");
        while (it.hasNext()) {
            writer.write(it.next());
            writer.flush();
        }
        it.close();


        // 8 - query the KB and print answers
        writer.write("\n= Answers =\n");
        CloseableIterator<Substitution> results = kb.query(query);
        if (results.hasNext()) {
            do {
                writer.write(results.next());
            } while (results.hasNext());
        } else {
            writer.write("No answers.\n");
        }

        // 9 - close resources
        results.close();
        writer.close();
        kb.close();
    }

    public static void init() throws AtomSetException, SQLException, FileNotFoundException {
        System.out.print("initialization...");
        System.out.flush();

        // set the connection to the database
        Store naturalRDBMSStore = new NaturalRDBMSStore(new SqliteDriver(new File(rootDir, dbFilepath)));

        // encapsulate the store to filter prefix
        naturalRDBMSStore = new MappedStore(naturalRDBMSStore,
                new PrefixMapper("http://swat.cse.lehigh.edu/onto/univ-bench.owl#").inverse());

        // set the data directory
        File dir = new File(rootDir);

        // iterate over data files
        for (File file : dir.listFiles()) {

            // create a parser for the data file
            Parser<Object> parser = new DlgpParser(file);

            // parse and add data to database
            naturalRDBMSStore.addAll(new AtomFilterIterator(parser));

            // close the parser
            parser.close();
        }

        // close the database connection
        naturalRDBMSStore.close();
        System.out.println(" finished.");
    }

}
