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


			while(true) {
				// >>> Print some info on the current buffer content for debugging purposes.
				// >>> please look at the available methods in C_buffer

				System.out.println("C:mutex - Buffer contents");
				buffer.show();


				synchronized (buffer) {
					// Wait until the buffer is not empty
					while (buffer.size() == 0) {
						try {
							// Wait for notifications from other threads
							buffer.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

					// >>>   Getting the first (FIFO) node that is waiting for a TOKEN form the buffer
					//       Type conversions may be needed.

					nodeHostAddress = (String) buffer.get();
					nodePort = Integer.parseInt((String) buffer.get());

				}

				// >>>  **** Granting the token
				try {
					grantToken();
				}
				catch (ConnectException e){
					System.out.println("C:mutex - Unable to connect to node, attempting to try again...");
					boolean success = false;

					while (!success){
						grantToken();
						success = true;
					}
				}
				catch (IOException e) {
					System.out.println("C:mutex - Unable to grant token to node");
					e.printStackTrace();
				}


				//  >>>  **** Getting the token back
				try {

					s = serverSocketReturn.accept();
					in = s.getInputStream();
					bin = new BufferedReader(new InputStreamReader(in));

					System.out.printf("C:mutex - %s\r\n", bin.readLine());
					// close the socket
					s.close();

				}
				catch (SocketException e){
					System.out.println("C:mutex - Connection error receiving token back");
					e.printStackTrace();
				}
				catch (IOException e) {
					System.out.println("CRASH: Mutex waiting for the TOKEN back");
					e.printStackTrace();
				}

			}

		}// end try
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void grantToken() throws IOException, InterruptedException {
		sleep(3000); // sleep for 3 seconds to allow for OS to close ports in TIME_WAIT

		// then open a socket to the node
		s = new Socket(nodeHostAddress, nodePort);

		System.out.println("C:mutex - Connecting to Node");
		pw = new PrintWriter(s.getOutputStream());
		pw.print("C:mutex - Token Granted");
		pw.close();
		new Logger("Coordinator", "Token granted");
		s.close();
		System.out.println("C:mutex - Socket to Node closed on port " + nodePort);
	}
}//end class
