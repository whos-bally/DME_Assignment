import java.net.*;
import java.io.*;
import java.util.*;

/**
 * Blueprint class for a node
 */
public class Node{

	// class variables
    private Socket socket;
    private ServerSocket nodeServerSocket;
    private Socket nodeToken;
	private String coordinatorHostAddress = "127.0.0.1";
	private int coordinatorRequestPort = 7000;
	private int coordinatorReturnPort = 7001;
	private String nodeHostAddress = "127.0.0.1";
	private String nodeHostName;
	private int nodePort;
	private Random random;
	private PrintWriter pw = null;
	private boolean hasToken = false;
    public Node(String nHostName, int nPort, int waitTime, int shutdown) throws InterruptedException, IOException {

		// setup class variables
		random = new Random();
		this.nodeHostName = nHostName;
		this.nodePort = nPort;

		// print node details
    	System.out.println("Node " + nodeHostName + ":" + nodePort + " of DME is active ....");

    	 /**
		  * NODE sends nodeHostName and nodePort through a 'socket' to the coordinator
		  * located at coordinatorHostAddress:coordinatorRequestPort
		  * and immediately opens a server socket through which will receive
		  * a TOKEN (actually just a synchronization).
		  **/
    
    	while(!hasToken){

			if (isCoordinatorAlive()){
				try{
					// >>>  sleep a random number of seconds linked to the waitTime value
					Thread.sleep(random.nextLong(waitTime));
				}
				catch (InterruptedException e){
					System.out.println("Node: Sleeping thread interrupted");
					e.printStackTrace();
				}

				// **** Send to the coordinator a token request.
				try {
					socket = new Socket(nodeHostAddress, coordinatorRequestPort);

					// send your ip address and port number
					pw = new PrintWriter(socket.getOutputStream());
					pw.println(nodeHostAddress);
					pw.flush();
					pw.println(nodePort);
					pw.close();
					socket.close();
				}
				catch (IOException e) {
					e.printStackTrace();
					System.exit(1);
				}

				// **** Then Wait for the token
				try{
					System.out.println("Node: Waiting for token");

					// wait for a token to be granted from C_mutex
					nodeServerSocket = new ServerSocket(nodePort);
					nodeToken = nodeServerSocket.accept();

					System.out.printf("Node: Connection IN - accepted socket, %s\r\n", nodeToken.toString());
					String cResponse = null;

					// read the InputStream of the socket for coordinator response
					BufferedReader br = new BufferedReader(new InputStreamReader(nodeToken.getInputStream()));
					cResponse = br.readLine(); // Coordinator response

					// server acknowledgement
					System.out.printf("Node: %s\r\n", cResponse);
				}
				catch (ConnectException e){
					boolean success = false;

					while (!success){
						// wait for a token to be granted from C_mutex
						nodeServerSocket = new ServerSocket(nodePort);
						nodeToken = nodeServerSocket.accept();

						System.out.printf("Node: Connection IN - accepted socket, %s\r\n", nodeToken.toString());
						String cResponse = null;

						// read the InputStream of the socket for coordinator response
						BufferedReader br = new BufferedReader(new InputStreamReader(nodeToken.getInputStream()));
						cResponse = br.readLine(); // Coordinator response

						// server acknowledgement
						System.out.printf("Node: %s\r\n", cResponse);
						success = true;
					}
				}
				catch (Exception e){
					System.out.println("Node: Error receiving token");
					e.printStackTrace();
					System.exit(1);
				}

				hasToken = true;

				// **** Sleep for a while
				// This is the critical session
				System.out.println("Node: Entering CS");
				new Logger("Node", "Entering critical section");
				Thread.sleep(random.nextLong(waitTime));
				System.out.println("Node: Exited CS");

				// **** Return the token
				try {
					System.out.println("Node: Returning token");
					socket = new Socket(coordinatorHostAddress, coordinatorReturnPort);
					pw = new PrintWriter(socket.getOutputStream());
					pw.println("Node: Token return");
					pw.close();
					socket.close();
					System.out.println("Node: Token returned");
					new Logger("Node", "Token returned");

				}
				catch (IOException e){
					System.out.println("Node: Error returning token");
					e.printStackTrace();
				}

				if (shutdown == 1){
					Socket s = new Socket(nodeHostAddress, 7002);
					pw = new PrintWriter(s.getOutputStream());
					pw.print("shutdown");
					pw.close();
				}
			}
			else {
				System.out.println("Node: Coordinator is down, node shutting down");
				System.exit(0);
			}
    	}
    }

	private boolean isCoordinatorAlive(){
		try {
			// Test for successful connection
			Socket socket = new Socket(coordinatorHostAddress, coordinatorRequestPort);
			socket.close();
			return true;
		} catch (IOException e) {
			// Connection failed, Coordinator down
			return false;
		}
	}
    
    public static void main (String args[]) throws InterruptedException, IOException {
		String nodeHostName = "";
		int nodePort;
		
		// port and millisec (average waiting time) are specific of a node
		if ((args.length < 1) || (args.length > 3)){
		    System.out.print("Usage: Node [port number] [wait time (ms)] [system shutdown? 1 or 0]");
		    System.exit(1);
		}
		
		// get the IP address and the port number of the node
	 	try{ 
		    InetAddress nodeInetAddress =  InetAddress.getLocalHost() ;
		    nodeHostName = nodeInetAddress.getHostName();
		    System.out.println ("Node hostname is " + nodeHostName + ":" + nodeInetAddress);
		}
		catch (UnknownHostException e){
		    e.printStackTrace();
		    System.exit(1);
		}
		
		nodePort = Integer.parseInt(args[0]);
		System.out.println ("Node port is "+nodePort);

		// construct node
	    Node n = new Node(nodeHostName, nodePort, Integer.parseInt(args[1]), Integer.parseInt(args[2]));
    }

}//end class
