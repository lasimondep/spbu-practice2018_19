package roomer

import swing._

class LoginTab {
  val nicknameField = new TitledTextField("Nickname", 20) {
    listenTo(publisher)
    reactions += {
      case UserLogged() =>
        enabled = false
      case UserLogout() =>
        enabled = true
    }
  }
  val loginUserButton: Button = new Button {
    action = Action("Войти") {
      RoomerApplication.tryLogin(nicknameField.text)
      if (!RoomerApplication.isUserLogged)
        ErrorDialog("Пожалуйста, введите nickname")
    }
    listenTo(publisher)
    reactions += {
      case UserLogged() =>
        enabled = false
      case UserLogout() =>
        enabled = true
    }
  }
  val logoutUserButton: Button = new Button {
    action = Action("Выйти") {
      RoomerApplication.userLogout
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
  val ui = new CenteredGridBagPanel(nicknameField, loginUserButton, logoutUserButton)
}
