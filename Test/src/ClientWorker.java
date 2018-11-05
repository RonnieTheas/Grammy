import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JTextArea;

class ClientWorker implements Runnable {
	private Socket client;
	private JTextArea textArea;

	public static String HOST_NAME = "localhost";
	public static int PORT_NUMBER = 12900;

	// Constructor
	ClientWorker(JTextArea textArea) {
		this.textArea = textArea;
	}

	public void run() {
		String line;
		BufferedReader in = null;
		PrintWriter out = null;
		try {
			client = new Socket(HOST_NAME, PORT_NUMBER);
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			out = new PrintWriter(client.getOutputStream(), true);
		} catch (IOException e) {
			System.out.println("in or out failed");
			System.exit(-1);
		}

		while (true) {
			try {
				line = in.readLine();
				// Send data back to client
				out.println(line);
				// Append data to text area
				textArea.append(line);
			} catch (IOException e) {
				System.out.println("Read failed");
				System.exit(-1);
			}
		}
	}
}