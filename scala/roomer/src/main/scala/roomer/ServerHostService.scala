package roomer

import java.net.{Socket, ServerSocket, SocketException}
import java.util.concurrent.Executors
import java.io.{ObjectInputStream, ObjectOutputStream}

class ServerHostService(port: Int, poolSize: Int = 50) {
  val serverSocket = new ServerSocket(port)
  val pool = Executors.newFixedThreadPool(poolSize)
  val listenerThread = new Thread(new Listener)
  var connectedClients = List[Handler]()

  def startListening =
    listenerThread.start
  def stopListening =
    serverSocket.close
  def runServer =
    startListening
  def isRunning =
    !serverSocket.isClosed
  def shutdownServer = {
    stopListening
    for (c <- connectedClients)
      c.socket.close
  }

  def sendBroadcast(message: Message) =
    for {
      c <- connectedClients if !c.socket.isClosed
    } {
      c.out.writeObject(message)
      c.out.flush
    }

  class Listener extends Runnable {
    def run =
      try
        while (true) {
          Log("Wait new clients...")
          val c = new Handler(serverSocket.accept)
          connectedClients = c :: connectedClients
          pool.execute(c)
        } catch {
          case e: SocketException => Log(e.toString)
        } finally
          pool.shutdown
  }

  class Handler(val socket: Socket) extends Runnable {
    val raw_in = socket.getInputStream
    val out = new ObjectOutputStream(socket.getOutputStream)
    def run = {
      try {
        val in = new ObjectInputStream(raw_in)
        while (true) {
          val message = in.readObject.asInstanceOf[Message]
          message match {
            case Message(Message.Type.GetRoomList, _, _) => {
              for (rd <- RoomerApplication.serverRoomList)
                out.writeObject(Message(Message.Type.CreateRoom, rd))
              out.flush
            }
            case Message(Message.Type.GetRoomHist, rd, _) => {
              for (rm <- RoomerApplication.serverRoomHist(rd.roomId).roomHist)
                out.writeObject(Message(Message.Type.PostMsg, rd, rm))
              out.flush
            }
            case Message(Message.Type.CreateRoom, rd, _) =>
              sendBroadcast(Message(Message.Type.CreateRoom, RoomerApplication.createServerRoom(rd)))
            case Message(Message.Type.PostMsg, rd, rm) => {
              RoomerApplication.serverRoomHist(rd.roomId).append(rm)
              sendBroadcast(Message(Message.Type.PostMsg, rd, rm))
            }
          }
        }
      } catch {
        case e: Throwable => Log(e.toString)
      } finally {
        raw_in.close
        out.close
        socket.close
      }
    }
  }
}
