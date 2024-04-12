import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;

public class Coordinator {

	public boolean shutdownRequested = false;


	
    public static void main (String[] args) throws IOException {
		int port = 7000;
		
		Coordinator c = new Coordinator ();
		Socket shutdownReq;
		InputStream in;
		BufferedReader bin;
		String request;
		ServerSocket shutdown = null;

		// Open a server socket for graceful shutdown request
		try{
			shutdown = new ServerSocket(7002);
			System.out.println("Coordinator listening for shutdown requests");
		}
		catch (IOException e){
			System.out.println("Error receiving shutdown request");
			e.printStackTrace();
		}

		// >>> Start Coordinator
		try {    
		    InetAddress coordinatorHostAddress = InetAddress.getLocalHost();
		    String coordinatorHostName = coordinatorHostAddress.getHostName();
		    System.out.println ("Coordinator address is "+coordinatorHostAddress);
		    System.out.println ("Coordinator host name is "+coordinatorHostName+"\n\n");
		}
		catch (Exception e) {
			System.err.println("Error in Coordinator");
			e.printStackTrace();
			System.exit(1);
		}
				
		// allows defining port at launch time
		if (args.length == 1) port = Integer.parseInt(args[0]);
	
		if(!c.shutdownRequested){
			// Create and run a C_receiver and a C_mutex object sharing a C_buffer object
			C_buffer buffer = new C_buffer();
			C_receiver receiver = new C_receiver(buffer, port);
			C_mutex mutex = new C_mutex(buffer, port);

			// start threads for C_receiver & C_mutex
			System.out.println("Coordinator: running receiver");
			receiver.start();

			System.out.println("Coordinator: running mutex");
			mutex.start();

			while(true){
				shutdownReq = shutdown.accept();
				System.out.println("Coordinator: IN connection on shutdown socket");

				in = shutdownReq.getInputStream();
				bin = new BufferedReader(new InputStreamReader(in));
				request = bin.readLine();
				if (request.equals("shutdown")){
					c.shutdownRequested = true;
				}
			}
		}
		else {
			System.out.println("Coordinator: shutting down system");
			System.exit(0);
		}



    }
    
}// end class
