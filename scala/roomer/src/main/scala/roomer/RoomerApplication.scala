package roomer

import swing._
import event._
import scala.collection.mutable.Buffer

object publisher extends Publisher

object RoomerApplication {
  val defaultPort = 25000

  var mainUserData: Option[UserData] = None
  def tryLogin(nickname: String) =
    if (nickname.length != 0) {
      mainUserData = Some(UserData(nickname))
      if (isUserLogged)
        publisher.publish(UserLogged())
    }
  def isUserLogged =
    mainUserData.isDefined
  def userLogout = {
    disconnect
    mainUserData = None
    publisher.publish(UserLogout())
  }

  var serverHostService: Option[ServerHostService] = None
  def tryCreateServer(port: Int) = {
    serverHostService = Some(new ServerHostService(port))
    serverHostService.foreach(_.runServer)
    if (isServerRunning)
      publisher.publish(ServerRun())
  }
  def isServerRunning =
    serverHostService.exists(_.isRunning)
  def stopServer = {
    serverHostService.foreach(_.shutdownServer)
    publisher.publish(ServerShutdown())
  }

  var clientService: Option[ClientService] = None
  def tryConnect(host: String, port: Int) = {
    clientService = Some(new ClientService)
    clientService.foreach(_.connect(host, port))
    if (isConnected) {
      sendMessage(Message(Message.Type.GetRoomList))
      publisher.publish(ClientConnected())
    }
  }
  def isConnected =
    clientService.exists(_.isConnected)
  def disconnect = {
    clientService.foreach(_.disconnect)
    clientRoomList.clear
    clientRoomHist.foreach(_.roomHist.clear)
    clientRoomHist.clear
    publisher.publish(ClientDisconnected())
  }

  var serverRoomList = Buffer[RoomData]()
  var serverRoomHist = Buffer[RoomHist]()
  def createServerRoom(rd: RoomData) = {
    val roomId = serverRoomList.length
    val room = RoomData(rd.roomTitle, roomId)
    serverRoomList = serverRoomList :+ room
    serverRoomHist = serverRoomHist :+ RoomHist(roomId)
    room
  }

  var clientRoomList = Buffer[RoomData]()
  var clientRoomHist = Buffer[RoomHist]()
  def takeRoom(rd: RoomData) = {
    clientRoomList += rd
    clientRoomHist += RoomHist(rd.roomId)
    sendMessage(Message(Message.Type.GetRoomHist, rd))
    publisher.publish(RoomReceived())
  }
  def takeMsg(rd: RoomData, rm: RoomMsg) {
    clientRoomHist(rd.roomId).append(rm)
    publisher.publish(MessageReceived(rd, rm))
  }

  def sendMessage(message: Message) =
    clientService.foreach(_.send(message))
}
