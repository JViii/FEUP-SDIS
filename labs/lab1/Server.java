import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;
import java.util.*;



public class Server{
  DatagramSocket socket;
  Map<String,InetAddress> dnsTable;

  public Server(int port) throws Exception{
    this.socket = new DatagramSocket(port);
    dnsTable = new HashMap<String,InetAddress>();
  }


  public static void main(String[] args) throws Exception {

    if(args.length!=1){
      System.out.println("Usage: java Server <port>"); 
      System.exit(1);
    }

    int port = Integer.parseInt(args[0]);
    Server server = new Server(port);
    server.start();
  }

  public void start() throws IOException{
    byte[] buf = new byte[1024];
    DatagramPacket packet = new DatagramPacket(buf,1024);
    System.out.println("Server has started");

    while(true){
      try{
        socket.receive(packet);
      } catch(IOException e){
        System.out.println(e);
      }
      
      String data = new String(packet.getData(),0,packet.getLength());
      String reply = new String();

      if(data != null && !data.trim().isEmpty()){ //TODO start a new thread
        System.out.println("Server: " + data);
        reply = processRequest(data);
        InetAddress clientIp = packet.getAddress();
        int clientPort = packet.getPort();

        DatagramSocket replySocket = new DatagramSocket(); 
        DatagramPacket replyPacket = new DatagramPacket(reply.getBytes(),reply.length(),clientIp,clientPort);
        replySocket.send(replyPacket);
        replySocket.close();
      }
    }
  }

  public String processRequest(String data) throws IOException{
    String[] args = data.split(" ");
    String oper = args[0];
    String dnsName = args[1];
    InetAddress ip; 
    String reply = new String();
    
    switch(oper){
      case "REGISTER":
        ip = InetAddress.getByName(args[2]);
        reply = registerDomain(dnsName,ip);
        break;
      case "LOOKUP":
        reply = lookupDomain(dnsName);
        break;
      default:
        System.out.println("Invalid Operation");
        System.exit(3);
    }

    return reply;
  }


  public String registerDomain(String dnsName, InetAddress ip){
    InetAddress success = this.dnsTable.put(dnsName,ip);
    return success == null ? String.valueOf(this.dnsTable.size()) : String.valueOf(-1);
  }

  public String lookupDomain(String dnsName){
    InetAddress ip = this.dnsTable.get(dnsName);
    return ip == null ? "NOT_FOUND" : ip.getHostAddress();
  }

}


