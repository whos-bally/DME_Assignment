import java.net.*;

public class Coordinator {
	
    public static void main (String[] args){
		int port = 7000;
		
		Coordinator c = new Coordinator ();
		
		try {    
		    InetAddress coordinatorHostAddress = InetAddress.getLocalHost();
		    String coordinatorHostName = coordinatorHostAddress.getHostName();
		    System.out.println ("Coordinator address is "+coordinatorHostAddress);
		    System.out.println ("Coordinator host name is "+coordinatorHostName+"\n\n");
		}
		catch (Exception e) {
			System.err.println("Error in coordinator");
			e.printStackTrace();
		}
				
		// allows defining port at launch time
		if (args.length == 1) port = Integer.parseInt(args[0]);
	
		// Create and run a C_receiver and a C_mutex object sharing a C_buffer object
		C_buffer buffer = new C_buffer();
		C_receiver receiver = new C_receiver(buffer, port);
		C_mutex mutex = new C_mutex(buffer, port);

		// start threads for C_receiver & C_mutex
		System.out.println("Coordinator: running receiver");
		receiver.run();

		System.out.println("Coordinator: running mutex");
		mutex.run();

    }
    
}// end class
