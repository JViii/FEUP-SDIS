import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;
import java.io.IOException;
import java.util.concurrent.*;

public class MCRunnable implements Runnable {
  InetAddress serviceIP, mcAddress;
  int servicePort, mcPort; 
  DatagramSocket socket;

  public MCRunnable(InetAddress serviceIP, int servicePort, InetAddress mcAddress, int mcPort) throws IOException 
    this.socket = new DatagramSocket();
    this.serviceIP= serviceIP;
    this.servicePort = servicePort;
    this.mcAddress = mcAddress
    this.mcPort = mcPort;
  }

  public void run() {
    try{
      this.multicast();
    }
    catch(IOException e){
      throw new RuntimeException(e);
    }
  }

  public void multicast() throws IOException{
    byte[] buf = new byte[1024];
    buf = new String(serviceIP.getHostAddress() + " " + this.servicePort).getBytes();

    DatagramPacket packet = new DatagramPacket(buf,buf.length,this.mcAddress,this.mcPort);
    socket.send(packet);
    System.out.println("multicast: " + this.mcAddress.getHostAddress() + " " + this.mcPort + ": " + this.serviceIP.getHostAddress() + " " + this.servicePort);
  }
}


