package pl.bigpicture.wikipv.job

import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

import org.apache.log4j.Logger
import pl.bigpicture.wikipv.Settings

/**
  * Do various housekeeping tasks.
  *
  * Currently just removes old files.
  */
object HousekeepingJob {

  val logger = Logger.getLogger(HousekeepingJob.getClass)

  val r = "pagecounts-([0-9]{8}-[0-9]{2})0000.gz".r
  val dateParser = new SimpleDateFormat("yyyyMMdd-HH")


  def shouldBeRemoved(fileName: String, keepDays: Int): Boolean = {
    fileName match {
      case r(group) => {
        val date = dateParser.parse(group)
        val now = new Date()

        (now.getTime - date.getTime) / 1000 / 60 / 60 > keepDays * 24
      }
      case _ => false
    }
  }

  def execute = {

    val downloadedFiles = new File(Settings.downloadsPath).listFiles()
    val keepDays = Settings.keepDownloadDays

    downloadedFiles
      .filter(file => {
        val toBeRemoved = shouldBeRemoved(file.getName, keepDays)
        if (toBeRemoved) logger.info("File %s should be removed".format(file.getName))
        else logger.info("Keeping file %s".format(file.getName))
        toBeRemoved
      })
      .foreach(file => {
        logger.info("Removing old file: %s".format(file.getName))
        file.delete()
      })

  }

}
