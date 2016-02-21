package pl.bigpicture.wikipv

import java.io.FileInputStream
import java.util.Properties

/**
  * Created by kuba on 18/02/16.
  */
object Settings {

  val prop = new Properties()
  prop.load(new FileInputStream("wikipv.properties"))

  def downloadsPath = prop.getProperty("download.dir")

  def outputJsonPath = prop.getProperty("output.dir")

  def checkHoursAgo = prop.getProperty("check.hours.ago").toInt

  def topN = prop.getProperty("top.n").toInt

  def ftpServer = prop.getProperty("ftp.server")
  def ftpUser = prop.getProperty("ftp.user")
  def ftpPassword = prop.getProperty("ftp.password")

  def keepDownloadDays = prop.getProperty("download.keep.days").toInt

}
