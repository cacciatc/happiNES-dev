package happiNESdev.app.debug;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class DebuggerServer {
	private ServerSocket serv;
	
	public DebuggerServer(){
		try {
			serv.bind(new InetSocketAddress("localhost",1045));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void spin(boolean debugging){
		Socket clientSocket = null;
		try {
		    clientSocket = serv.accept();
		    if(debugging){
		    	clientSocket.getOutputStream().write(0x05);
		    }
		} catch (IOException e) {
		    System.out.println("Accept failed: 1045");
		}
	}
}
