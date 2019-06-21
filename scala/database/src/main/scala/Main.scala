import scalaDb.{Table, IntegerField, TextField, DoubleField}

object People extends Table {
  val fields = Map("pk" -> IntegerField(), "name" -> TextField(16), "surname" -> TextField(16), "money" -> DoubleField())
  val filename = "test.bin"
}

object Main extends App {
  People.accessOrder
  People.insert(1, "0123456789abcdef", "0123456789abcdef", 239.17)
  People.insert(2, "Petr", "Vasechkin", 155.0)
  People.insert(3, "Third", "Pupkin", 10.1374)
  var res = People.SelectSeq()
  println("First try")
  res.confirm.foreach(println)
  println("\nSecond try")
  res = res.lessEq("pk", 2)
  res.confirm.foreach(println)
  println("\nThird try")
  res = res.greaterThan("money", 155.0)
  res.confirm.foreach(println)
}
