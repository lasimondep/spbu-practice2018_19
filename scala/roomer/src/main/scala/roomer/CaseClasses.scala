package roomer

import swing._
import event._
import scala.collection.mutable.Buffer

object Message {
  object Type extends Enumeration {
    val GetRoomList: Type.Value = Value(0)
    val GetRoomHist: Type.Value = Value(1)
    val CreateRoom: Type.Value = Value(2)
    val PostMsg: Type.Value = Value(3)
  }
  type Type = Type.Value
}
case class Message(t: Message.Type,
  roomData: RoomData = RoomData(),
  rm: RoomMsg = RoomMsg())

case class RoomMsg(user: UserData = UserData(), text: String = "", date: java.util.Date = new java.util.Date)
case class RoomHist(roomId: Int, var roomHist: Buffer[RoomMsg] = Buffer[RoomMsg]()) {
  def append(rm: RoomMsg) =
    roomHist = roomHist :+ rm
}
case class RoomData(roomTitle: String = "", roomId: Int = -1)

case class UserData(nickname: String = "")

case class UserLogged() extends Event
case class UserLogout() extends Event
case class ServerRun() extends Event
case class ServerShutdown() extends Event
case class ClientConnected() extends Event
case class ClientDisconnected() extends Event
case class RoomReceived() extends Event
case class MessageReceived(roomData: RoomData, message: RoomMsg) extends Event

