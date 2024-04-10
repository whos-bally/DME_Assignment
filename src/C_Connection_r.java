import java.net.*;
import java.io.*;
// Reacts to a node request.
// Receives and records the node request in the buffer.
//
public class C_Connection_r extends Thread{
	
    // class variables
    C_buffer 	   buffer;
    Socket socket;
    InputStream    in;
    BufferedReader bin;
       	
    public C_Connection_r(Socket s, C_buffer b){
    	this.socket = s;
    	this.buffer = b;
    }
    
    public void run() {	
		final int NODE = 0;
		final int PORT = 1;
	
		String[] request = new String[2];
		
		System.out.println("C:connection IN  dealing with request from socket "+ socket);
		try {	
		    
		    // >>> read the request, i.e. node ip and port from the socket s
		    // >>> save it in a request object and save the object in the buffer (see C_buffer's methods).
	
		    in = socket.getInputStream();
		    bin = new BufferedReader(new InputStreamReader(in));
		    
		//    request[NODE] =
		//    request[PORT] = 
		    
		    socket.close();
		    System.out.println("C:connection OUT    received and recorded request from "+ request[NODE]+":"+request[PORT]+ "  (socket closed)"); 
		    	
		} 
		catch (java.io.IOException e){
				System.out.println(e);
				System.exit(1);
		} 	
		buffer.show();
		
 	}
}
