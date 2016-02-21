package pl.bigpicture.wikipv

import java.util.Date

import org.apache.spark.{SparkConf, SparkContext}
import org.scalatest.FunSuite

/**
  * Created by kuba on 12/02/16.
  */
class HourlyStatsTest extends FunSuite {

  test("test1") {

    val stream = this.getClass.getResourceAsStream("/sample_pagecounts.txt")
    val b = scala.io.Source.fromInputStream(stream).getLines()

    val sc = new SparkContext("local", "test", new SparkConf())
    val rdd = sc.parallelize(b.toSeq).map(line => PageCountRow(line))

    val result = HourlyStats.topPages(999, 3, rdd)
    val resEn = result.filter(f => f.lang == "en").collect
    val resPl = result.filter(f => f.lang == "pl").collect

    assert(resEn.length == 3)
    assert(resEn(0).page == "article1")
    assert(resEn(1).page == "article2")
    assert(resEn(2).page == "article3")

    assert(resPl.length == 3)
    assert(resPl(0).page == "article1")
    assert(resPl(1).page == "article2")
    assert(resPl(2).page == "article3")

  }

}
