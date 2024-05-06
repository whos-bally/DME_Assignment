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
    public Node(String nHostName, int nPort, int waitTime) throws InterruptedException {

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
			catch (Exception e){
				e.printStackTrace();
			}

			// **** Sleep for a while
			// This is the critical session
			System.out.println("Node: Entering CS");

			try{
				new Logger("Node", "Entering critical section");
			}
			catch (IOException e){
				System.out.println("Node: Error logging CS");
				e.printStackTrace();
				System.exit(1);
			}


			Thread.sleep((long) random.nextLong(waitTime));
			System.out.println("Node: Exited CS");

			// **** Return the token
			try {
				System.out.println("Node: Returning token");
				socket = new Socket(coordinatorHostAddress, coordinatorReturnPort);
				pw = new PrintWriter(socket.getOutputStream());
				pw.println("Node: Token returned");
				pw.close();
				socket.close();
				System.out.println("Node: Token returned");
				new Logger("Node", "Token returned");
				hasToken = true;
			}
			catch (IOException e){
				System.out.println("Node: Error returning the token or logging the token being returned");
				e.printStackTrace();
				System.exit(1);
			}
    	}
    }
    
    public static void main (String args[]) throws InterruptedException {
		String nodeHostName = "";
		int nodePort;
		
		// port and millisec (average waiting time) are specific of a node
		if ((args.length < 1) || (args.length > 2)){
		    System.out.print("Usage: Node [port number] [wait time (ms)]");
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
	    Node n = new Node(nodeHostName, nodePort, Integer.parseInt(args[1]));
    }

}//end class
