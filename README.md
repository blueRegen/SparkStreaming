# SparkStreamingPipeline
Kafka - Spark Streaming Transformer

Initialize an aws emr cluster with spark(2.1.0) and hbase (1.3.0)
Login

wget http://mirror.fibergrid.in/apache/kafka/0.10.2.0/kafka_2.11-0.10.2.0.tgz

tar -zvxf kafka_2.11-0.10.2.0.tgz 
cd SparkStreaminngPipeline-assembly-1.0.jar
bin/kafka-server-start.sh config/server.properties ## starts the kafka server
bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test 





Use assembly plugin to generate the artifacts (build.sbt is already configured)


copy the jar and asc file to home directory
At the home directory

java -jar SparkStreaminngPipeline-assembly-1.0.jar -- starts the kafka message producer. Will print out the messages

Use hbase shell to create a table t2 with column family cfkp

Exit hbase shell and be back in the home directory

spark-submit --class com.lxmt.spark.streaming.Transformer --master yarn-client SparkStreaminngPipeline-assembly-1.0.jar

This will start the streaming spark job

You can see the results if you take another terminal and scan t2 table in hbase.








