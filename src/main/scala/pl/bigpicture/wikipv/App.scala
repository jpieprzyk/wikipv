package pl.bigpicture.wikipv


import com.datastax.spark.connector.SomeColumns
import org.apache.spark.{SparkConf, SparkContext}
import com.datastax.spark.connector._
import pl.bigpicture.wikipv.write.JsonWriter


/**
  * Created by kuba on 12/02/16.
  */
object App {

  def main(args: Array[String]) {

    val sparkConf = new SparkConf(true).set("spark.cassandra.connection.host", "127.0.0.1")

    val sc = new SparkContext(sparkConf)

    val fileName = args(0)

    val pageCounts = sc.textFile(fileName).map(line => PageCountRow(line))

    val topPages = HourlyStats.topPages(9999, 10, pageCounts)

    JsonWriter.save(topPages, "output.txt")

  }
}
