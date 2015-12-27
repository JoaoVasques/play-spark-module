package play.modules.io.joaovasques.playspark.tests

import com.typesafe.config.{Config, ConfigFactory}
import org.apache.spark._
import scala.util.Try
import java.util.{Random, Date}
import play.modules.io.joaovasques.playspark.api.SparkJob

/**
 * A long job for stress tests purpose.
 * Iterative and randomized algorithm to compute Pi.
 * Imagine a square centered at (1,1) of length 2 units.
 * It tightly encloses a circle centered also at (1,1) of radius 1 unit.
 * Randomly throw darts to them.
 * We can use the ratio of darts inside the square and circle to approximate the Pi.
 *
 * stress.test.longpijob.duration controls how long it run in seconds.
 * Longer duration increases precision.
 *
 */
private[tests] class LongPiJob extends SparkJob {
  private val rand = new Random(now)

  override def runJob(sc: SparkContext): Any = {
    val duration = 5
    var hit:Long = 0
    var total:Long = 0
    val start = now
    while(stillHaveTime(start, duration)) {
      val counts = estimatePi(sc)
      hit = hit + counts._1
      total = total + counts._2
    }

    (4.0 * hit) / total
  }

  /**
   *
   * @param sc
   * @return (hit, total) where hit is the count hit inside circle and total is the total darts
   */
  private def estimatePi(sc: SparkContext): Tuple2[Int, Int] = {
    val data = Array.iterate(0, 1000)(x => x + 1)

    val dd = sc.parallelize(data)
    dd.map { x =>
      // The first value is the count of hitting inside the circle. The second is the total.
      if (throwADart()) (1, 1) else (0, 1)
    }.reduce { (x, y) => (x._1 + y._1, x._2 + y._2) }
  }

  /**
   * Throw a dart.
   *
   * @return true if the dart hits inside the circle.
   */
  private def throwADart(): Boolean = {
    val x = rand.nextDouble() * 2
    val y = rand.nextDouble() * 2
    // square of distance to center
    val dist = math.pow(x - 1, 2) + math.pow(y - 1, 2)
    // square root wouldn't affect the math.
    // if dist > 1, then hit outside the circle, else hit inside the circle
    dist <= 1
  }

  private def now(): Long = (new Date()).getTime

  private val OneSec = 1000 // in milliseconds
  private def stillHaveTime(startTime: Long, duration: Int): Boolean = (now - startTime) < duration * OneSec
}
