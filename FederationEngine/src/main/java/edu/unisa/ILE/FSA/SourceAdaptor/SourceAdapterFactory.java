package edu.unisa.ILE.FSA.SourceAdaptor;

/**
 * Created by wenhaoli on 22/06/2017.
 */
public class SourceAdapterFactory {

//    public static void main(String[] args){
//        Adapter a = build("ES");
//        System.out.println(a.getClass());
//        a = build("promis");
//        System.out.println(a.getClass());
//    }

    public static Adapter build(String adaptor) {
        String s = adaptor.toLowerCase();
        Adapter a = null;
        System.out.println(s);
        switch (s) {
            case "es":
                a = new ESAdapter();
                break;
            case "promis":
                a = new PromisAdapter();
                break;
            case "poler":
                a = new PolerAdapter();
                break;
            case "lei":
                a = new LEIAdapter();
                break;
            case "hdfs":
                a = new HDFSAdapter();
                break;
            case "obdapostgresql":
                a = new RDBMSAdapter(s,"postgresql");
                break;
            case "obdasqlite":
                a = new RDBMSAdapter(s,"sqlite");
                break;
        }
        return a;
    }
}
