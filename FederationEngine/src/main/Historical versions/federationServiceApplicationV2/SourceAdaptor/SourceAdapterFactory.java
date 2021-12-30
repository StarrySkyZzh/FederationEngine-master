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
        Adapter r = null;
        switch (s) {
            case "es":
                r = new ESAdapter();
                break;
            case "promis":
                r = new PromisAdapter();
                break;
        }
        return r;
    }
}
