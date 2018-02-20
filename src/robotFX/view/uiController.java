package robotFX.view;


import com.fazecast.jSerialComm.SerialPort;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import robotFX.Main;
import robotFX.model.Pose;

import java.util.Optional;

public class uiController {

    @FXML
    ComboBox<String> comBox;

    @FXML
    Button connectionButton;

    @FXML
    Label baseLabel;
    @FXML
    Label shoulderLabel;
    @FXML
    Label elbowLabel;
    @FXML
    Label pinchLabel;

    @FXML
    Slider baseSlider;
    @FXML
    Slider shoulderSlider;
    @FXML
    Slider elbowSlider;
    @FXML
    Slider pinchSlider;

    @FXML
    TextArea textArea;

    @FXML
    TableView<Pose> poseTable;

    @FXML
    TableColumn<Pose,String> poseNameColumn;
    @FXML
    TableColumn<Pose,Integer> poseBaseColumn;
    @FXML
    TableColumn<Pose,Integer> poseShoulderColumn;
    @FXML
    TableColumn<Pose,Integer> poseElbowColumn;
    @FXML
    TableColumn<Pose,Integer> posePinchColumn;
    
    @FXML
    Slider XSlider;
    @FXML
    Slider YSlider;
    @FXML
    Slider ZSlider;
    
    @FXML
    TabPane cordsTabPan;
    @FXML
    Tab anglesTab;

    @FXML
    AnchorPane objRecognitionPan;
    
    @FXML
    Tab XYZTab;

    private String textToSet = "";
    private Main main;
    
    private Pose pose;

    @FXML
    private void initialize() {
    	
    	pose = new Pose((int) baseSlider.getValue(),
                (int) shoulderSlider.getValue(),
                (int) elbowSlider.getValue(),
                (int) pinchSlider.getValue());
    	
    	pose.baseProperty().bindBidirectional(baseSlider.valueProperty());
    	pose.shoulderProperty().bindBidirectional(shoulderSlider.valueProperty());
    	pose.elbowProperty().bindBidirectional(elbowSlider.valueProperty());
    	pose.pinchProperty().bindBidirectional(pinchSlider.valueProperty());

    	pose.xProperty().bindBidirectional(XSlider.valueProperty());
        pose.yProperty().bindBidirectional(YSlider.valueProperty());
        pose.zProperty().bindBidirectional(ZSlider.valueProperty());

    	cordsTabPan.getSelectionModel().selectedItemProperty().addListener((ob, ol, newValue) -> {
    	    if(newValue == anglesTab){
                pose.sync();
            }
        });
    	
        poseNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        poseBaseColumn.setCellValueFactory(cellData -> cellData.getValue().baseProperty().asObject() );
        poseShoulderColumn.setCellValueFactory(cellData -> cellData.getValue().shoulderProperty().asObject() );
        poseElbowColumn.setCellValueFactory(cellData -> cellData.getValue().elbowProperty().asObject() );
        posePinchColumn.setCellValueFactory(cellData -> cellData.getValue().pinchProperty().asObject() );

        //set the position from table click
        poseTable.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> setPoseFromTable(newValue));


        //move on tow clicks
        poseTable.setRowFactory(tv -> {
            TableRow<Pose> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    this.onMoveButtonClic();
                }
            });
            return row;
        });

        poseTable.setOnKeyPressed(keyEvent -> {

            if (keyEvent.getCode().equals(KeyCode.DELETE)) {
                uiController.this.handleDeletePose();
            }else if(keyEvent.getCode().equals(KeyCode.ENTER)) {
                uiController.this.onMoveButtonClic();
            }

            // ... other keyevents
        });
        
        textArea.textProperty().addListener(this::changed);

        

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
        if (connectionButton.getText().equals("connection")) {

            if (comBox.getSelectionModel().getSelectedIndex() > -1) {
                main.connect(comBox.getSelectionModel().getSelectedIndex());
                connectionButton.setText("Deconnection");
                comBox.setDisable(true);
            } else {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("connection Error");
                alert.setContentText("no selected port");
                alert.show();
            }


        } else {
            main.desconnect();
            connectionButton.setText("connection");
            comBox.setDisable(false);

        }
    }

    @FXML
    public void onMoveButtonClic() {
        if (checkConnection()) {
            if(cordsTabPan.getSelectionModel().getSelectedItem() == XYZTab){

                pose.sync();

                System.out.println("printing pose after sync" + pose);
            }
            main.move(pose.getCopy());
        }
    }

    public void appandText(String text) {
        textArea.appendText(text + "\n");
    }

    public void setMainApp(Main main) {
        this.main = main;
        poseTable.setItems(this.main.getSavedPoses());
    }

    public void onSaveClick(){
        System.out.println("on save click");
        System.out.println("printing pose befor sync" + pose);
        if(cordsTabPan.getSelectionModel().getSelectedItem() == XYZTab){

            pose.sync();

            System.out.println("printing pose after sync" + pose);
        }
        this.main.savedPoses.add(pose.getCopy());
        System.out.println("printing last pose added " + this.main.savedPoses.get(this.main.savedPoses.size() - 1));
    }

    public void onSaveClickHere(){
        int selectedIndex = poseTable.getSelectionModel().getSelectedIndex();

        if (selectedIndex != -1) {
            this.main.savedPoses.get(selectedIndex).setBase((int) baseSlider.getValue());
            this.main.savedPoses.get(selectedIndex).setShoulder((int) shoulderSlider.getValue());
            this.main.savedPoses.get(selectedIndex).setElbow((int) elbowSlider.getValue());
            this.main.savedPoses.get(selectedIndex).setPinch((int) pinchSlider.getValue());
        } else {
            // Nothing selected.
            Alert alert = new Alert(AlertType.WARNING);
            alert.initOwner(main.getPrimaryStage());
            alert.setTitle("Pas de Selection");
            alert.setHeaderText("aucun Pose n'est selectioner");
            alert.setContentText("SVP selectioner un Pose.");
            alert.showAndWait();
        }
    }

    private void setPoseFromTable(Pose poseFromTable){

        if (poseTable.getSelectionModel().getSelectedIndex() > -1){
            pose.setFromPose(poseFromTable.getCopy());
        }
    }

    private void handleDeletePose() {
        int selectedIndex = poseTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.initOwner(main.getPrimaryStage());
            alert.setTitle("Comfirmation");
            alert.setHeaderText("vous etes sur de supprimer cette Pose");
            ButtonType buttonTypeYes = new ButtonType("Oui");
            ButtonType buttonTypeNo = new ButtonType("Annuler");
            alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);
            Optional<ButtonType> result = alert.showAndWait();
            if (buttonTypeYes != result.get()) return;
            else {
                poseTable.getItems().remove(selectedIndex);
            }

        } else {
            // Nothing selected.
            Alert alert = new Alert(AlertType.WARNING);
            alert.initOwner(main.getPrimaryStage());
            alert.setTitle("Pas de Selection");
            alert.setHeaderText("aucun Pose n'est selectioner");
            alert.setContentText("SVP selectioner un Pose.");
            alert.showAndWait();
        }
    }

    public void onMoveToClick(){
        if(! checkConnection()) return;
        int selectedIndex = poseTable.getSelectionModel().getSelectedIndex();

        if (selectedIndex != -1) {

            pose.setFromPose(this.main.savedPoses.get(selectedIndex).getCopy());
            this.main.move(pose.getCopy());
        } else {
            // Nothing selected.
            Alert alert = new Alert(AlertType.WARNING);
            alert.initOwner(main.getPrimaryStage());
            alert.setTitle("Pas de Selection");
            alert.setHeaderText("aucun Pose n'est selectioner");
            alert.setContentText("SVP selectioner un Pose.");
            alert.showAndWait();
        }
    }

    public void onNextClick(){
        if(! checkConnection()) return;
        int selectedIndex = poseTable.getSelectionModel().getSelectedIndex();
        if(selectedIndex == -1){
            poseTable.getSelectionModel().selectFirst();
            selectedIndex = poseTable.getSelectionModel().getSelectedIndex();
        }
        pose.setFromPose(this.main.savedPoses.get(selectedIndex).getCopy());
        this.main.move(pose.getCopy());
        //select next or go to 0 back
        if(selectedIndex < poseTable.getItems().size()-1) {
            poseTable.getSelectionModel().select(selectedIndex+1);
        }else{
            poseTable.getSelectionModel().selectFirst();
        }
    }

    public void onPrevClick(){
        if(! checkConnection()) return;
        int selectedIndex = poseTable.getSelectionModel().getSelectedIndex();
        if(selectedIndex == -1){
            poseTable.getSelectionModel().selectFirst();
            selectedIndex = poseTable.getSelectionModel().getSelectedIndex();
        }
        pose.setFromPose(this.main.savedPoses.get(selectedIndex).getCopy());
        this.main.move(pose.getCopy());

        if(selectedIndex > 0) {
            poseTable.getSelectionModel().select(selectedIndex-1);
        }else{
            poseTable.getSelectionModel().selectLast();
        }
    }

    private boolean checkConnection(){
        if (connectionButton.getText().equals("connection")) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("connection Error");
            alert.setContentText("Not Connected yet");
            alert.show();
        } else {
            return true;
        }
        return false;
    }

    public void setObjRecognitionPan(BorderPane ObjRecognitionBorderPan){
        objRecognitionPan.getChildren().add(ObjRecognitionBorderPan);
        AnchorPane.setTopAnchor(ObjRecognitionBorderPan,0.0);
        AnchorPane.setBottomAnchor(ObjRecognitionBorderPan,0.0);
        AnchorPane.setRightAnchor(ObjRecognitionBorderPan,0.0);
        AnchorPane.setLeftAnchor(ObjRecognitionBorderPan,0.0);
    }

    private void changed(ObservableValue<? extends String> ob, String ol, String newValue) {
        String array[] = newValue.split("\n");
        textToSet = "";

        StringBuilder stringBuilder = new StringBuilder(textToSet);

        if (array.length > 20) {
            for (int i = array.length - 20; i < array.length; i++) {
                //textToSet = textToSet + (array[i] + "\n");
                textToSet = stringBuilder.append(array[i]).append("\n").toString();
            }
            Platform.runLater(() -> textArea.setText(textToSet));

        }
    }

    public void setXY(double x, double y) {
        XSlider.setValue(x);
        YSlider.setValue(y);
    }
}
