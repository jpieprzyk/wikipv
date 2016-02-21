package pl.bigpicture.wikipv.job

import java.io.File
import java.util.Calendar

import _root_.pl.bigpicture.wikipv.download.StatsFile
import org.apache.log4j.Logger
import org.apache.spark.{SparkConf, SparkContext}
import pl.bigpicture.wikipv.write.JsonWriter
import pl.bigpicture.wikipv.{HourlyStats, PageCountRow, Settings}

/**
  * Created by kuba on 19/02/16.
  */
object OneHourJob {

  val logger = Logger.getLogger(OneHourJob.getClass)

  def main(args: Array[String]) {

    val ts = args(0)

    val cal = Calendar.getInstance()
    cal.set(Calendar.YEAR, ts.substring(0, 4).toInt)
    cal.set(Calendar.MONTH, ts.substring(4, 6).toInt -1)
    cal.set(Calendar.DAY_OF_MONTH, ts.substring(6, 8).toInt)
    cal.set(Calendar.HOUR_OF_DAY, ts.substring(8, 10).toInt)

    val file = StatsFile(cal)

    if (!file.isDownloaded) {
      logger.info("File not downloaded for %s".format(file.timestamp))

      if (file.availableOnWikipedia) {
        logger.info("File %s available on wikipedia".format(file.timestamp))
        file.download
      }
    }

    if (file.isDownloaded) {
      logger.info("File downloaded for hour %s".format(ts))

      val sc = new SparkContext(new SparkConf())

      val rdd = sc.textFile(Settings.downloadsPath + File.separator + file.fileName).map(r => PageCountRow(r))
      val topPages = HourlyStats.topPages(file.timestamp.toInt, 10, rdd)

      JsonWriter.save(file.cal, topPages, Settings.outputJsonPath + File.separator + "output_%s.json".format(file.timestamp))

    } else {
      logger.info("No file to process for hour %s".format(ts))
    }



  }
}
