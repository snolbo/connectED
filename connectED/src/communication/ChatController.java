package communication;

import T2.ServerRequest;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;


// Controls the chat of one connection
public class ChatController {

	@FXML private TextArea userText;
	@FXML private ListView<Label> chatWindow;
	
	private RecieveAndSend receiveAndSend = null;
	private boolean isAssistantHost= false;
	private boolean isHelperHost = false;
	

	public boolean isHelperHost() {
		return isHelperHost;
	}
	
	public boolean isAssistantHost() {
		return isAssistantHost;
	}

	public void setHelperHost(boolean isHelperHost) {
		this.isHelperHost = isHelperHost;
	}
	
	public void setAssistantHost(boolean isAssistantHost) {
		this.isAssistantHost = isAssistantHost;
	}
	
	@FXML // listens to keyevents in userText, if keyevent is enter, send CHAT protovol and the text
	public void handleChatEnterKey(KeyEvent event) {
		if(event.getCode() == KeyCode.ENTER ){
			String message = userText.getText();
			Platform.runLater(() -> {receiveAndSend.sendChatMessage("CHAT-" + message);});
			viewMessage(message, true);
			event.consume();
		}
	}
	
	// used by tab creates in serverChatController on closeRequest of tab
	public void onClosed(String tag) {
		if(receiveAndSend != null){
			receiveAndSend.sendChatMessage("END-null");
			receiveAndSend.closeConnection();
		}
		if(isHelperHost && receiveAndSend == null){
			ServerRequest request = new ServerRequest(tag + "Delete");
			request.removeAdressFromQueue();
		}
		else if(isAssistantHost && receiveAndSend == null){
			ServerRequest request = new ServerRequest(tag + "Delete");
			request.removeAdressFromQueue();
		}
		else if(receiveAndSend == null){
			ServerRequest request = new ServerRequest(tag + "Delete");
			request.removeAdressFromQueue();
		}
	}
	
	// shows message in chatWIndow
	public void viewMessage(String text, boolean madeByMe){  // TODO: want the label to be smaller, wrap text, and positioned at one side
		Label message = new Label(text);
		message.prefWidthProperty().bind(chatWindow.widthProperty().subtract(25));
		message.setWrapText(true);
		if(madeByMe) {
			message.setTextFill(Color.WHITE);
			message.setBackground(new Background(new BackgroundFill(Color.web("#4678FB"), new CornerRadii(5), null)));
		}
		else {
			message.setTextFill(Color.BLACK);
			message.setBackground(new Background(new BackgroundFill(Color.web("#EFEEEE"), new CornerRadii(5), null)));
		}	
		Platform.runLater(() -> {chatWindow.getItems().add(message);});
		this.userText.clear();
	}

	// sets a serverCOnnection to this chattab
	public void setRecieveAndSendConnection(RecieveAndSend connection){
		this.receiveAndSend = connection;
	}

	public void ableToType(boolean tof) {
		this.userText.setEditable(tof);
	}
	
}
