import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

public class Logger {

    private String processID, action;

    public Logger(String pid, String action) throws IOException {
        processID = pid;
        this.action = action;

        try {
            printToLog();
        } catch (IOException e) {
            System.out.println("Logger: IO Error when logging");
            e.printStackTrace();
            System.exit(1);
        }
    }

    private synchronized void printToLog() throws IOException {

        try(FileWriter printLog = new FileWriter("log.txt", true);
            PrintWriter pw = new PrintWriter(printLog)){

            pw.append(	  "\nProcess: " + processID
                     + "\t | Timestamp: " + new Date()
                        + " | Action: " + action);

            pw.flush();
        }
        catch (IOException e){
            System.out.println("Logger: IO Error when logging");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
