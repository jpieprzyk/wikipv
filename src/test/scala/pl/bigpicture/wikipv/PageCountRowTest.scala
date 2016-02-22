package pl.bigpicture.wikipv

import org.scalatest.FunSuite

/**
  * Created by kuba on 12/02/16.
  */
class PageCountRowTest extends FunSuite {


  test("field parsing") {
    val row = PageCountRow("en Main_Page 4050 234234")
    assert(row.lang == "en")
    assert(row.articleStr == "Main_Page")
    assert(row.pageViews == 4050)
  }

  test("get lang") {
    val row = PageCountRow("pl.m Main_Page 4050 234234")
    assert(row.lang == "pl")
  }

  test("is wikipedia") {
    assert(PageCountRow("pl.m Main_Page 4050 234234").isWikipedia)
    assert(PageCountRow("pl Main_Page 4050 234234").isWikipedia)
    assert(!PageCountRow("commons.m Main_Page 4050 234234").isWikipedia)
    assert(!PageCountRow("en.q Main_Page 4050 234234").isWikipedia)
    assert(!PageCountRow("en.m.voy Main_Page 4050 234234").isWikipedia)
  }

}
