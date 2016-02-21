package pl.bigpicture.wikipv.download

import java.io.FileOutputStream
import java.net.URL
import java.nio.channels.Channels
import java.util.Calendar

/**
  * Created by kuba on 14/02/16.
  */
object Downloader {

  def pageCountUrl(cal: Calendar) = {
//    val year = ts.toString.substring(0, 4)
//    val month = ts.toString.substring(4, 2)
//    val day = ts.toString.substring(6, 2)
//    val hour = ts.toString.substring(8, 2)
    "https://dumps.wikimedia.org/other/pagecounts-all-sites/%04d/%04d-%02d/pagecounts-%04d%02d%02d-%02d0000.gz"
      .format(cal.get(Calendar.YEAR),
        cal.get(Calendar.YEAR),
        cal.get(Calendar.MONTH) + 1,
        cal.get(Calendar.YEAR),
        cal.get(Calendar.MONTH) + 1,
        cal.get(Calendar.DAY_OF_MONTH))
  }

  def download(url: String, output: String): Unit = {
    val urlObj = new URL(url)
    val channel = Channels.newChannel(urlObj.openStream())
    val fos = new FileOutputStream(output)
    fos.getChannel.transferFrom(channel, 0, Long.MaxValue)
  }

}
