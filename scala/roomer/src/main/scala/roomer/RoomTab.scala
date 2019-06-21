package roomer

import swing._
import event._

class RoomTab {
  var currentRoom = RoomData()
  val format = new java.text.SimpleDateFormat("[dd.MM hh:mm:ss]")
  val chatField = new TextArea {
    editable = false
    listenTo(publisher)
    reactions += {
      case SelectionChanged(`roomListView`) =>
        if (roomListView.selection.items.length == 1) {
          text = ""
          for (message <- RoomerApplication.clientRoomHist(roomListView.selection.items(0).roomId).roomHist)
            append(s"${format.format(message.date)} ${message.user.nickname}: ${message.text}\n")
        }
      case MessageReceived(roomData, message) =>
        if (currentRoom == roomData)
          append(s"${format.format(message.date)} ${message.user.nickname}: ${message.text}\n")
      case ClientDisconnected() =>
        text = ""
    }
  }
  val messageField = new TextArea {
    rows = 3
  }
  val sendMessageButton = new Button {
    action = Action("Отправить") {
      if (currentRoom.roomId != -1) {
        if (messageField.text.length != 0) {
          RoomerApplication.sendMessage(Message(
            Message.Type.PostMsg,
            currentRoom,
            RoomMsg(RoomerApplication.mainUserData.get, messageField.text)
          ))
          messageField.text = ""
        }
      } else
        ErrorDialog("Пожалуйста, выберите комнату")
    }
  }
  val roomTitleField = new TitledTextField("Название комнаты")
  val createRoomButton = new Button {
    action = Action("Создать комнату") {
      if (roomTitleField.text.length != 0) {
        RoomerApplication.sendMessage(Message(Message.Type.CreateRoom, RoomData(roomTitleField.text)))
        roomTitleField.text = ""
      } else
        ErrorDialog("Пожалуйста, впишите название комнаты")
    }
  }
  val roomListView: ListView[RoomData] = new ListView[RoomData] {
    selection.intervalMode = ListView.IntervalMode.Single
    chatField.listenTo(selection)
    renderer = ListView.Renderer(_.roomTitle)
    listenTo(publisher, selection)
    reactions += {
      case SelectionChanged(`roomListView`) =>
        if (roomListView.selection.items.length == 1)
          currentRoom = roomListView.selection.items(0)
      case RoomReceived() =>
        roomListView.listData = RoomerApplication.clientRoomList
    }
  }
  val ui = new BorderPanel {
    layout(new SplitPane(Orientation.Vertical) {
      leftComponent = new BorderPanel {
        layout(new BoxPanel(Orientation.Vertical) {
          contents ++= Seq(roomTitleField, createRoomButton)
        }) = BorderPanel.Position.North
        layout(new ScrollPane(roomListView) {
          border = Swing.TitledBorder(Swing.EtchedBorder(Swing.Raised), "Список комнат")
        }) = BorderPanel.Position.Center
      }
      rightComponent = new BorderPanel {
        layout(new ScrollPane {
              contents = chatField
        }) = BorderPanel.Position.Center
        layout(new BoxPanel(Orientation.Horizontal) {
          contents ++= Seq(
            new ScrollPane {
              contents = messageField
            }, sendMessageButton)
        }) = BorderPanel.Position.South
      }
    }) = BorderPanel.Position.Center
  }
}
