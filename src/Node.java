import java.net.*;
import java.io.*;
import java.util.*;

public class Node{

    private Random random;
    private Socket socket;
    private PrintWriter printWriter = null;
    private ServerSocket nodeServerSocket;
    private Socket nodeToken;
	private String coordinatorHostAddress = "127.0.0.1";
	private int coordinatorRequestPort = 7000;
	private int coordinatorReturnPort = 7001;
	private String nodeHostAddress = "127.0.0.1";
	private String nodeHostName;
	private int nodePort;
    
    public Node(String nodeHostName, int nodePort, int waitTime){
		random = new Random();
		this.nodeHostName = nodeHostName;
		this.nodePort = nodePort;
	
    	System.out.println("Node " + this.nodeHostName + ":" + this.nodePort + " of DME is active ....");

    	// NODE sends nodeHostName and nodePort through a 'socket' to the coordinator
    	// located at coordinatorHostAddress:coordinatorRequestPort
    	// and immediately opens a server socket through which will receive 
    	// a TOKEN (actually just a synchronization).
    
//    	while(true){
//
//	    // >>>  sleep a random number of seconds linked to the waitTime value
//
//    		try {
//
//		    // **** Send to the coordinator a token request.
//		    // send your ip address and port number
//
//		    // **** Then Wait for the token
//		    // Print suitable messages
//
//		    // **** Sleep for a while
//		    // This is the critical session
//
//		    // **** Return the token
//		    // Print suitable messages - also considering communication failures
//
//    		}
//    		catch (IOException e) {
//		    System.out.println(e);
//		    System.exit(1);
//    		}
//    	}
    }
    
    public static void main (String args[]){
		String nodeHostName = "";
		int nodePort;
		
		// port and millisec (average waiting time) are specific of a node
		if ((args.length < 1) || (args.length > 2)){
		    System.out.print("Usage: [node] [port number] [wait time (ms)]");
		    System.exit(1);
		}
		
		// get the IP address and the port number of the node
	 	try{ 
		    InetAddress nodeInetAddress =  InetAddress.getLocalHost() ;
		    nodeHostName = nodeInetAddress.getHostName();
		    System.out.println ("Node hostname is " + nodeHostName + ":" + nodeInetAddress);
		}
		catch (UnknownHostException e){
		    System.out.println(e);
		    System.exit(1);
		}
		
		nodePort = Integer.parseInt(args[0]);
		System.out.println ("node port is "+nodePort);
	    Node n = new Node(nodeHostName, nodePort, Integer.parseInt(args[1]));
    }

}//end class
