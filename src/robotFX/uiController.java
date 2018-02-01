package robotFX;


import com.fazecast.jSerialComm.SerialPort;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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


    private Main main;

    @FXML
    private void initialize() {
        poseNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        poseBaseColumn.setCellValueFactory(cellData -> cellData.getValue().baseProperty().asObject() );
        poseShoulderColumn.setCellValueFactory(cellData -> cellData.getValue().shoulerProperty().asObject() );
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

        poseTable.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(final KeyEvent keyEvent) {

                if (keyEvent.getCode().equals(KeyCode.DELETE)) {
                    uiController.this.handleDeletePose();
                }else if(keyEvent.getCode().equals(KeyCode.ENTER)) {
                    uiController.this.onMoveButtonClic();
                }

                // ... other keyevents
            }

        });
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
        main.move(
                baseSlider.getValue(),
                shoulderSlider.getValue(),
                elbowSlider.getValue(),
                pinchSlider.getValue()
        );
    }

    public void appandText(String text) {
        textArea.appendText(text + "\n");
    }

    public void setMainApp(Main main) {
        this.main = main;
        poseTable.setItems(this.main.getSavedPoses());
    }

    public void onSaveClick(){
        this.main.savedPoses.add(new Pose(
                "P"+this.main.getSavedPoses().size(),
                (int) baseSlider.getValue(),
                (int) shoulderSlider.getValue(),
                (int) elbowSlider.getValue(),
                (int) pinchSlider.getValue())
        );
    }

    public void onSaveClickHere(){
        int selectedIndex = poseTable.getSelectionModel().getSelectedIndex();

        if (selectedIndex != -1) {
            this.main.savedPoses.get(selectedIndex).setBase((int) baseSlider.getValue());
            this.main.savedPoses.get(selectedIndex).setShouler((int) shoulderSlider.getValue());
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

    public void setPoseFromTable(Pose poseFromTable){

        if (poseTable.getSelectionModel().getSelectedIndex() > -1) {
            baseSlider.setValue(poseFromTable.getBase());
            shoulderSlider.setValue(poseFromTable.getShouler());
            elbowSlider.setValue(poseFromTable.getElbow());
            pinchSlider.setValue(poseFromTable.getPinch());
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
            if (result.get() == buttonTypeYes) {
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
        int selectedIndex = poseTable.getSelectionModel().getSelectedIndex();

        if (selectedIndex != -1) {
            this.main.move(this.main.savedPoses.get(selectedIndex));
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

        int selectedIndex = poseTable.getSelectionModel().getSelectedIndex();
        if(selectedIndex == -1){
            poseTable.getSelectionModel().selectFirst();
            selectedIndex = poseTable.getSelectionModel().getSelectedIndex();
        }
        this.main.move(this.main.savedPoses.get(selectedIndex));
        //select next or go to 0 back
        if(selectedIndex < poseTable.getItems().size()-1) {
            poseTable.getSelectionModel().select(selectedIndex+1);
        }else{
            poseTable.getSelectionModel().selectFirst();
        }
    }

    public void onPrevClick(){
        int selectedIndex = poseTable.getSelectionModel().getSelectedIndex();
        if(selectedIndex == -1){
            poseTable.getSelectionModel().selectFirst();
            selectedIndex = poseTable.getSelectionModel().getSelectedIndex();
        }
        this.main.move(this.main.savedPoses.get(selectedIndex));

        if(selectedIndex > 0) {
            poseTable.getSelectionModel().select(selectedIndex-1);
        }else{
            poseTable.getSelectionModel().selectLast();
        }
    }

}
