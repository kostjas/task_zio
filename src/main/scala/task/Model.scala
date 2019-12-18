package task

import cats._
import cats.data.{ValidatedNel, _}
import cats.implicits._

import ToBooleanOps._

import scala.util.Try

object Model {

  val maxSizeOfFields = 50
  val maxSizeOfAdditionalFields = 11

  val permittedFields: Set[Int] = (1 to maxSizeOfFields).to(Set)
  val permittedAdditionalFields: Set[Int] = (1 to maxSizeOfAdditionalFields).to(Set)

  object TicketSources {
    val standard: String = "standard"
    val system: String = "system"
  }

  sealed trait TicketType
  trait Simple extends TicketType
  trait Advanced extends TicketType
  trait Winning extends TicketType

  case class Ticket[T <: TicketType](fields: Set[Int], additionalFields: Set[Int])

  object WinningTicket {
    val amountOfSelectedFields = 5
    val amountSelectedAdditionalFields = 2

    /**
      * assumptions:
      *
      * amount of selected fields can be 5
      * amount of selected additional fields can be 2
      *
      * values of selected fields can be in scope of the range from 1 to 50
      * values of selected additional fields can be in scope of the range from 1 to 11
      *
      */
    def apply(fields: Set[Int], additionalFields: Set[Int]): Ticket[Winning] = {
      require(fields.size == amountOfSelectedFields, s"Max selected Fields must be $amountOfSelectedFields")
      require(additionalFields.size == amountSelectedAdditionalFields, s"Max selected AdditionalFields must be $amountSelectedAdditionalFields")
      require(fields.forall(permittedFields.contains),
        s"Selected Fields must be in scope of the range from 1 to $maxSizeOfFields")
      require(additionalFields.forall(permittedAdditionalFields.contains),
        s"Selected Additional Fields must be in scope of the range from 1 to $maxSizeOfAdditionalFields")

      new Ticket[Winning](fields, additionalFields)
    }

    implicit object WinningSingleTicketParser$ extends SingleTicketParser[Ticket[Winning]] {

      /**
        * assumption:
        *
        * format of the line is:
        * it contains two "space" separated elements, which elements are coma separated lists
        * first list represents fields and second one - additional fields
        * Example
        * 1,2,32,34,5,45 2,4,5
        */
      override def parse(line: String): String Either Ticket[Winning] = {
        Try[Ticket[Winning]] {
          val lineContent = line.split(" ")
          require(lineContent.length == 2, "Line must contain two elements, separated by space!")
          val fields: Set[Int] = lineContent(0).split(",").map(_.toInt).to(Set)
          val additionalFields: Set[Int] = lineContent(1).split(",").map(_.toInt).to(Set)
          WinningTicket(fields, additionalFields)
        }.toEither.leftMap(_.getMessage)
      }
    }
  }

  object SimpleTicket {

    val amountOfSelectedFields = 5
    val amountSelectedAdditionalFields = 2

    /**
      * assumptions:
      *
      * amount of selected fields can be 5
      * amount of selected additional fields can be 2
      *
      * values of selected fields can be in scope of the range from 1 to 50
      * values of selected additional fields can be in scope of the range from 1 to 11
      *
      */
    def apply(fields: Set[Int], additionalFields: Set[Int]): Ticket[Simple] = {
      require(fields.size == amountOfSelectedFields, s"Max selected Fields must be $amountOfSelectedFields")
      require(additionalFields.size == amountSelectedAdditionalFields, s"Max selected additionalFields must be $amountSelectedAdditionalFields")
      require(fields.forall(permittedFields.contains),
        s"Selected Fields must be in scope of the range from 1 to $maxSizeOfFields")
      require(additionalFields.forall(permittedAdditionalFields.contains),
        s"Selected Additional Fields must be in scope of the range from 1 to $maxSizeOfAdditionalFields")

      new Ticket[Simple](fields, additionalFields)
    }
  }

  object SystemTicket {

    val minSelectedFields = 5
    val minSelectedAdditionalFields = 2
    val maxSelectedFields: Int = 10
    val maxSelectedAdditionalFields: Int = 5

    /**
      * assumptions:
      *
      * amount of selected fields can be in scope of the range from 5 to 10
      * amount of selected additional fields can be in scope of the range from 2 to 5
      *
      * values of selected fields can be in scope of the range from 1 to 50
      * values of selected additional fields can be in scope of the range from 1 to 11
      *
      */
    def apply(fields: Set[Int], additionalFields: Set[Int]): Ticket[Advanced] = {
      val fieldsSize = fields.size

      require(minSelectedFields <= fieldsSize && fieldsSize <= maxSelectedFields,
        s"Amount of selected fields must be between $minSelectedFields and $maxSelectedFields ")
      val additionalFieldsSize = additionalFields.size

      require(minSelectedAdditionalFields <= additionalFieldsSize && additionalFieldsSize <= maxSelectedAdditionalFields,
        s"Amount of selected Additional Fields must be between $minSelectedAdditionalFields and $maxSelectedAdditionalFields ")

      require(fields.forall(permittedFields.contains),
        s"Selected Fields must be in scope of the range from 1 to $maxSizeOfFields")

      require(additionalFields.forall(permittedAdditionalFields.contains),
        s"Selected Additional Fields must be in scope of the range from 1 to $maxSizeOfAdditionalFields")

      new Ticket[Advanced](fields, additionalFields)
    }

    def allCombinations(ticket: Ticket[Advanced]): List[Ticket[Simple]] = {
      (ticket.fields.size, ticket.additionalFields.size) match {
        case (fieldsSize, additionalFieldsSize) if fieldsSize == minSelectedFields && additionalFieldsSize == minSelectedAdditionalFields =>
          List(SimpleTicket(ticket.fields, ticket.additionalFields))

        case (_, _) =>
          val fieldsCombinations = ticket.fields.to(List).combinations(minSelectedFields).to(List)
          val additionalFieldsCombinations = ticket.additionalFields.to(List).combinations(minSelectedAdditionalFields).to(List)
          (fieldsCombinations, additionalFieldsCombinations).mapN { (f, sf) => SimpleTicket(f.to(Set), sf.to(Set)) }
      }
    }

    /**
      * assumption:
      *
      * format of the line is:
      * it contains two "space" separated elements, which elements are coma separated lists
      * first list represents fields and second one - additional fields
      * Example
      * 1,2,32,34,5,45 2,4,5
      */
    implicit object SystemSingleTicketParser$ extends SingleTicketParser[Ticket[Advanced]] {

      override def parse(line: String): String Either Ticket[Advanced] = {
        Try[Ticket[Advanced]] {
          val lineContent = line.split(" ")
          require(lineContent.length == 2, "Line must contain two elements, separated by space!")
          val fields: Set[Int] = lineContent(0).split(",").map(_.toInt).to(Set)
          val additionalFields: Set[Int] = lineContent(1).split(",").map(_.toInt).to(Set)
          SystemTicket(fields, additionalFields)
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
    * first list represents fields and second one - additional fields
    * Example
    * standard 1,2,32,5,45 2,4
    * system 1,2,32,34,5,45 3,4,6
    *
    * It returns validation since file lines can contain multiple errors
    * and it needs to accumulate all of them
    */
  implicit object SimpleTicketParser extends TicketMultiLineParser[Ticket[Simple]] {
    override def parse(line: String): ValidatedNel[String, List[Ticket[Simple]]] = {
      Validated.catchNonFatal[List[Ticket[Simple]]] {
        val lineContent = line.split(" ")
        require(lineContent.length == 3, "Line must contain three elements, separated by space!")
        val ticketSource = lineContent(0)
        require(ticketSource == TicketSources.standard || ticketSource == TicketSources.system, "TicketType can be either standard or system!")
        val fields: Set[Int] = lineContent(1).split(",").map(_.toInt).to(Set)
        val additionalFields: Set[Int] = lineContent(2).split(",").map(_.toInt).to(Set)

        (ticketSource == TicketSources.standard) ? List(SimpleTicket(fields, additionalFields)) | SystemTicket.allCombinations(SystemTicket(fields, additionalFields))

      }.leftMap(_.getMessage).toValidatedNel
    }
  }
}
