package pl.bigpicture.wikipv

import org.apache.spark.rdd.RDD

/**
  * Get top pages for log file from 1 hour.
  */
object HourlyStats {


  /**
    * Gets the RDD of the top-n pages
    *
    * @param topn n (top-n) of pages that will be in each language
    * @param rdd Tuple of: project, page, pv
    */
  def topPages(ts: Int, topn: Int, rdd: RDD[PageCountRow]) = {

    rdd.filter(row => row.isWikipedia)
      .filter(row => row.isValidPage)
      .filter(row => List("pl", "en", "de", "ru", "fr", "es", "pt", "tr", "ar").contains(row.lang))
      .map(row => ((row.lang, row.article), row.pageViews))
      .reduceByKey( _ + _ )
      .map( tuple => (tuple._1._1, (tuple._1._2, tuple._2)))
      .groupByKey()
      .mapValues(iter => iter.toList.sortWith( (a,b) => a._2 > b._2).slice(0, topn).iterator)
      .flatMap( tuple => tuple._2.map( pair => PageStat(pair._1, tuple._1, pair._2, ts)) )

  }

}
