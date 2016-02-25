package pl.bigpicture.wikipv.write

import java.io.{File, PrintWriter}
import java.net.{URLEncoder, URL}
import java.util.Calendar

import org.apache.commons.lang3.StringEscapeUtils
import org.apache.spark.rdd.RDD
import pl.bigpicture.wikipv.PageStat

/**
  * Writes rdd results as Json to a local file.
  *
  */
object JsonWriter {

  def getTimestamp(cal: Calendar) = "%04d-%02d-%02d %02d:00".format(
    cal.get(Calendar.YEAR),
    cal.get(Calendar.MONTH) +1,
    cal.get(Calendar.DAY_OF_MONTH),
    cal.get(Calendar.HOUR_OF_DAY)
  )


  def save(cal: Calendar, rdd: RDD[PageStat], fileName: String): Unit = {

    val langs = rdd.map(ps => ps.lang).distinct()
      .collect.map(lang => "\"%s\" ".format(lang)).mkString(",")

    val langHeader = "\"langs\": [ %s ], ".format(langs)


    val content = rdd
      .map(ps => (ps.lang, ps))
      .groupByKey()
      .mapValues( iter => iter.toList.map(x =>
        """
          { "pageName": "%s", "url": "%s", "pv": %d, "lang": "%s" }
        """.format(x.pageTitleJson, x.pageURL, x.pv, x.lang) ).mkString(",\n"))
      .map(tuple =>
        """
          | "%s": [ %s ]
        """.stripMargin.format(tuple._1, tuple._2))
      .collect.mkString(",\n ")

    val result =
      """
        |{
        |%s
        |%s,
        |"lastUpdated": "%s"
        |}
      """.stripMargin.format(langHeader, content, getTimestamp(cal))

    val pw = new PrintWriter(new File(fileName))
    pw.print(result)
    pw.close()

  }

}
