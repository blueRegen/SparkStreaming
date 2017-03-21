package com.lxmt.kafka.producer

import java.io.ByteArrayOutputStream
import java.util.{UUID, Properties}


import kafka.producer.{ProducerConfig, Producer, KeyedMessage}
import org.apache.avro.Schema
import org.apache.avro.Schema.Parser
import org.apache.avro.generic.{GenericData, GenericRecord}
import org.apache.avro.io.{EncoderFactory, BinaryEncoder}
import org.apache.avro.specific.SpecificDatumWriter
import scala.io.Source

/**
 * Created by ravi on 20/03/2017.
 */
object KafkaProducer {

  val mySchema = Source.fromFile("schema.asc").mkString

  val props = new Properties()
  props.put("metadata.broker.list", "localhost:9092")
  props.put("message.send.max.retries", "5")
  props.put("request.required.acks", "-1")
  props.put("serializer.class", "kafka.serializer.DefaultEncoder")
  props.put("client.id", UUID.randomUUID().toString())

  val producer = new Producer[String, Array[Byte]](new ProducerConfig(props))
  val schema: Schema = new Parser().parse(mySchema)
  val writer = new SpecificDatumWriter[GenericRecord](schema)

 def main (args: Array[String]){
   while(true)
   {
     val out = new ByteArrayOutputStream()
     val encoder: BinaryEncoder = EncoderFactory.get().binaryEncoder(out, null)
     val genericUser: GenericRecord = new GenericData.Record(schema)
     //Put data in that generic record
     genericUser.put("id", System.currentTimeMillis()+"")
     genericUser.put("category", "signal")
     genericUser.put("kpi", scala.util.Random.nextInt(100))

     // Serialize generic record into byte array
     writer.write(genericUser, encoder)
     encoder.flush()
     out.close()

     val serializedBytes: Array[Byte] = out.toByteArray()

     val queueMessage = new KeyedMessage[String, Array[Byte]]("test", serializedBytes)
     producer.send(queueMessage)
     println("Sent message:"+genericUser)
     Thread.sleep(500)
   }

}



}
