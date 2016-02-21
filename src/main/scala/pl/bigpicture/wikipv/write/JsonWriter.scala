package pl.bigpicture.wikipv.write

import java.io.{File, PrintWriter}

import org.apache.spark.rdd.RDD
import pl.bigpicture.wikipv.PageStat

/**
  * Created by kuba on 14/02/16.
  */
object JsonWriter {

  def save(rdd: RDD[PageStat], fileName: String): Unit = {

    val langs = rdd.map(ps => ps.lang).distinct()
      .collect.map(lang => "\"%s\" ".format(lang)).mkString(",")

    val langHeader = "\"langs\": [ %s ], ".format(langs)


    val content = rdd
      .map(ps => (ps.lang, ps))
      .groupByKey()
      .mapValues( iter => iter.toList.map(x =>
        """
          { "pageName": "%s", "url": "%s", "pv": %d, "lang": "%s" }
        """.format(x.page, x.pageURL, x.pv, x.lang) ).mkString(",\n"))
      .map(tuple =>
        """
          | "%s": [ %s ]
        """.stripMargin.format(tuple._1, tuple._2))
      .collect.mkString(",\n ")

    val result =
      """
        |{
        |%s
        |%s
        |}
      """.stripMargin.format(langHeader, content)

    println(result)

    val pw = new PrintWriter(new File(fileName))
    pw.print(result)
    pw.close()

  }

}
