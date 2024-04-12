import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;

public class C_mutex extends Thread{
    private C_buffer buffer;
	private Socket s;
	private int port;
	private PrintWriter pw;

    // ip address and port number of the node requesting the token.
    // They will be fetched from the buffer    
	private String nodeHostAddress;
	private int nodePort;
	
    public C_mutex (C_buffer b, int p){
		buffer = b;
		port = p;
    }

    public void run() {

		try {
			//  >>>  Listening from the server socket on port 7001
			// from where the TOKEN will be returned later.
			ServerSocket serverSocketReturn = new ServerSocket(7001);


			while (true) {
				// >>> Print some info on the current buffer content for debugging purposes.
				// >>> please look at the available methods in C_buffer

				System.out.println("C:mutex - Buffer size is " + buffer.size());

				// if the buffer is not empty
				if (buffer.size() != 0) {
					System.out.println("C:mutex - buffer not empty");

					// >>>   Getting the first (FIFO) node that is waiting for a TOKEN form the buffer
					//       Type conversions may be needed.
					String[] bufferRequest = new String[2];
					bufferRequest = (String[]) buffer.get();
					nodeHostAddress = bufferRequest[0];
					nodePort = Integer.parseInt(bufferRequest[1]);


					 // >>>  **** Granting the token
					System.out.println("C:mutex - Connecting to node");

					try {
						s = new Socket(nodeHostAddress, nodePort);
						pw = new PrintWriter(s.getOutputStream());
						pw.print("C:mutex - Token Granted");
						pw.close();
						s.close();
					}
					catch (IOException e) {
						System.out.println(e);
						System.out.println("CRASH Mutex connecting to the node for granting the TOKEN" + e);
					}


					//  >>>  **** Getting the token back
//					try {
//						// THIS IS BLOCKING !
//					} catch (IOException e) {
//						System.out.println(e);
//						System.out.println("CRASH Mutex waiting for the TOKEN back" + e);
//					}
				}// endif
			}// endwhile
		}// end try
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}//end class
