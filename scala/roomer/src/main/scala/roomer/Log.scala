package roomer

object Log {
  val format = new java.text.SimpleDateFormat("dd hh:mm:ss")
  def apply(msg: String) =
    println(s"${format.format(new java.util.Date)}: $msg")
}
