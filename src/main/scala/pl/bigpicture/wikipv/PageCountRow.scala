package pl.bigpicture.wikipv

import java.net.URLDecoder

/**
  * Describes one line of pagecounts-...gz log files from Wikipedia.
  *
  */
case class PageCountRow(row: String) {

  val fields = row.split(" ")

  /**
    * Project id string, for example "en" or "en.m"
    *
    * See: https://dumps.wikimedia.org/other/pagecounts-all-sites/README.txt
    *
    */
  def projectStr = fields(0)

  /**
    * Array of project words. So for example project "en.m" will be
    * presented here as "en", "m"
    *
    * @return
    */
  def projectParts = projectStr.split("\\.")



  /**
    * Page name string, sometimes can be prefixed by project name or page type,
    * for example "Wikinews:Breaking_news"
    *
    * This string has special charactes escaped (like URL)
    */
  def articleStr = fields(1)


  /**
    * Article with special character unescaped
    */
  def article = {
    val withSpaces = articleStr.replace("_", " ")
    try {
      URLDecoder.decode(withSpaces, "UTF-8")
    } catch {
      case illegal: IllegalArgumentException => withSpaces
    }
  }

  /**
    * Gets the Wiki project, like "wikipedia", "wikibooks", or "wiktionary"...
    */
  def project = {
    if (projectParts.length == 1) "wikipedia"
    else if (projectParts.length > 1 && projectParts(0).length <= 3 && projectParts(1) == "m") "wikipedia"
    else "other"
    // other project currently not supported
  }

  /**
    * True if this is Wikipedia page count, not other project (like wikisource, wiktionary, ...)
 *
    * @return
    */
  def isWikipedia = {
    projectParts.length == 1 ||       // so only language indicator "en"
      ( projectParts.length == 2 && projectParts(0).length <= 3 && projectParts(1) == "m")
                                      // or language followed by ".m"
  }

  /**
    * Return language code. For example "en" for English, "de" for German...
    */
  def lang = projectParts(0)


  def pageViews = fields(2).toInt


  def isValidPage = {
    ! (
        articleStr == "Main_Page"
      || articleStr.contains(":")
      || articleStr == "index.html"
      )
  }

}
