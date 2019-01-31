/*
 * (C) Copyright 2017-2019 ElasTest (http://elastest.io/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */


package org.sparkexample;
import com.google.common.collect.ImmutableList;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
//import org.elasticsearch.spark.rdd.api.java.JavaEsSpark;
import static org.elasticsearch.spark.rdd.api.java.JavaEsSpark.*;
import scala.Tuple2;

import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;


public class WordCountTask {
  public static int main(String[] args) {
    checkArgument(args.length > 0, "Please provide the path of input file as first parameter.");
    int ret = new WordCountTask().run(args[0], args[1]);
    return ret;
  }



  public int run(String input, String esindex){
    SparkConf conf = new SparkConf()
            .setAppName(WordCountTask.class.getName());
    conf.set("spark.es.index.auto.create", "true");
    conf.set("spark.es.nodes", "elastest_edm-elasticsearch_1");
    JavaSparkContext context = new JavaSparkContext(conf);
    JavaRDD<Map<String, Object>> esRDD = esRDD(context, esindex, "?q=*").values();
    //esRDD.take(100).forEach(System.out::println);
    //esRDD.take(100).forEach(p -> );


    List<Object> rddItems = new ArrayList<>();
    esRDD.collect().forEach(i -> {
      for(Map.Entry<String, Object> miRdd : i.entrySet() ){
        if(miRdd.getKey().equals("message") && miRdd.getValue() != null && miRdd.getValue().toString().matches(input)){
          rddItems.add(miRdd.getValue());
        }
      }
    });
    System.out.println("list_size: " + rddItems.size());

    //rddItems.stream().forEach(i -> System.out.println(i));

/*

    List<Map<String, Object>> lmap = esRDD.collect();

    for(Map<String, Object> item : lmap){
      for(Map.Entry<String, Object> mItem : item.entrySet()){
        if (mItem.getKey().equals("message") && mItem.getValue() == null){//safety check
          System.out.println("id: " + mItem.getKey().equals("id"));
        }
        if( mItem.getKey().equals("message") && mItem.getValue().toString().matches(input) ){
          continue;
    //      System.out.println("NEW MESSAGE: " +  mItem.getValue());
        }

      }
    }
*/



  return rddItems.size();
  }
}



















