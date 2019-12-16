package test

import Model._
import org.scalatest.Succeeded
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor2}

import scala.annotation.tailrec
import org.scalatest.{Matchers, WordSpec}

class ModelSpec extends WordSpec with Matchers with TableDrivenPropertyChecks {

  val systemEuroSuccesses: TableFor2[Set[Int], Set[Int]] =
    Table(
      ("fields", "starFields"),
      (Set(1,2,32,34,5,45,43,42), Set(2,3,6)),
      (Set(1,2,32,34,5), Set(2,3,6)),
      (Set(1,2,32,34,5), Set(2,4)),
      (Set(1,2,32,34,5), Set(3,7,5,4,2)),
      (Set(1,2,32,34,5,34,22,44,3,7), Set(3,7,5,4,2))
    )

  val systemEuroFailures: TableFor2[Set[Int], Set[Int]] =
    Table(
      ("fields", "starFields"),
      (Set(1,2,32,34,5,45,43,42,22,11,12), Set(2,3,5)),
      (Set(1,2,32,34,5,45,43,42,22,11), Set(3,4,5,9,7,2)),
      (Set(1,2,32,34,5,45,43,42,22,11,55), Set(1,2,3,4,5,6)),
      (Set(1,2,32,34), Set(1,2,3,4,5,6)),
      (Set(1,2,32,34), Set(1,2,3,4,5)),
      (Set(1,2,32,34), Set(1)),
      (Set(1,2,32,34,5,45,43,42,22,11), Set(1)),
      (Set(1,2,32,34,55), Set(1,4,5)),
      (Set(1,2,32,34,50), Set(1,4,5,13))
    )

  val euroSuccesses: TableFor2[Set[Int], Set[Int]] =
    Table(
      ("fields", "starFields"),
      (Set(1,2,32,34,5), Set(2,11))
    )

  val euroFailures: TableFor2[Set[Int], Set[Int]] =
    Table(
      ("fields", "starFields"),
      (Set(1,2,32,34,5,45), Set(2,3)),
      (Set(1,2,32,34), Set(2,3)),
      (Set(1,2,32,34,5), Set(3)),
      (Set(1,2,32,34,5), Set(1,2,3)),
      (Set(1,2,32,34,50), Set(1,13)),
      (Set(1,2,32,34,51), Set(1,11))
    )

  "Model" should {
    forAll(systemEuroSuccesses) { (fields, starFields) =>
      s"SystemEuroTicket finds all combinations for $fields and $starFields of SystemEuroTickets" in new TestScope {
        SystemEuroTicket.allCombinations(SystemEuroTicket(fields, starFields)) should have size calculateCombinations(fields, starFields)
      }
    }

    forAll(systemEuroFailures) { (fields, starFields) =>
      s"SystemEuroTicket cannot be created for $fields and $starFields of SystemEuroTickets" in new TestScope {
        intercept[IllegalArgumentException] { SystemEuroTicket(fields, starFields) }
      }
    }

    forAll(euroSuccesses) { (fields, starFields) =>
      s"EuroTicket can be created if we satisfy our assumtions for $fields and $starFields" in new TestScope {
        EuroTicket(fields, starFields)
        Succeeded
      }
    }

    forAll(euroFailures) { (fields, starFields) =>
      s"EuroTicket cannot be created for $fields and $starFields of SystemEuroTickets" in new TestScope {
        intercept[IllegalArgumentException] { EuroTicket(fields, starFields) }
      }
    }
  }

  class TestScope {

    @tailrec
    private def factorial(acc: Long, n: Long): Long = {
      if (n == 0) acc
      else factorial(n * acc, n - 1)
    }

    //Amount of combinations we can calculate as
    // Cmn = n! / ((n−m)!⋅m!)
    private def combinations(n: Long, k: Long): Long = {
      factorial(1, n) / (factorial(1, n - k) * factorial(1, k))
    }

    def calculateCombinations(fields: Set[Int], starFields: Set[Int]): Long = {
      val fieldsCombinations = combinations(fields.size, EuroTicket.amountOfSelectedFields)
      val starFieldsCombinations = combinations(starFields.size, EuroTicket.amountSelectedStarFields)
      fieldsCombinations * starFieldsCombinations
    }

  }
}
