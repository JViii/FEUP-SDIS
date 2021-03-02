import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.net.InetAddress;
import java.util.*;

public class Client{
  String operation;
  String [] operands;

  public Client(String oper,String[] opnd){
    this.operation = oper;
    this.operands = opnd;
  }

  public static void main(String[] args){
    if(args.length<4){
      System.out.println("Usage: java Client <host_name> <remote_object_name> <oper> <opnd>*");
      System.exit(1);
    }

    String hostName = args[0];
    String remoteObjName = args[1];

    String[] opnd = Arrays.copyOfRange(args, 3, args.length);
    Client client = new Client(args[2],opnd); 

    try{
      Registry registry = LocateRegistry.getRegistry(hostName);
      RemoteMethods stub = (RemoteMethods) registry.lookup(remoteObjName);

      String request = new String(client.operation).toUpperCase();
      String reply = new String();

      switch(request){
        case "REGISTER":
          InetAddress host = InetAddress.getByName(client.operands[1]);
          reply = stub.registerDomain(client.operands[0],host);
          break;
        case "LOOKUP":
          reply = stub.lookupDomain(client.operands[0]);
          break;
        default:
          System.out.println("Invalid Operation");
          System.exit(3);
      }

      System.out.println(reply);
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }
  
}
