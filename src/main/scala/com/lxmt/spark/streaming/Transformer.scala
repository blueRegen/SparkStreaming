package com.lxmt.spark.streaming

import _root_.kafka.serializer.{DefaultDecoder, StringDecoder}
import org.apache.avro.Schema
import org.apache.avro.generic.GenericRecord
import org.apache.avro.io.{DecoderFactory, Decoder, DatumReader}
import org.apache.avro.specific.SpecificDatumReader
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.Put
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.mapred.JobConf
import org.apache.spark.SparkConf
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.hadoop.hbase.mapred.TableOutputFormat

import scala.io.Source


/**
 * Created by ravi on 20/03/2017.
 */

object Transformer {

  def main (args: Array[String]){
      val conf = new SparkConf().setMaster("local[2]").setAppName("NetworkWordCount")
      val ssc = new StreamingContext(conf, Seconds(2))
      val kafkaParams = Map[String, String]("metadata.broker.list" -> "localhost:9092")
      val hbaseConf = HBaseConfiguration.create()
      hbaseConf.set(TableOutputFormat.OUTPUT_TABLE, "t2")
      val jobConfig: JobConf = new JobConf(hbaseConf, this.getClass)
      //jobConfig.set("mapreduce.output.fileoutputformat.outputdir", "/user/user01/out")
      jobConfig.setOutputFormat(classOf[TableOutputFormat])
      jobConfig.set(TableOutputFormat.OUTPUT_TABLE, "t2")

      val kafkaStream = KafkaUtils.createDirectStream[String,Array[Byte],StringDecoder,DefaultDecoder](ssc,kafkaParams,Set("test"))
      kafkaStream.foreachRDD{ rdd =>
         val records= rdd.map{ p =>
           val byteArray = p._2
           getRecord(byteArray)
         }
        val streamTime=System.currentTimeMillis()+""
        val streamTimeKpi=records.map( record => ("id_"+streamTime,record.kpi))
        streamTimeKpi.reduceByKey((kpi1,kpi2) => scala.math.max(kpi1,kpi2)).map(getHbasePut(_)).saveAsHadoopDataset(jobConfig)
      }
      ssc.start()
      ssc.awaitTermination()
    }


  private def getRecord(message: Array[Byte]):Record = {
        val schemaString = Source.fromFile("schema.asc").mkString
        val schema: Schema = new Schema.Parser().parse(schemaString)
        val reader: DatumReader[GenericRecord] = new SpecificDatumReader[GenericRecord](schema)
        val decoder: Decoder = DecoderFactory.get().binaryDecoder(message, null)
        val recordData: GenericRecord = reader.read(null, decoder)
        val record = Record(recordData.get("id").toString, recordData.get("category").toString,recordData.get("kpi").toString.toInt)
        record
      }

  private def getHbasePut(data:(String,Int)): (ImmutableBytesWritable, Put) ={
        println(data)
        val rowKey     = data._1
        val put        = new Put(Bytes.toBytes(rowKey))
        val idBytes    = Bytes.toBytes("id")
        val maxBytes   = Bytes.toBytes("maxKpi")
        val cf         = Bytes.toBytes("cfkp")
        put.addColumn(cf,idBytes,Bytes.toBytes(rowKey))
        put.addColumn(cf,maxBytes,Bytes.toBytes(data._2.toString))
        (new ImmutableBytesWritable(Bytes.toBytes(rowKey)), put)
      }

  case class Record(id:String,category:String,kpi:Int)

}
