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


  /**
    * Number of pageviews
    *
    * @return
    */
  def pageViews = fields(2).toInt


  /**
    * There are some articles that should be filtered like
    * index.html or Main_Page. We don't want them in our
    * results.
    *
    * TODO: This depends on language, so probably it should
    * be more complicated than this simple version
    *
    * @return true if this row describes article that we want
    *         to include in stats.
    *         false if it should be ignored
    */
  def isValidPage = {
    ! (
        articleStr == "Main_Page"
      || articleStr == "Hauptseite"
      || articleStr == "%D0%97%D0%B0%D0%B3%D0%BB%D0%B0%D0%B2%D0%BD%D0%B0%D1%8F_%D1%81%D1%82%D1%80%D0%B0%D0%BD%D0%B8%D1%86%D0%B0"
      || articleStr == "%D8%A7%D9%84%D8%B5%D9%81%D8%AD%D8%A9_%D8%A7%D9%84%D8%B1%D8%A6%D9%8A%D8%B3%D9%8A%D8%A9"
      || articleStr == "Ana_Sayfa"
      || articleStr == "Strona_g%C5%82%C3%B3wna"
      || articleStr.contains(":")
      || articleStr == "index.html"
      || articleStr == "_"
      || articleStr.endsWith(".jpg")
      || articleStr.endsWith(".php")
      || articleStr.endsWith(".html")
      || articleStr.length == 0
      )
  }

}
