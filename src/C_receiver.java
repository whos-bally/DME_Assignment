import java.io.IOException;
import java.net.*;

/**
 * This class is the Coordinator Receiver.
 * It is used to receive requests from nodes to pass to the coordinator
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
			System.out.println("C:receiver - Coordinator is listening for requests");
		} catch (IOException e) {
			e.printStackTrace();
		}

		do {
			try{

				// >>> get a new connection
				socketFromNode = serverSocket.accept();

				System.out.println ("C:receiver - Coordinator has received a request") ;

				// >>> create a separate thread to service the request, a C_Connection_r thread.
				connect = new C_Connection_r(socketFromNode, buffer);
				connect.run();
				System.out.println("C:receiver - closing request thread");
				//serverSocket.close();

			}
			catch (IOException e) {
				System.out.println("Exception when creating a connection ");
				e.printStackTrace();
			}
	    
		}
		while (true);
    }

}//end class
