package roomer

import java.net.{InetSocketAddress, Socket}
import java.io.{InputStream, ObjectInputStream, ObjectOutputStream, OutputStream}

class ClientService {
  val socket = new Socket()
  var out: Option[ObjectOutputStream] = None
  var receiverThread = new Thread(new Receiver)

  def connect(host: String, port: Int) {
    try {
      socket.connect(new InetSocketAddress(host, port))
      out = Some(new ObjectOutputStream(socket.getOutputStream))
      receiverThread.start
    } catch {
      case e: Throwable => {
        Log(e.toString)
        disconnect
      }
    }
  }
  def isConnected =
    socket.isConnected
  def disconnect = {
    out.foreach(_.close)
    socket.close
    publisher.publish(ClientDisconnected())
  }

  def send(message: Message) =
    try
      if (isConnected) {
        out.foreach(_.writeObject(message))
        out.foreach(_.flush)
      }
    catch {
      case e: Throwable =>
        Log(e.toString)
        disconnect
    }

  class Receiver extends Runnable {
    def run =
      try {
        val in = new ObjectInputStream(socket.getInputStream)
        while (true) {
          val message = in.readObject.asInstanceOf[Message]
          message match {
            case Message(Message.Type.CreateRoom, rd, _) =>
              RoomerApplication.takeRoom(rd)
            case Message(Message.Type.PostMsg, rd, rm) =>
              RoomerApplication.takeMsg(rd, rm)
          }
        }
      } catch {
        case e: Throwable => Log(e.toString)
      } finally
        disconnect
  }
}
