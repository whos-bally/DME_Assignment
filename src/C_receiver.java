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
		// >>> create the socket the server will listen to
		try {
			serverSocket = new ServerSocket(7000);
			System.out.println("C:receiver - Coordinator is listening for requests");
		} catch (IOException e) {
			System.out.println("C:receiver - Error connecting to socket");
			e.printStackTrace();
			System.exit(1);
		}

		do {
			try{

				// >>> get a new connection
				socketFromNode = serverSocket.accept();
				System.out.println ("C:receiver - Coordinator has received a request") ;

				// >>> create a separate thread to service the request, a C_Connection_r thread.
				connect = new C_Connection_r(socketFromNode, buffer);
				connect.start();
			}
			catch (IOException e) {
				System.out.println("C:receiver - Exception when creating a connection ");
				e.printStackTrace();
				System.exit(1);
			}
	    
		}
		while (true);
    }

}//end class
