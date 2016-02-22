package pl.bigpicture.wikipv.download

import java.io.File
import java.net.URL
import java.util.Calendar

import org.apache.log4j.Logger
import pl.bigpicture.wikipv.Settings


/**
  * File with pagecount statistics.
  */
case class StatsFile(cal: Calendar) {

  val logger = Logger.getLogger(StatsFile.getClass)

  def timestamp = "%04d%02d%02d%02d".format(
    cal.get(Calendar.YEAR),
    cal.get(Calendar.MONTH) +1,
    cal.get(Calendar.DAY_OF_MONTH),
    cal.get(Calendar.HOUR_OF_DAY)
  )

  def fileName = "pagecounts-%04d%02d%02d-%02d0000.gz".format(
    cal.get(Calendar.YEAR),
    cal.get(Calendar.MONTH) +1,
    cal.get(Calendar.DAY_OF_MONTH),
    cal.get(Calendar.HOUR_OF_DAY)
    )

  val listingUrl = "https://dumps.wikimedia.org/other/pagecounts-all-sites/%04d/%04d-%02d/"
    .format(cal.get(Calendar.YEAR), cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1)

  def tempFileName = fileName + "_temp"

  /**
    * Is such file on our local directory?
    *
    * @return true if file exists
    */
  def isDownloaded = {
    val allDownloadedFiles = new File(Settings.downloadsPath).listFiles()
    allDownloadedFiles.exists(p => p.getName == fileName)
  }

  /**
    * Is the file for given hour available to be downlaoded.
    */
  def availableOnWikipedia = {
    val listing = Listings.site(cal)
    if (listing.isDefined) {
      listing.get.fileIsAvailable(this)
    }
    else false
  }

  def outputFile = "output_%s0000.json".format(timestamp)

  def outputExists = {
    val allFiles = new File(Settings.downloadsPath).listFiles()
    allFiles.exists(f => f.getName == outputFile)
  }


  def download: Unit = {
    import sys.process._
    val url = listingUrl + fileName
    new URL(url) #> new File(Settings.downloadsPath + File.separator + fileName) !!
  }


}
