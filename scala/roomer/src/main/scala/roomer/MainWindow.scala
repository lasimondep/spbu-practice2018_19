package roomer

import swing.{Dimension, MainFrame, TabbedPane}

class MainWindow extends MainFrame {
  override def closeOperation = {
    RoomerApplication.stopServer
    RoomerApplication.disconnect
    sys.exit(0)
  }
  val createConnectPage = new TabbedPane.Page("Создание сервера/подключение", (new CreateConnectTab).ui)
  val roomPage: TabbedPane.Page = new TabbedPane.Page("Комната", (new RoomTab).ui) {
    listenTo(publisher)
    reactions += {
      case ClientConnected() =>
        enabled = true
      case ClientDisconnected() =>
        if (tabbedPane.selection.page == roomPage)
          tabbedPane.selection.page = createConnectPage
        enabled = false
    }
  }
  val tabbedPane: TabbedPane = new TabbedPane {
    pages ++= Seq(
      new TabbedPane.Page("Пользователь", (new LoginTab).ui),
      createConnectPage, roomPage
    )
  }
  contents = tabbedPane
  roomPage.enabled = false
  maximize()
  title = "Roomer"
  minimumSize = new Dimension(400, 400)
  visible = true
  peer.setLocationRelativeTo(null)
}
