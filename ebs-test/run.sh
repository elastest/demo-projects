#this works too
mvn package

#get the big file
wget https://norvig.com/big.txt


#clean the pre-existing file
hadoop fs  -rmr /out.txt

spark-submit --class org.sparkexample.WordCountTask --master spark://spark-master:7077 /demo-projects/ebs-test/target/hadoopWordCount-1.0-SNAPSHOT.jar /demo-projects/ebs-test/big.txt

hadoop fs -getmerge /out.txt ./out.txt
head -10 out.txt
