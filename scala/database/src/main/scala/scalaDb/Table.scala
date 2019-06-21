package scalaDb
import java.io.FileOutputStream
import java.nio.ByteBuffer

class Field
case class IntegerField() extends Field
case class DoubleField() extends Field
case class TextField(val maxLen: Int) extends Field

abstract class Cell {
  def toString: String
  def <(other: Any): Boolean = (this, other) match {
    case (CellInt(x), y: Int) => x < y
    case (CellDouble(x), y: Double) => x < y
    case (CellText(x), y: String) => x < y
  }
  def >(other: Any): Boolean = (this, other) match {
    case (CellInt(x), y: Int) => x > y
    case (CellDouble(x), y: Double) => x > y
    case (CellText(x), y: String) => x > y
  }
  def <=(other: Any): Boolean = (this, other) match {
    case (CellInt(x), y: Int) => x <= y
    case (CellDouble(x), y: Double) => x <= y
    case (CellText(x), y: String) => x <= y
  }
  def >=(other: Any): Boolean = (this, other) match {
    case (CellInt(x), y: Int) => x >= y
    case (CellDouble(x), y: Double) => x >= y
    case (CellText(x), y: String) => x >= y
  }
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

abstract class Table {
  var data: List[Row] = Nil
  val fields: Map[String, Field]
  var fieldOrder: Array[String] = Array()
  def accessOrder: Unit = fieldOrder = fields.keys.toArray
  def filename: String

  //TODO: Продумать и дописать общение с физическим файлом
  case class Row(val data: Array[Cell], val pos: Int = 0) {
    def save: Unit = {
      var capacity = 8
      for (it <- fieldOrder)
        capacity = capacity + (fields(it) match {
          case IntegerField() => 4
          case DoubleField() => 8
          case TextField(l) => l * 2
        })
      var rawData  = ByteBuffer.allocate(capacity)
      for (i <- 0 until fieldOrder.length) {
        (fields(fieldOrder(i)), data(i)) match {
          case (IntegerField(), CellInt(x)) => rawData.putInt(1).putInt(4).putInt(x)
          case (DoubleField(), CellDouble(x)) => rawData.putInt(2).putInt(8).putDouble(x)
          case (TextField(l), CellText(s)) => rawData.putInt(l).putInt(l).put(s.getBytes)
        }
      }
      rawData.position(0)
      var _d: Array[Byte] = new Array(capacity)
      rawData.get(_d)
      println(s"Row #$pos saved: " + _d.toString)
    }
    override def toString: String = data.mkString(", ")
  }

  case class SelectSeq(val prev: List[SelectSeq] = Nil, val mod: Row => Boolean = (_) => true) {
    def all: SelectSeq =
      SelectSeq(this :: prev, (_) => true)
    def equal(fName: String, x: Any): SelectSeq = {
      val cell = x match {
        case y: Int => CellInt(y)
        case y: Double => CellDouble(y)
        case y: String => CellText(y)
      }
      SelectSeq(this :: prev, (_: Row).data(fieldOrder.indexOf(fName)) == cell)
    }
    def lessThan(fName: String, x: Any): SelectSeq = {
      SelectSeq(this :: prev, (_: Row).data(fieldOrder.indexOf(fName)) < x)
    }
    def greaterThan(fName: String, x: Any): SelectSeq = {
      SelectSeq(this :: prev, (_: Row).data(fieldOrder.indexOf(fName)) > x)
    }
    def lessEq(fName: String, x: Any): SelectSeq = {
      SelectSeq(this :: prev, (_: Row).data(fieldOrder.indexOf(fName)) <= x)
    }
    def greaterEq(fName: String, x: Any): SelectSeq = {
      SelectSeq(this :: prev, (_: Row).data(fieldOrder.indexOf(fName)) >= x)
    }
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
    data = Row(lst.reverse.toArray) :: data
    Row(lst.reverse.toArray).save
  }
}
