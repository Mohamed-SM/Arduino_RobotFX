package application;


import com.fazecast.jSerialComm.SerialPort;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;

public class uiController {
	
	@FXML
	ComboBox<String> comBox;
	
	@FXML
	Spinner<Integer> blinks;
	
	@FXML
	Spinner<Integer> delay;
	
	
	@FXML
	Button connectionButton;
	
	@FXML
	TextArea textArea;
	
	private Main main;
	
	@FXML
	private void initialize() {
		blinks.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1,99,1));
		delay.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(50, 5000, 500, 50));
	}
	
	public void fillComboBox() {
		comBox.getItems().clear();
		comBox.setPromptText("select a port");
		for (SerialPort port : main.serPort) {
			comBox.getItems().add(port.getSystemPortName());
		}
	}
	
	@FXML
	public void onConnetClick() {
		if( connectionButton.getText().equals("connection") ) {
			
			if (comBox.getSelectionModel().getSelectedIndex()>-1) {
				main.connect(comBox.getSelectionModel().getSelectedIndex());
				connectionButton.setText("Deconnection");
				comBox.setDisable(true);
			}else {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("connection Error");
				alert.setContentText("no selected port");
				alert.show();
			}
			
			
		}else {
			main.setPin(14,1,100);
			main.Desconnect();
			connectionButton.setText("connection");
			comBox.setDisable(false);
			
		}
	}
	
	@FXML
	public void onPinButtonClic(ActionEvent event) {
		Button pinButton = (Button)event.getSource();
		int pin = Integer.parseInt(pinButton.getText());
		main.setPin(pin,blinks.getValue(),delay.getValue());
	}
	
	public void appandText(String text) {
		textArea.appendText(text+"\n");
	}
	
	public void setMainApp(Main main){
		this.main = main;
	}

}
