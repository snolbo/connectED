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
		chatController.setRecieveAndSendConnection(this);
		this.protocolParser = new ProtocolParser(chatController);
	}
	
	@Override
	public void run() {

			
		try{
			setupStreams();
			
			
			if(!chatController.isHost()){
//				try {
//					Thread.sleep(2000); // THIS IS FUCKING BAD SOLUTION, HELPER WILL NOT GET WEBSITE OF SENDCODEURL IF IT IS CALLED BEFORE THE SITE AT STUDENT IS LOADED
						Platform.runLater( () ->{
							if(chatController.codeEditorFinishedLoading())
								chatController.sendCodeURL();
							else
								chatController.sendCodeURLWhenLoaded();
						});

//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}finally{
//					
//					Platform.runLater( ()->{
//						chatController.sendCodeURLWhenFinishedLoading();
//					});
//				}
			}
			
			
			
			
			
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
	
	
	public void sendCodeUrl(String URL){
		System.out.println("sending:" + URL);
		sendChatMessage(URL);
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
				if(socket.isClosed()){
					System.out.println("Socket is closed so we stop looping in whileReceiving");
					break;
				}
				else{
					e.printStackTrace(); //cathes the socket closed exception when tabing out
				}
			}
		} while(message != null && !socket.isClosed());
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
				this.output.writeBytes(message +"\r\n");
				this.output.flush();
			}catch(IOException e){
				 e.printStackTrace();
				 if(socket.isClosed())
					 closeConnection();
			}
	}
	


	private void viewMessage(final String text, boolean madeByMe){
		Platform.runLater(() -> {chatController.viewMessage(text, madeByMe);});
	}
	
	private void ableToType(final boolean tof){
		Platform.runLater(() -> { chatController.ableToType(tof);});
	}

	
}