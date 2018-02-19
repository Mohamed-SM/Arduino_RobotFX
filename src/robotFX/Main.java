package robotFX;

import com.fazecast.jSerialComm.SerialPort;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.opencv.core.Core;
import robotFX.model.Pose;
import robotFX.view.ObjRecognitionController;
import robotFX.view.uiController;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Scanner;


public class Main extends Application {

    public SerialPort serPort[] = SerialPort.getCommPorts();
    private SerialPort port;
    private static boolean connected;
    private uiController controller;
    private Stage primaryStage;

    public ObservableList<Pose> savedPoses = FXCollections.observableArrayList();

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("view/ui.fxml"));
            FXMLLoader objRecognitionloader = new FXMLLoader(getClass().getResource("view/ObjRecognition.fxml"));

            BorderPane root = loader.load();

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();

            BorderPane objRecognitionBorderPan = objRecognitionloader.load();
            ObjRecognitionController objRecognitionController = objRecognitionloader.getController();

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

    public void move(double base, double shoulder, double elbow, double pinch) {

        /*
        try {
            Thread.sleep(100);
        } catch (InterruptedException ignored) { }
        */

        PrintWriter printWriter;
        printWriter = new PrintWriter(this.port.getOutputStream());
        String msg = new DecimalFormat("000").format(base) + " "
                + new DecimalFormat("000").format(shoulder) + " "
                + new DecimalFormat("000").format(elbow) + " "
                + new DecimalFormat("000").format(pinch);
        printWriter.print(msg);
        System.out.println( "sendeing msg : " + msg);
        printWriter.flush();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) { }

        printWriter.close();
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

    public void start() {


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
}
