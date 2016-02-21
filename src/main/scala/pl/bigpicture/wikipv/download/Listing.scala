package pl.bigpicture.wikipv.download

import java.io.FileNotFoundException
import java.util.Calendar

import org.apache.log4j.Logger

import scala.io.Source


/**
  * Wikipedia site with listing of avaialable logs...
  */
case class Listing(year: Int, month: Int) {

  val listingUrl = "https://dumps.wikimedia.org/other/pagecounts-all-sites/%04d/%04d-%02d/".format(year, year, month)

  val html = Source.fromURL(listingUrl).mkString

  def fileIsAvailable(file: StatsFile) = html.contains(file.fileName)

}


object Listings {

  val logger = Logger.getLogger(Listings.getClass)

  var downloadedSites = Map[Int, Listing]()


  /**
    * Get listings for given year-month calendar
    *
    * @param cal
    * @return
    */
  def site(cal: Calendar): Option[Listing] = {
    val key = cal.get(Calendar.YEAR) * 100 + cal.get(Calendar.MONTH) +1

    if (downloadedSites.contains(key)) Some(downloadedSites.get(key).get)
    else {
      try {
        val site = Listing(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) +1)
        downloadedSites + (key -> site)
        Some(site)
      } catch {
        case e: FileNotFoundException => {
          logger.info("Unable to download listing for: %d".format(key))
          None
        }
      }
    }
  }



  /**
    * Removes cache
    */
  def invalidate = downloadedSites = Map[Int, Listing]()

}
