package communication;

import java.io.*;
import java.net.*;

import communication.ChatController;
import javafx.application.Platform;



// recieves and sends data to other party that is connected
public class RecieveAndSend implements Runnable{
	
	// streams to read and write to socket
	private InputStream input;
	private DataOutputStream output;
	private BufferedReader buffread;
	
	// socket for reading from and writing too
	private Socket socket;
	private ProtocolParser protocolParser;
	private ChatController chatController;
	
	public RecieveAndSend(Socket clientSocket, ChatController chatController){
		this.socket = clientSocket;
		this.chatController = chatController;
		// tells the controller that it is handeling this thread
		chatController.setRecieveAndSend(this);
		this.protocolParser = new ProtocolParser(chatController);
	}
	
	@Override
	public void run() {
		try{
			setupStreams();
			whileReceiving();
		}catch(EOFException e){
			viewMessage("Server terminated the connection", true);
		}
		catch(IOException e){
			e.printStackTrace();
		}finally{
			closeConnection();
		}
	}
	

	private void setupStreams() throws IOException{
		input = socket.getInputStream();
		buffread = new BufferedReader(new InputStreamReader(input));
		output = new DataOutputStream(socket.getOutputStream());
		output.flush();
	}
	

	private void whileReceiving(){
		String message = "You are now connected!";
		viewMessage(message, true);
		ableToType(true); 					
		do{
			try{
				message = buffread.readLine();
				protocolParser.handleMessageProtocoll(message);
			}catch(IOException e){
				e.printStackTrace();
				break;
			}
		} while(message != null && !message.substring(0, 3).equals("END"));
	}
	
	
	// closes the connection
	public void closeConnection(){
		viewMessage("Closing connection...", true);
		ableToType(false);
		try{
			this.output.close();
			this.input.close();
			this.buffread.close();
			this.socket.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	// sending message to the server, method is used from the controller
	public void sendChatMessage(String message){
			try{
				this.output.writeBytes(message +"\r");
				this.output.flush();
			}catch(IOException e){
				e.printStackTrace();
			}
	}
	


	private void viewMessage(final String text, boolean madeByMe){
		Platform.runLater(() -> {chatController.viewMessage(text, madeByMe);});
	}
	
	private void ableToType(final boolean tof){
		Platform.runLater(() -> { chatController.ableToType(tof);});
	}
	
	
}