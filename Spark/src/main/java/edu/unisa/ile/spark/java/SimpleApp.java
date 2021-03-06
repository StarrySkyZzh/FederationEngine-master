package edu.unisa.ile.spark.java;

/* SimpleApp.java */
import org.apache.spark.api.java.*;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.Function;

public class SimpleApp {
  public static void main(String[] args) {
    String logFile = "hdfs://130.220.210.127:8020/user/ile/document/milad.txt"; // Should be some file on your system
    
//    SparkConf conf = new SparkConf().setAppName("Remote Word Count").setMaster("spark://130.220.210.127:6066");
    SparkConf conf = new SparkConf().setAppName("Remote Word Count").setMaster("local");
    JavaSparkContext sc = new JavaSparkContext(conf);
    JavaRDD<String> logData = sc.textFile(logFile).cache();

    long numAs = logData.filter(new Function<String, Boolean>() {
      public Boolean call(String s) { return s.contains("a"); }
    }).count();

    long numBs = logData.filter(new Function<String, Boolean>() {
      public Boolean call(String s) { return s.contains("b"); }
    }).count();

    System.out.println("Lines with a: " + numAs + ", lines with b: " + numBs);
  }
}
