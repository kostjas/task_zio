package test

import cats._
import cats.data.{ValidatedNel, _}
import cats.implicits._

import scala.util.Try

object Model {

  val maxSizeOfFields = 50
  val maxSizeOfStarFields = 11

  val permittedFields: Set[Int] = (1 to maxSizeOfFields).toSet
  val permittedStarFields: Set[Int] = (1 to maxSizeOfStarFields).toSet

  sealed trait TicketType
  trait Euro extends TicketType
  trait System extends TicketType
  trait Winning extends TicketType

  case class Ticket[T <: TicketType](fields: Set[Int], starFields: Set[Int])

  object WinningTicket {
    val amountOfSelectedFields = 5
    val amountSelectedStarFields = 2

    /**
      * assumptions:
      *
      * amount of selected fields can be 5
      * amount of selected star fields can be 2
      *
      * values of selected fields can be in scope of the range from 1 to 50
      * values of selected star fields can be in scope of the range from 1 to 11
      *
      */
    def apply(fields: Set[Int], starFields: Set[Int]): Ticket[Winning] = {
      require(fields.size == amountOfSelectedFields, s"Max selected Fields must be $amountOfSelectedFields")
      require(starFields.size == amountSelectedStarFields, s"Max selected StarFields must be $amountSelectedStarFields")
      require(fields.forall(permittedFields.contains),
        s"Selected Fields must be in scope of the range from 1 to $maxSizeOfFields")
      require(starFields.forall(permittedStarFields.contains),
        s"Selected StarFields must be in scope of the range from 1 to $maxSizeOfStarFields")

      new Ticket[Winning](fields, starFields)
    }

    implicit object WinningSingleTicketParser$ extends SingleTicketParser[Ticket[Winning]] {

      /**
        * assumption:
        *
        * format of the line is:
        * it contains two "space" separated elements, which elements are coma separated lists
        * first list represents fields and second one - star fields
        * Example
        * 1,2,32,34,5,45 2,4,5
        */
      override def parse(line: String): String Either Ticket[Winning] = {
        Try[Ticket[Winning]] {
          val lineContent = line.split(" ")
          require(lineContent.length == 2, "Line must contain two elements, separated by space!")
          val fields: Set[Int] = lineContent(0).split(",").map(_.toInt).toSet
          val starFields: Set[Int] = lineContent(1).split(",").map(_.toInt).toSet
          WinningTicket(fields, starFields)
        }.toEither.leftMap(_.getMessage)
      }
    }
  }

  object EuroTicket {

    val amountOfSelectedFields = 5
    val amountSelectedStarFields = 2

    /**
      * assumptions:
      *
      * amount of selected fields can be 5
      * amount of selected star fields can be 2
      *
      * values of selected fields can be in scope of the range from 1 to 50
      * values of selected star fields can be in scope of the range from 1 to 11
      *
      */
    def apply(fields: Set[Int], starFields: Set[Int]): Ticket[Euro] = {
      require(fields.size == amountOfSelectedFields, s"Max selected Fields must be $amountOfSelectedFields")
      require(starFields.size == amountSelectedStarFields, s"Max selected StarFields must be $amountSelectedStarFields")
      require(fields.forall(permittedFields.contains),
        s"Selected Fields must be in scope of the range from 1 to $maxSizeOfFields")
      require(starFields.forall(permittedStarFields.contains),
        s"Selected StarFields must be in scope of the range from 1 to $maxSizeOfStarFields")

      new Ticket[Euro](fields, starFields)
    }
  }

  object SystemEuroTicket {

    val minSelectedFields = 5
    val minSelectedStarFields = 2
    val maxSelectedFields: Int = 10
    val maxSelectedStarFields: Int = 5

    /**
      * assumptions:
      *
      * amount of selected fields can be in scope of the range from 5 to 10
      * amount of selected star fields can be in scope of the range from 2 to 5
      *
      * values of selected fields can be in scope of the range from 1 to 50
      * values of selected star fields can be in scope of the range from 1 to 11
      *
      */
    def apply(fields: Set[Int], starFields: Set[Int]): Ticket[System] = {
      val fieldsSize = fields.size

      require(minSelectedFields <= fieldsSize && fieldsSize <= maxSelectedFields,
        s"Amount of selected fields must be between $minSelectedFields and $maxSelectedFields ")
      val starFieldsSize = starFields.size

      require(minSelectedStarFields <= starFieldsSize && starFieldsSize <= maxSelectedStarFields,
        s"Amount of selected StarFields must be between $minSelectedStarFields and $maxSelectedStarFields ")

      require(fields.forall(permittedFields.contains),
        s"Selected Fields must be in scope of the range from 1 to $maxSizeOfFields")

      require(starFields.forall(permittedStarFields.contains),
        s"Selected StarFields must be in scope of the range from 1 to $maxSizeOfStarFields")

      new Ticket[System](fields, starFields)
    }

    def allCombinations(ticket: Ticket[System]): List[Ticket[Euro]] = {
      (ticket.fields.size, ticket.starFields.size) match {
        case (fieldsSize, starFieldsSize) if fieldsSize == minSelectedFields && starFieldsSize == minSelectedStarFields =>
          List(EuroTicket(ticket.fields, ticket.starFields))

        case (_, _) =>
          val fieldsCombinations = ticket.fields.toList.combinations(minSelectedFields).toList
          val starFieldsCombinations = ticket.starFields.toList.combinations(minSelectedStarFields).toList
          (fieldsCombinations, starFieldsCombinations).mapN { (f, sf) => EuroTicket(f.toSet, sf.toSet) }
      }
    }

    /**
      * assumption:
      *
      * format of the line is:
      * it contains two "space" separated elements, which elements are coma separated lists
      * first list represents fields and second one - star fields
      * Example
      * 1,2,32,34,5,45 2,4,5
      */
    implicit object SystemSingleTicketParser$ extends SingleTicketParser[Ticket[System]] {

      override def parse(line: String): String Either Ticket[System] = {
        Try[Ticket[System]] {
          val lineContent = line.split(" ")
          require(lineContent.length == 2, "Line must contain two elements, separated by space!")
          val fields: Set[Int] = lineContent(0).split(",").map(_.toInt).toSet
          val starFields: Set[Int] = lineContent(1).split(",").map(_.toInt).toSet
          SystemEuroTicket(fields, starFields)
        }.toEither.leftMap(_.getMessage)
      }
    }
  }

  trait SingleTicketParser[T] {
    def parse(line: String): String Either T
  }

  object SingleTicketParser {
    def apply[T](f: String => String Either T): SingleTicketParser[T] = (line: String) => f(line)
  }

  trait TicketMultiLineParser[T] {
    def parse(line: String): ValidatedNel[String, List[T]]
  }

  object TicketMultiLineParser {
    def apply[T](f: String => ValidatedNel[String, List[T]]): TicketMultiLineParser[T] = (line: String) => f(line)
  }

  /**
    * assumption:
    *
    * format of the line is:
    * it contains three "space" separated elements, the very first one is a type of the message
    * standard or system
    * two other "space" separated elements are coma separated lists
    * first list represents fields and second one - star fields
    * Example
    * standard 1,2,32,5,45 2,4
    * system 1,2,32,34,5,45 3,4,6
    *
    * It returns validation since file lines can contain multiple errors
    * and it needs to accumulate all of them
    */
  implicit object EuroTicketParser extends TicketMultiLineParser[Ticket[Euro]] {
    override def parse(line: String): ValidatedNel[String, List[Ticket[Euro]]] = {
      Validated.catchNonFatal[List[Ticket[Euro]]] {
        val lineContent = line.split(" ")
        require(lineContent.length == 3, "Line must contain three elements, separated by space!")
        val ticketType = lineContent(0)
        require(ticketType == "standard" || ticketType == "system", "TicketType can be either standard or system!")
        val fields: Set[Int] = lineContent(1).split(",").map(_.toInt).toSet
        val starFields: Set[Int] = lineContent(2).split(",").map(_.toInt).toSet

        if (ticketType == "standard") {
          List(EuroTicket(fields, starFields))
        } else {
          SystemEuroTicket.allCombinations(SystemEuroTicket(fields, starFields))
        }

      }.leftMap(_.getMessage).toValidatedNel
    }
  }
}
