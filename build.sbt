name := "SparkStreaminngPipeline"

version := "1.0"

scalaVersion := "2.11.5"

libraryDependencies ++= Seq(
  "org.apache.spark" % "spark-core_2.11" % "2.1.0" ,
  "org.apache.spark" % "spark-streaming_2.11" % "2.1.0" ,
  "org.apache.spark" % "spark-streaming-kafka-0-8_2.11" % "2.1.0",
  "org.apache.avro" % "avro" % "1.8.0",
  "com.twitter" % "bijection-avro_2.10" % "0.9.2",
  "org.apache.hbase" % "hbase-client" % "1.2.4",
  "org.apache.hbase" % "hbase-common" % "1.2.4",
  "org.apache.hbase" % "hbase-server" % "1.2.4"
)