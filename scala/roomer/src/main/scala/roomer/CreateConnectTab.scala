package roomer

import swing._

class CreateConnectTab {
  val serverPortField = new TitledFormattedTextField("Порт", "{0,number,00000}") {
    focusLostBehavior = FormattedTextField.FocusLostBehavior.Commit
    text = RoomerApplication.defaultPort.toString
    listenTo(publisher)
    reactions += {
      case ServerRun() =>
        enabled = false
      case ServerShutdown() =>
        enabled = true
    }
  }
  val createServerButton = new Button {
    action = Action("Создать сервер") {
      if (serverPortField.editValid)
        RoomerApplication.tryCreateServer(serverPortField.text.toInt)
      if (!RoomerApplication.isServerRunning)
        ErrorDialog("Не удалось создать сервер")
    }
    listenTo(publisher)
    reactions += {
      case ServerRun() =>
        enabled = false
      case ServerShutdown() =>
        enabled = true
    }
  }
  val stopServerButton = new Button {
    action = Action("Остановить Сервер") {
      RoomerApplication.stopServer
    }
    listenTo(publisher)
    reactions += {
      case ServerRun() =>
        enabled = true
      case ServerShutdown() =>
        enabled = false
    }
    enabled = false
  }
  val hostField = new TitledTextField("Хост") {
    enabled = false
    text = "localhost"
    listenTo(publisher)
    reactions += {
      case UserLogged() =>
        enabled = true
      case UserLogout() =>
        enabled = false
    }
  }
  val clientPortField = new TitledFormattedTextField("Порт", "{0,number,00000}") {
    enabled = false
    focusLostBehavior = FormattedTextField.FocusLostBehavior.Commit
    text = RoomerApplication.defaultPort.toString
    listenTo(publisher)
    reactions += {
      case UserLogged() =>
        enabled = true
      case UserLogout() =>
        enabled = false
    }
  }
  val connectButton = new Button {
    action = Action("Подключиться") {
      if (clientPortField.editValid)
        RoomerApplication.tryConnect(hostField.text, clientPortField.text.toInt)
      if (!RoomerApplication.isConnected)
        ErrorDialog("Не удалось подключиться")
    }
    listenTo(publisher)
    reactions += {
      case UserLogged() | ClientDisconnected() =>
        enabled = true
      case UserLogout() | ClientConnected() =>
        enabled = false
    }
    enabled = false
  }
  val disconnectButton = new Button {
    action = Action("Отключиться") {
      RoomerApplication.disconnect
    }
    listenTo(publisher)
    reactions += {
      case UserLogged() =>
        enabled = true
      case UserLogout() =>
        enabled = false
    }
    enabled = false
  }
  val ui = new CenteredGridBagPanel(serverPortField, createServerButton, stopServerButton,
    hostField, clientPortField, connectButton, disconnectButton)
}
