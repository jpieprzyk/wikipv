package pl.bigpicture.wikipv

import org.apache.commons.net.ftp.FTPSClient

/**
  * Created by kuba on 20/02/16.
  */
object Uploader {

  def upload(file: String): Unit = {

    val ftp = new FTPSClient()

    ftp.connect(Settings.ftpServer)
    ftp.login(Settings.ftpUser, Settings.ftpPassword)
    
  }

}
