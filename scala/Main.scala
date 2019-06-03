import scalaDb.{Table, IntegerField, TextField, DoubleField}

object People extends Table {
  val fields = Map("pk" -> IntegerField(), "name" -> TextField(25), "surname" -> TextField(25), "money" -> DoubleField())
}

object Main extends App {
  People.insert(1, "Vasiliy", "Pupkin", 239.17)
  People.insert(2, "Petr", "Vasechkin", 155.0)
  People.insert(3, "Third", "Pupkin", 10.1374)
  People.accessOrder
  var res = People.all.equal("surname", "Pupkin")

  println("First try")
  res.confirm.foreach(println)
  println("\nSecond try")
  res.equal("pk", 1).confirm.foreach(println)
}
