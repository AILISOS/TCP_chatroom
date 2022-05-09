package tcp_2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

	private final ServerSocket serverSocket; // Responsible for listening incoming connection or client and create a
												// socket
	// object to communicate

	public Server(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}

	public void startServer() {
		// some input output error handling
		try {
			// For our server to be constantly running until it is closed
			while (!serverSocket.isClosed()) {
				// while server is running, a method to wait for the client to connect
				Socket socket = serverSocket.accept(); // socket object
				System.out.println("A new client has connected");

				ClientControl clientControls = new ClientControl(socket);

				// this class is responsible for communication between clients
				// Also implements interface runnable, runnable is implemented on a class, each
				// instances is executed on a separate thread

				Thread thread = new Thread(clientControls); // creating a new thread and pass in the instance of our new
															// class clientControl
				thread.start(); // to begin the execution of this thread
			}
		} catch (IOException e) {
			// input output exception with sockets

		}
	}

	// handle nested try catch error, if an error occurs we shut down out server
	public void closeServerSocket() {
		try {
			if (serverSocket != null) {
				serverSocket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Run the program.
	public static void main(String[] args) throws IOException {
		ServerSocket serverSocket = new ServerSocket(1500);
		Server server = new Server(serverSocket);
		server.startServer();
	}

}

/** Reference
Java Socket Programming (2021) Java Socket Programming - Multiple Clients Chat. Available at:  https://www.youtube.com/watch?v=gLfuZrrfKes&t=1615s (Accessed: 05 May 2022).
 */
