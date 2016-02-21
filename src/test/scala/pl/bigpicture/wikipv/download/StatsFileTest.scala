package pl.bigpicture.wikipv.download

import java.util.Calendar

import org.scalatest.FunSuite

/**
  * Created by kuba on 18/02/16.
  */
class StatsFileTest extends FunSuite {

  test("test that file is available to download") {

    val cal = Calendar.getInstance()
    cal.set(Calendar.YEAR, 2016)
    cal.set(Calendar.MONTH, 1)
    cal.set(Calendar.DAY_OF_MONTH, 15)
    cal.set(Calendar.HOUR_OF_DAY, 6)
    val f = StatsFile(cal)
    assert(f.availableOnWikipedia == true)

  }

  test("non existing file") {
    val cal = Calendar.getInstance()
    cal.set(Calendar.YEAR, 2011)
    cal.set(Calendar.MONTH, 1)
    cal.set(Calendar.DAY_OF_MONTH, 23)
    cal.set(Calendar.HOUR_OF_DAY, 19)
    val oldFile =  StatsFile(cal)
    assert(oldFile.availableOnWikipedia == false)

    cal.set(Calendar.YEAR, 2020)
    cal.set(Calendar.MONTH, 1)
    cal.set(Calendar.DAY_OF_MONTH, 20)
    cal.set(Calendar.HOUR_OF_DAY, 6)
    val futureFile = StatsFile(cal)
    assert(futureFile.availableOnWikipedia == false)
  }
}
