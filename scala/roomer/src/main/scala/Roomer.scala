import roomer.{MainWindow, Log}

object Roomer {
  def main(args: Array[String]): Unit = {
    val mainWindow = new MainWindow
    Log("Application started")
  }
}
