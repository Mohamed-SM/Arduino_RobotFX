package robotFX;

import com.fazecast.jSerialComm.SerialPort;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.opencv.core.Core;
import org.opencv.core.Point;
import robotFX.model.Pose;
import robotFX.utiles.ik;
import robotFX.view.ObjRecognitionController;
import robotFX.view.uiController;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Scanner;


public class Main extends Application {

    private Scene primaryScene;
    public SerialPort serPort[] = SerialPort.getCommPorts();
    private SerialPort port;
    private static boolean connected;
    private uiController controller;
    private Stage primaryStage;
    private ObjRecognitionController objRecognitionController;
    public ObservableList<Point> points  = FXCollections.observableArrayList();

    public ObservableList<Pose> savedPoses = FXCollections.observableArrayList();

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("view/ui.fxml"));
            FXMLLoader objRecognitionloader = new FXMLLoader(getClass().getResource("view/ObjRecognition.fxml"));

            BorderPane root = loader.load();

            primaryScene = new Scene(root);
            primaryStage.setScene(primaryScene);
            primaryStage.setMaximized(true);
            primaryStage.show();

            BorderPane objRecognitionBorderPan = objRecognitionloader.load();
            objRecognitionController = objRecognitionloader.getController();
            objRecognitionController.setMainApp(this,points);

            primaryStage.setOnCloseRequest(event -> {
                objRecognitionController.setClosed();
                desconnect();
                System.exit(0);
            });

            controller = loader.getController();
            controller.setMainApp(this);
            controller.fillComboBox();
            controller.setObjRecognitionPan(objRecognitionBorderPan);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void fullScreenCam(ImageView fulloriginalFrame) {
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.getChildren().add(fulloriginalFrame);
        fulloriginalFrame.setFitWidth(Screen.getPrimary().getBounds().getWidth());
        fulloriginalFrame.setFitHeight(Screen.getPrimary().getBounds().getHeight());
        Scene scene = new Scene(anchorPane);
        this.primaryStage.setScene(scene);
        this.primaryStage.setFullScreen(true);

        scene.setOnKeyPressed(keyEvent -> {

            if (keyEvent.getCode().equals(KeyCode.ESCAPE)) {
                primaryStage.setScene(primaryScene);
                this.primaryStage.setFullScreen(false);
                this.primaryStage.setMaximized(false);
                this.primaryStage.setMaximized(true);
            }

            // ... other keyevents
        });
    }

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        launch(args);
    }

    public void connect(int selectedIndex) {
        port = serPort[selectedIndex];

        try {
            if (port.openPort()) {
                //System.out.println("Connection is opend");
                controller.appandText("Connection is opend");
                port.setBaudRate(115200);
                port.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
                connected = true;
                creatAndStartThread();
                start();
            } else {
                System.err.println("Chack connection ther is a problem : " + selectedIndex);
                controller.appandText("Chack connection ther is a problem : \n" + selectedIndex);
            }
        } catch (Exception ex) {
            if (port.isOpen()) {
                //System.out.println("port is alredy open");
                controller.appandText("port is alredy open");
                port.setBaudRate(115200);
                port.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
                connected = true;
                creatAndStartThread();
                start();
            } else {
                System.err.println("Chack connection ther is a problem : " + selectedIndex);
                controller.appandText("Chack connection ther is a problem : \n" + selectedIndex);
            }

        }
    }

    public void desconnect() {

        if (connected) {

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) { }

            this.stop();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) { }

            serPort = SerialPort.getCommPorts();
            controller.fillComboBox();
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            connected = false;

            if (port.closePort()) {
                System.out.println("Connection is Closed");
                controller.appandText("Connection is Closed\n");
            } else {
                System.err.println("Connection is not Closed");
                controller.appandText("Connection is not Closed\n");
            }
        }

    }

    public void move(Pose pose) {

        /*
        try {
            Thread.sleep(100);
        } catch (InterruptedException ignored) { }
        */

        PrintWriter printWriter;
        printWriter = new PrintWriter(this.port.getOutputStream());
        String msg = new DecimalFormat("000").format(pose.getBase()) + " "
                + new DecimalFormat("000").format(pose.getShoulder()) + " "
                + new DecimalFormat("000").format(pose.getElbow()) + " "
                + new DecimalFormat("000").format(pose.getPinch());
        printWriter.print(msg);
        System.out.println( "sendeing msg : " + msg);
        printWriter.flush();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) { }

        printWriter.close();
    }

    private void start() {


        try {
            Thread.sleep(100);
        } catch (InterruptedException ignored) { }


        PrintWriter printWriter;
        printWriter = new PrintWriter(this.port.getOutputStream());
        String msg = "start";
        printWriter.print(msg);
        System.out.println( "sendeing msg : " + msg);
        printWriter.flush();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) { }

        printWriter.close();
    }

    public void stop() {


        if (connected) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) { }


            PrintWriter printWriter;
            printWriter = new PrintWriter(this.port.getOutputStream());
            printWriter.print("stop");
            System.out.println( "sendeing stop");
            printWriter.flush();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) { }

            printWriter.close();
        }
    }

    private void creatAndStartThread() {
        /*
         *  outputthread = new Thread() {
         *
         * @Override public void run() { try {Thread.sleep(100);} catch
         * (InterruptedException e) {} PrintWriter printWriter = new
         * PrintWriter(port.getOutputStream()); while (connexted) {
         * printWriter.println(Main.pin); System.out.println("printing from thread " +
         * Main.pin); printWriter.flush(); try {Thread.sleep(1000);} catch
         * (InterruptedException e) {} } printWriter.close(); } }; outputthread.start();
         */


        Thread inputThread = new Thread(() -> {
            Scanner scanner = new Scanner(port.getInputStream());

            while (scanner.hasNextLine()) {
                try {
                    String line = scanner.nextLine();
                    System.out.println(line);
                    controller.appandText(line);
                    if (line.equals("stoping")) {
                        break;
                    }
                } catch (Exception ex) {
                    System.out.println("Error getting data	....");
                }
            }

            System.out.println("end of connection");
            scanner.close();
        });
        inputThread.start();

    }

    public ObservableList<Pose> getSavedPoses() {
        return savedPoses;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void grap(Point point) {

        Thread grapThread = new Thread(() -> {
            System.out.println("Starting grap");
            Pose pose = ik.solve(point.x,point.y,10,120);
            controller.setXY(point.x,point.y);
            this.move(pose);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException ignored) { }

            pose = ik.solve(point.x,point.y,0,120);
            controller.setXY(point.x,point.y);
            this.move(pose);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException ignored) { }

            pose = ik.solve(point.x,point.y,-13,120);
            this.move(pose);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException ignored) { }

            pose = ik.solve(point.x,point.y,-13,90);
            this.move(pose);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException ignored) { }

            pose = ik.solve(point.x,point.y,10,90);
            this.move(pose);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException ignored) { }

            pose = ik.solve(0,30,10,90);
            this.move(pose);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException ignored) { }

            pose = ik.solve(0,30,10,120);
            this.move(pose);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException ignored) { }

            pose = ik.solve(10,0,10,120);
            controller.setXY(point.x,point.y);
            this.move(pose);

        });
        grapThread.start();
    }

    public void grapAll(ObservableList<Point> courentPointList) {
        Thread grapAllThread = new Thread(() -> courentPointList.forEach(point -> {
            Main.this.grap(point);

            try {
                Thread.sleep(20000);
            } catch (InterruptedException ignored) { }

        }));
        grapAllThread.start();
    }
}
