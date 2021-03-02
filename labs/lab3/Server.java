import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.net.InetAddress;
import java.util.*;

public class Server implements RemoteMethods {
  String remoteName;
  Map<String,InetAddress> dnsTable;

  public Server(String remoteName){
    this.remoteName = remoteName; 
    dnsTable = new HashMap<String,InetAddress>();
  }

  public static void main(String[] args){
    if(args.length!=1){
      System.out.println("Usage: java Server <remote_object_name>");
      System.exit(1);
    }

    try{
      Server server = new Server(args[0]);  
      RemoteMethods rm = (RemoteMethods) UnicastRemoteObject.exportObject(server,0);

      Registry registry = LocateRegistry.getRegistry();
      registry.bind(server.remoteName,rm);

      System.out.println("Server has started");
    }
    catch(Exception e){
      e.printStackTrace();
    }

  }

  public String registerDomain(String dnsName,InetAddress ip){
    System.out.println("Server: Register " + dnsName + " to " + ip.getHostName());
    InetAddress success = this.dnsTable.put(dnsName,ip);
    return success == null ? String.valueOf(this.dnsTable.size()) : String.valueOf(-1);
  }

  public String lookupDomain(String dnsName){
    System.out.println("Server: Lookup " + dnsName); 
    InetAddress ip = this.dnsTable.get(dnsName);
    return ip == null ? "NOT_FOUND" : ip.getHostAddress();     
  }
}
