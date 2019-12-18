package task

import Model._
import org.scalatest.Succeeded
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor2}
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import scala.annotation.tailrec

class ModelSpec extends AnyWordSpec with Matchers with TableDrivenPropertyChecks {

  val systemSuccesses: TableFor2[Set[Int], Set[Int]] =
    Table(
      ("fields", "additionalFields"),
      (Set(1,2,32,34,5,45,43,42), Set(2,3,6)),
      (Set(1,2,32,34,5), Set(2,3,6)),
      (Set(1,2,32,34,5), Set(2,4)),
      (Set(1,2,32,34,5), Set(3,7,5,4,2)),
      (Set(1,2,32,34,5,34,22,44,3,7), Set(3,7,5,4,2))
    )

  val systemFailures: TableFor2[Set[Int], Set[Int]] =
    Table(
      ("fields", "additionalFields"),
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

  val simpleSuccesses: TableFor2[Set[Int], Set[Int]] =
    Table(
      ("fields", "additionalFields"),
      (Set(1,2,32,34,5), Set(2,11))
    )

  val simpleFailures: TableFor2[Set[Int], Set[Int]] =
    Table(
      ("fields", "additionalFields"),
      (Set(1,2,32,34,5,45), Set(2,3)),
      (Set(1,2,32,34), Set(2,3)),
      (Set(1,2,32,34,5), Set(3)),
      (Set(1,2,32,34,5), Set(1,2,3)),
      (Set(1,2,32,34,50), Set(1,13)),
      (Set(1,2,32,34,51), Set(1,11))
    )

  "Model" should {
    forAll(systemSuccesses) { (fields, additionalFields) =>
      s"SystemTicket finds all combinations for $fields and $additionalFields of SystemTickets" in new TestScope {
        SystemTicket.allCombinations(SystemTicket(fields, additionalFields)) should have size calculateCombinations(fields, additionalFields)
      }
    }

    forAll(systemFailures) { (fields, additionalFields) =>
      s"SystemTicket cannot be created for $fields and $additionalFields of SystemTickets" in new TestScope {
        intercept[IllegalArgumentException] { SystemTicket(fields, additionalFields) }
      }
    }

    forAll(simpleSuccesses) { (fields, additionalFields) =>
      s"SimpleTicket can be created if we satisfy our assumtions for $fields and $additionalFields" in new TestScope {
        SimpleTicket(fields, additionalFields)
        Succeeded
      }
    }

    forAll(simpleFailures) { (fields, additionalFields) =>
      s"SimpleTicket cannot be created for $fields and $additionalFields of SystemTickets" in new TestScope {
        intercept[IllegalArgumentException] { SimpleTicket(fields, additionalFields) }
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

    def calculateCombinations(fields: Set[Int], additionalFields: Set[Int]): Long = {
      val fieldsCombinations = combinations(fields.size, SimpleTicket.amountOfSelectedFields)
      val additionalFieldsCombinations = combinations(additionalFields.size, SimpleTicket.amountSelectedAdditionalFields)
      fieldsCombinations * additionalFieldsCombinations
    }
  }
}
