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
	private PrintWriter printWriter = null;
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
    
    	while(true){


			try{
				// >>>  sleep a random number of seconds linked to the waitTime value
				Thread.sleep(random.nextLong(waitTime));
			}
			catch (InterruptedException e){
				System.out.println("Node: Sleeping thread interrupted");
				e.printStackTrace();
			}

    		try {
				// **** Send to the coordinator a token request.
				socket = new Socket(nodeHostAddress, coordinatorRequestPort);

				// send your ip address and port number
				printWriter = new PrintWriter(socket.getOutputStream());
				printWriter.println(nodeHostAddress);
				printWriter.flush();
				printWriter.println(nodePort);
				printWriter.close();

		    	// **** Then Wait for the token
		    	// Print suitable messages
				System.out.println("Node: Waiting for token");
				tokenRequest();

		    	// **** Sleep for a while
		    	// This is the critical session
				Thread.sleep(random.nextLong(waitTime));

		    	// **** Return the token
		    	// Print suitable messages - also considering communication failures

    		}
    		catch (IOException e) {
		    System.out.println(e);
		    System.exit(1);
    		}
    	}
    }

	private synchronized void tokenRequest(){
		try{
			// wait for a token to be granted from C_mutex
			nodeServerSocket = new ServerSocket(nodePort);
			nodeToken = nodeServerSocket.accept();
			String cResponse = null;

			while (cResponse == null){
				BufferedReader br = new BufferedReader(new InputStreamReader(nodeToken.getInputStream()));
				cResponse = br.readLine(); // Coordinator response
			}

			// server acknowledgement
			System.out.printf("Node: %s\r\n", cResponse);
			nodeToken.close();
		}
		catch (IOException e){
			e.printStackTrace();
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
