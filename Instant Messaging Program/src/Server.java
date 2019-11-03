import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Server extends JFrame {
	
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerSocket server;
	private Socket connection;
	
	public Server() {
		
		super("Instant Messenger");
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(
				new ActionListener() {
					//Place for user to write and send message
					public void actionPerformed(ActionEvent event) {
						//Send message
						sendMessage(event.getActionCommand());
						//Write text
						userText.setText("");
					}
				}
		);
		//Add all the above to screen
		add(userText, BorderLayout.NORTH);
		//Main window where all messages are displayed
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow));
		setSize(300,150);
		setVisible(true);
		
	}
	
	//set up and run the server
	public void startRunning() {
		
		try {
			//Backlog
			server = new ServerSocket(6789, 100);
			while(true) {
				try {
					//Wait for other person
					waitForConnection();
					//After connecting
					setupStreams();
				} catch(EOFException eofException) {
					showMessage("\n Server ended the connection! ");
				} finally {
					closeIt();
				}
			}
		} catch(IOException ioException) {
			ioException.printStackTrace();
		}
		
	}
	
	//Wait for connection, then display connection information 
	private void waitForConnection() throws IOException {
		showMessage("Waiting for someone to connect....\n");
		//Accept a connection with someone and create socket
		connection = server.accept();
		//After socket is created
		showMessage("Now connected to " + connection.getInetAddress().getHostName());
	}
	
	//Get stream to send and receive data
	private void setupStreams() throws IOException {
		
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n Streams are now setup \n");
		
	}
	
	//During the chat conversation
	private void whileChatting() throws IOException {
		
		String message = "You are now connected";
		sendMessage(message);
		//If connected
		ableToType(true);
		do {
			//Have a conversation
			try {
				//message from the other person
				message = (String) input.readObject();
				showMessage("\n" + message);
			} catch(ClassNotFoundException classNotFoundException) {
				//If message cannot be converted into a string
				showMessage("\n Not found ");
			}
		} while(!message.equals("Client - End"));
		
	}
	
	public void closeIt() {
		
		showMessage("\n Closing connection... \n");
		//When not connected
		ableToType(false);
		//To shut down everything
		try {
			output.close();
			input.close();
			connection.close();
		} catch(IOException ioException) {
			ioException.printStackTrace();
		}
		
	}
	
	//Send a message to client
	private void sendMessage(String message) {
		
		try {
			//To send message to someone
			output.writeObject("Server: " + message);
			output.flush();
			sendMessage("Server: " + message);
		} catch(IOException ioException) {
			//If unable to send message
			chatWindow.append("\n ERROR: Unable to send message ");
		}

	}
	
	//Updates chat window
	private void showMessage(final String text) {
		
		//Create thread
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					//To add a new line of text at the bottom of the chat window
					chatWindow.append(text);
				}
			}
				
		);
		
	}
	
	//Let the user type into their box
	private void ableToType(final boolean tof) {
		
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					userText.setEditable(tof);
				}
			}	
		);
		
	}

}
