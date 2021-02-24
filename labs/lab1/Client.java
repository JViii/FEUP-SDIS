import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class Client{
  DatagramSocket socket;
  int serverPort;
  InetAddress serverIp;
  String operation;
  String [] operands;


  public Client(String server, int port, String oper, String[] opnd) throws Exception{
    this.socket = new DatagramSocket();
    this.serverIp = InetAddress.getByName(server); // This work for dns names and ip's
    this.serverPort = port;
    this.operation = new String(oper);
    this.operands = opnd;
  }


  public static void main(String[] args) throws Exception {

    if(args.length<4){
      System.out.println("Usage: java Client <host> <port> <oper> <opnd>*"); 
      System.exit(1);
    }

    int port = Integer.parseInt(args[1]);
    String[] opnd = Arrays.copyOfRange(args, 3, args.length);

    Client client = new Client(args[0],port,args[2],opnd);
    String request = client.buildRequest();
    client.sendRequest(request);
  }

  public String buildRequest(){
    String request = new String(this.operation).toUpperCase();

    //TODO error handling if missing/wrong args
    switch(request){
      case "REGISTER":
        request = request + " " + this.operands[0] + " " + this.operands[1];
        break;
      case "LOOKUP":
        request = request + " " + this.operands[0];
        break;
      default:
        System.out.println("Invalid Operation");
        System.exit(3);
    }

    return request;
  }

  public void sendRequest(String request) throws IOException{
    byte[] buf = new byte[1024];
    DatagramPacket reply = new DatagramPacket(buf,1024);
    DatagramPacket packet = new DatagramPacket(request.getBytes(),request.length(),this.serverIp,this.serverPort);
    this.socket.send(packet);

    while(true){
      try{
        socket.receive(reply);
      } catch(Exception e){
        System.out.println(e);
      }
      
      String data = new String(reply.getData(),0,reply.getLength());
      if(data != null && !data.trim().isEmpty()){
        System.out.println("Client: " + request + " : " + data);
        break;
      }
    }

  this.socket.close();
  }
}


