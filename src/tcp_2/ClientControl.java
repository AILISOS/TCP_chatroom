package tcp_2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientControl implements Runnable  {

	// this class implements runnable so that the instances will be executed on a seperate thread

	public static ArrayList<ClientControl> clientControls = new ArrayList<>();
	private Socket socket; //estblishe a connection between client and server
	private BufferedReader bufferedReader; //read data such as messages
	private BufferedWriter bufferedWriter; // send data such as messages, and also broadcast it to other client using the arraylist
	private String clientUsername;
	private String clientPassword;
	
	public ClientControl(Socket socket) {
		try {
			this.socket = socket;
			// We use a buffer to increase out efficiency
			this.bufferedWriter = new BufferedWriter (new OutputStreamWriter(socket.getOutputStream())); // we want a character stream because we are sending messages
			 this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream())); // we want a character stream because we are sending messages 
			this.clientUsername = bufferedReader.readLine(); // Read the client's user name 
			clientControls.add(this); //Add the client to the array list so they can be part of the group chat
			broadcastMessage("SERVER: " + clientUsername + " has entered the chat!");
		} catch (IOException e) {
			closeEverything(socket, bufferedReader, bufferedWriter);
		}
	}
	
	@Override
	public void run() {
		// everything on this tun method is run on a separate thread
		// creating two thread one for waiting for messages, one for working with rest of the application
		
		String messageFromClient;
		
		while (socket.isConnected()) {
			try {
				// Program halts here until we receive a message form a client
				// This is why we want to run it on a separate to keep the rest of the program running
				messageFromClient = bufferedReader.readLine();
				broadcastMessage(messageFromClient);
			} catch (IOException e) {
				closeEverything(socket, bufferedReader, bufferedWriter);
				break;
			}
		}
	}
	
	public void broadcastMessage(String messgaeToSend) {
		// Used to send the message client wrote to everyone
		for (ClientControl clientControl : clientControls) { // Loop through array list, Represent each client control for each iteration 
			try {
				// Broadcast message to everyone except the person wrote it 
				if (!clientControl.clientUsername.equals(clientUsername)) {
					clientControl.bufferedWriter.write(messgaeToSend);
					clientControl.bufferedWriter.newLine(); // finish sending message type enter
					clientControl.bufferedWriter.flush(); // flush buffer writer 
				}
			} catch (IOException e) {
				closeEverything(socket, bufferedReader, bufferedWriter);
			}
		}
	}
	
		// Sends a message where a client leaves the chat
		public void removeClientControl() {
			 clientControls.remove(this);
			 broadcastMessage("SERVER: " + clientUsername + " has left the chat!");
		}
		
		// Close down our connection 
		 public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
			  removeClientControl();
		        try {
		            if (bufferedReader != null) {
		                bufferedReader.close();
		            }
		            if (bufferedWriter != null) {
		                bufferedWriter.close();
		            }
		            if (socket != null) {
		                socket.close();
		            }
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
		    }
		 }

/** Reference
Java Socket Programming (2021) Java Socket Programming - Multiple Clients Chat. Available at:  https://www.youtube.com/watch?v=gLfuZrrfKes&t=1615s (Accessed: 05 May 2022).
 */