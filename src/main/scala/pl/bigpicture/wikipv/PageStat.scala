package pl.bigpicture.wikipv

import java.net.URLEncoder

/**
  * Created by kuba on 14/02/16.
  */
case class PageStat(page: String, lang: String, pv: Int, ts: Int) {

  def pageTitleURL = URLEncoder.encode(page.replace(" ", "_"), "UTF-8")

  /**
    * Get URL to Wikipedia article for given page and lang
    */
  def pageURL = "https://%s.wikipedia.org/wiki/%s".format(lang, pageTitleURL)

}
