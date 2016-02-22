package pl.bigpicture.wikipv.job

import java.text.SimpleDateFormat
import java.util.Date

import org.scalatest.FunSuite

/**
  * Created by kuba on 21/02/16.
  */
class HousekeepingJobTest extends FunSuite {

  test("file with current timestamp should not be removed") {

    val sdf = new SimpleDateFormat("yyyyMMdd-HH")
    val file1 = "pagecounts-%s0000.gz".format(sdf.format(new Date()))

    // File with current timestamp should not be removed when
    // we keep 1, 2 or 100 day-history
    assert(!HousekeepingJob.shouldBeRemoved(file1, 1))
    assert(!HousekeepingJob.shouldBeRemoved(file1, 2))
    assert(!HousekeepingJob.shouldBeRemoved(file1, 100))

  }

  test("old file should be removed according to history setting") {

    val sdf = new SimpleDateFormat("yyyyMMdd-HH")
    // file name with 5 days ago timestamp
    val file1 = "pagecounts-%s0000.gz".format(sdf.format(new Date(new Date().getTime - 5 * 24 * 60 * 60 * 1000)))

    assert(HousekeepingJob.shouldBeRemoved(file1, 1))
    assert(HousekeepingJob.shouldBeRemoved(file1, 2))
    assert(HousekeepingJob.shouldBeRemoved(file1, 3))
    assert(HousekeepingJob.shouldBeRemoved(file1, 4))
    assert(!HousekeepingJob.shouldBeRemoved(file1, 5))
    assert(!HousekeepingJob.shouldBeRemoved(file1, 6))
    assert(!HousekeepingJob.shouldBeRemoved(file1, 10))

  }

}
