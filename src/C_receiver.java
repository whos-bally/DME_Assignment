import java.io.IOException;
import java.net.*;

/**
 * This class is the Coordinator Receiver. It is used to receive requests from nodes to pass to the coordinator
 */

public class C_receiver extends Thread{
    
    private C_buffer buffer;
    private int port;
    private ServerSocket serverSocket;
    private Socket socketFromNode;
    private C_Connection_r connect;
    
    public C_receiver (C_buffer b, int p){
		buffer = b;
		port = p;
    }
    
    public void run () {

		try {
			serverSocket = new ServerSocket(7000);
		} catch (IOException e) {
			e.printStackTrace();
		}

		while (true) {
//	    try{
//
//			// >>> get a new connection
//
//			System.out.println ("C:receiver    Coordinator has received a request ...") ;
//
//			// >>> create a separate thread to service the request, a C_Connection_r thread.
//
//	    }
//	    catch (IOException e) {
//	    	System.out.println("Exception when creating a connection "+e);
//	    }
	    
	}
    }//end run
}
