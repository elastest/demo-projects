package org.sparkexample;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Tuple2;
import java.util.Arrays;
import static com.google.common.base.Preconditions.checkArgument;


public class WordCountTask {
  public static void main(String[] args) {
    checkArgument(args.length > 0, "Please provide the path of input file as first parameter.");
    new WordCountTask().run(args[0]);
  }

  
  public void run(String inputFilePath) {
    SparkConf conf = new SparkConf()
        .setAppName(WordCountTask.class.getName());
    JavaSparkContext context = new JavaSparkContext(conf);

    /*
     * Performs a work count sequence of tasks and prints the output with a logger.
     */
    context.textFile(inputFilePath)
        .flatMap(text -> Arrays.asList(text.split(" ")).iterator())
        .mapToPair(word -> new Tuple2<>(word, 1))
        .reduceByKey((a, b) -> a + b).saveAsTextFile("file:///out.txt");
  }
}
