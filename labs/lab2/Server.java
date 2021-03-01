import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;
import java.io.IOException;
import java.util.concurrent.*;

public class Server{
  DatagramSocket socket;
  int port,mcPort;
  InetAddress mcAddress;
  Map<String,InetAddress> dnsTable;
  MCRunnable mcr;

  public Server(int port, InetAddress mcAddress, int mcPort) throws IOException{
    this.socket = new DatagramSocket(port); 
    dnsTable = new HashMap<String,InetAddress>();
    this.port = port;
    this.mcPort = mcPort;
    this.mcAddress = mcAddress;

    this.mcr = new MCRunnable(InetAddress.getByName("localhost"),port,mcAddress,mcPort);
  }

  public static void main(String[] args) throws Exception{
    if(args.length!=3){
      System.out.println("Usage: java Server <srvc_port> <mcast_addr> <mcast_port>");
      System.exit(1);
    }

    int port = Integer.parseInt(args[0]);
    int mcPort = Integer.parseInt(args[2]);
    InetAddress id = InetAddress.getByName(args[1]);

    Server server = new Server(port,id,mcPort);
    server.startAdvertisment();
    server.start();
  }

  public void startAdvertisment(){
    ScheduledExecutorService adService = Executors.newScheduledThreadPool(1);
    ScheduledFuture<?> scheduledFuture = adService.scheduleAtFixedRate(this.mcr, 1, 1, TimeUnit.SECONDS);
  }

	public void start() throws IOException{
    byte[] buf = new byte[1024];
    DatagramPacket packet = new DatagramPacket(buf,1024);
    System.out.println("Server has started");

    while(true){
      try{
        this.socket.receive(packet);
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
