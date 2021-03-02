import java.rmi.Remote;
import java.rmi.RemoteException;
import java.net.InetAddress;

public interface RemoteMethods extends Remote{
  String registerDomain(String dnsName,InetAddress ip) throws RemoteException;
  String lookupDomain(String dnsName) throws RemoteException;
}
