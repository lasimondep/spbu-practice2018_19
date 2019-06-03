package scalaDb

class Field
case class IntegerField() extends Field
case class DoubleField() extends Field
case class TextField(val maxLen: Int) extends Field

abstract class Cell {
  def toString: String
}
case class CellInt(val x: Int) extends Cell {
  override def toString: String = x.toString
}
case class CellDouble(val x: Double) extends Cell {
  override def toString: String = x.toString
}
case class CellText(val x: String) extends Cell {
  override def toString: String = x.toString
}

case class Row(val data: List[Cell] = Nil, val pos: Int = 0) {
  def save: Unit = {
    println(s"Row #$pos saved: " + data.toString) //TODO
  }
  override def toString: String = data.mkString(", ")
}

abstract class Table {
  var data: List[Row] = Nil
  val fields: Map[String, Field]
  var fieldOrder: List[String] = Nil
  def accessOrder: Unit = fieldOrder = fields.keys.toList

  case class SelectSeq(val prev: List[SelectSeq], val mod: Row => Boolean) {
    def all: SelectSeq =
      SelectSeq(this :: prev, (_) => true)
    def equal(fName: String, m: Any): SelectSeq = {
      val mtch = m match {
        case x: Int => CellInt(x.toInt)
        case x: Double => CellDouble(x.toDouble)
        case x => CellText(x.toString)
      }
      SelectSeq(this :: prev, (_: Row).data(fieldOrder.indexOf(fName)) == mtch)
    }
    def lessThan: Unit = ()
    def greaterThan: Unit = ()
    def lessEq: Unit = ()
    def greaterEq: Unit = ()
    def confirm: List[Row] = {
      var lst: List[Row => Boolean] = List(mod)
      var res: List[Row] = Nil
      for (it <- prev)
        lst = it.mod :: lst
      var flag: Boolean = true
      for (xit <- data) {
        flag = true
        for (it <- lst)
          if (!it(xit))
            flag = false
        if (flag)
          res = xit :: res
      }
      res
    }
  }

  def insert(params: Any*): Unit = {
    var lst: List[Cell] = Nil
    for (it <- params)
      lst = it match {
        case x: Int => CellInt(x.toInt) :: lst
        case x: Double => CellDouble(x.toDouble) :: lst
        case x => CellText(x.toString) :: lst
      }
    data = Row(lst.reverse) :: data
    Row(lst.reverse).save
  }
  def all: SelectSeq =
    SelectSeq(Nil, (_) => true)
}
