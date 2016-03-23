package pl.bigpicture.wikipv.job

import java.io.File
import java.net.ConnectException
import java.util.Calendar

import pl.bigpicture.wikipv.download.{Listings, StatsFile}
import org.apache.log4j.Logger
import org.apache.spark.{SparkConf, SparkContext}
import pl.bigpicture.wikipv.write.JsonWriter
import pl.bigpicture.wikipv.{HourlyStats, PageCountRow, Settings}

/**
  * This job is intedend to be started once and periodically check
  * for new log files on Wikipedia, download them and process.
  *
  */
object ContinuousJob {
  val logger = Logger.getLogger(ContinuousJob.getClass)


  def singleRun(sc: SparkContext) = {
    val now = Calendar.getInstance()

    val statsFile = (1 to Settings.checkHoursAgo)
      .map(h => {
        val c = Calendar.getInstance()
        c.setTime(now.getTime)
        c.add(Calendar.HOUR_OF_DAY, -h)
        c
      })
      .map(ts => StatsFile(ts))
      .map(f => {
        logger.info("Considering timestamp %s".format(f.timestamp))
        f
      })
      .filter(statsFile => !statsFile.isDownloaded)
      .map(f => {
        logger.info("Timestamp %s is not downloaded yet".format(f.timestamp))
        f
      })
      .filter(f => f.availableOnWikipedia)
      .map(f => {
        logger.info("Timestamp %s is available on Wikipedia".format(f.timestamp))
        f
      })
      .filter(f => !f.outputExists)
      .map(f => {
        logger.info("Timestamp %s was not processed before".format(f.timestamp))
        f
      })
      .headOption


    if (statsFile.isEmpty) {
      logger.info("No hours available to download at this time.")
    } else {
      logger.info("Downloading hour: %s".format(statsFile.get.timestamp))

      statsFile.get.download

      val rdd = sc.textFile(Settings.downloadsPath + File.separator + statsFile.get.fileName).map(line => PageCountRow(line))
      val topPages = HourlyStats.topPages(statsFile.get.timestamp.toInt, Settings.topN, rdd)

      JsonWriter.save(statsFile.get.cal, topPages, Settings.outputJsonPath + File.separator + statsFile.get.outputFile)

      import sys.process._

      "./upload_latest.sh" !
    }

    Listings.invalidate
    logger.info("Do the housekeeping")
    HousekeepingJob.execute

  }

  def main(args: Array[String]) {

    val sc = new SparkContext(new SparkConf())

    while (true) {

      try {
        singleRun(sc)
        logger.info("Waiting...")
        Thread.sleep(1000 * 60 * 1)

      } catch {
        case e: ConnectException => {
          logger.warn("Cannot Connect: ", e)
          val sleep = 180
          logger.warn("Sleeping for %d sec".format(sleep))
          Thread.sleep(1000 * sleep)
        }
      }

    }

  }
}
