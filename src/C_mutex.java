import java.io.*;
import java.net.*;

public class C_mutex extends Thread{
    private C_buffer buffer;
	private Socket s;
	private int port;
	private PrintWriter pw;
	private InputStream in;
	private BufferedReader bin;

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


			do {
				// >>> Print some info on the current buffer content for debugging purposes.
				// >>> please look at the available methods in C_buffer

				System.out.println("C:mutex - Buffer size is " + buffer.size());

				// if the buffer is not empty
				if (buffer.size() != 0) {

					// >>>   Getting the first (FIFO) node that is waiting for a TOKEN form the buffer
					//       Type conversions may be needed.

					nodeHostAddress = (String) buffer.get();
					nodePort = Integer.parseInt((String) buffer.get());


					 // >>>  **** Granting the token
					try {
						s = new Socket(nodeHostAddress, nodePort);

						System.out.println("C:connection IN - Mutex connecting to node");
						pw = new PrintWriter(s.getOutputStream());
						pw.print("C:response - Token Granted");
						pw.close();
						s.close();
						System.out.println("C:connection OUT - Mutex socket to node closed");
					}
					catch (IOException e) {
						System.out.println(e);
						System.out.println("CRASH Mutex connecting to the node for granting the TOKEN" + e);
					}


					//  >>>  **** Getting the token back
					try {
						// THIS IS BLOCKING !
						System.out.printf("C:serversocket - Heartbeat status: Bound[%s]\r\n", serverSocketReturn.isBound());
						s = serverSocketReturn.accept();
						in = s.getInputStream();
						bin = new BufferedReader(new InputStreamReader(in));

						System.out.printf("C:mutex - %s\r\n", bin.readLine());

						// close the socket
						s.close();

					} catch (IOException e) {
						System.out.println(e);
						System.out.println("CRASH Mutex waiting for the TOKEN back" + e);
					}

				}// endif
			}
			while(!(buffer.size() == 0));

		}// end try
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}//end class
