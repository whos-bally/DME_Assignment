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

				//System.out.println("C:mutex - Buffer size is " + buffer.size());
//				System.out.println("C:mutex - Buffer contents");
//				buffer.show();

				// if the buffer is not empty
				if (buffer.size() != 0) {

					// >>>   Getting the first (FIFO) node that is waiting for a TOKEN form the buffer
					//       Type conversions may be needed.

					nodeHostAddress = (String) buffer.get();
					nodePort = Integer.parseInt((String) buffer.get());


					 // >>>  **** Granting the token
					try {
						grantToken();
					}
					catch (ConnectException e){
						boolean success = false;

						while (!success){
							grantToken();
							success = true;
						}
					}
					catch (IOException e) {
						e.printStackTrace();
						System.out.println("C:mutex - Unable to grant token to node");
					}


					//  >>>  **** Getting the token back
					try {
						// THIS IS BLOCKING !
						s = serverSocketReturn.accept();
						in = s.getInputStream();
						bin = new BufferedReader(new InputStreamReader(in));

						System.out.printf("C:mutex - %s\r\n", bin.readLine());
						// close the socket
						s.close();

					}
					catch (SocketException e){
						e.printStackTrace();
					}
					catch (IOException e) {
						e.printStackTrace();
						System.out.println("CRASH Mutex waiting for the TOKEN back");
					}

				}// endif
			}
			while(true);

		}// end try
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void grantToken() throws SocketException, IOException{
		s = new Socket(nodeHostAddress, nodePort);

		System.out.println("C:connection IN - Mutex connecting to node");
		pw = new PrintWriter(s.getOutputStream());
		pw.print("C:response - Token Granted");
		pw.close();
		s.close();
		System.out.println("C:connection OUT - Mutex socket to node closed");
	}
}//end class
