package edu.unisa.ile.spark.java;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;

import scala.Tuple2;

public class RemoteWordCount {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		SparkConf conf = new SparkConf().setAppName("Remote Word Count").setMaster("spark://130.220.210.127:6066");
		SparkConf conf = new SparkConf().setAppName("Remote Word Count").setMaster("local");
		JavaSparkContext sc = new JavaSparkContext(conf);

		JavaRDD<String> textFile = sc.textFile("hdfs://130.220.210.127:8020/user/ile/document/milad.txt");

		JavaPairRDD<String, Integer> counts = textFile
            .flatMap(s -> {
                s = s.replace("\"", "").replace(".", "").replace(",", "").replace("(","").replace(")","").replace(" - "," ");
                System.out.println(s);
                List<String> list = Arrays.asList(s.split(" "));
                return list.iterator();
            })
            .mapToPair(word -> {
                Tuple2<String,Integer> x = new Tuple2<>(word,1);
                System.out.println(x._1());
                return x;
            })
            .reduceByKey((a, b) -> {
            return a + b;
            });
//		counts.saveAsTextFile("hdfs://130.220.210.127:8020/user/ile/document/testResult");
		counts.saveAsTextFile("testResult.txt");
//		counts.saveAsObjectFile("testResultOF.txt");
        sc.close();
	}

}
