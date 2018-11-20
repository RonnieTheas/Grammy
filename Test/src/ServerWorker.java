import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerWorker {

	void accept()
	{
		int portNumber = 12900;
	
		try
		{
		    ServerSocket serverSocket = new ServerSocket(portNumber);
		    Socket clientSocket = serverSocket.accept();
		    PrintWriter out =
		        new PrintWriter(clientSocket.getOutputStream(), true);
		    BufferedReader in = new BufferedReader(
		        new InputStreamReader(clientSocket.getInputStream()));
		
			
			String inputLine, outputLine;
	        
		    // Initiate conversation with client
		    outputLine = "DATA?";
		    out.println(outputLine);
	
		    while ((inputLine = in.readLine()) != null) {
		        outputLine = "DATA?"+inputLine;
		        out.println(outputLine);
		        if (outputLine.equals("Bye."))
		            break;
		    }
		} catch (IOException e)
		{
			System.out.println("Client connection lost.");//comment
		    
		}
	}
}
