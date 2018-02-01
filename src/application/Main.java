package application;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Scanner;

import com.fazecast.jSerialComm.SerialPort;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;


public class Main extends Application {

    private static int pin;
    public SerialPort serPort[] = SerialPort.getCommPorts();
    private SerialPort port;
    private static boolean connected;
    private uiController controller;
    //private Thread outputthread;
    private Thread inputthread;


    @Override
    public void start(Stage primaryStage) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ui.fxml"));
            BorderPane root = loader.load();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.show();

            primaryStage.setOnCloseRequest(event -> {
                Desconnect();
            });

            controller = loader.getController();
            controller.setMainApp(this);
            controller.fillComboBox();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        launch(args);
    }

    public void connect(int selectedIndex) {
        port = serPort[selectedIndex];

        try {
            if (port.openPort()) {
                //System.out.println("Connection is opend");
                controller.appandText("Connection is opend");
                port.setBaudRate(9600);
                port.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
                connected = true;
                creatAndStartThread();
            } else {
                System.err.println("Chack connection ther is a problem : " + selectedIndex);
                controller.appandText("Chack connection ther is a problem : \n" + selectedIndex);
            }
        } catch (Exception ex) {
            if (port.isOpen()) {
                //System.out.println("port is alredy open");
                controller.appandText("port is alredy open");
                port.setBaudRate(9600);
                port.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
                connected = true;
                creatAndStartThread();
            } else {
                System.err.println("Chack connection ther is a problem : " + selectedIndex);
                controller.appandText("Chack connection ther is a problem : \n" + selectedIndex);
            }

        }
    }

    public void Desconnect() {
        if (connected) {
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

    public void setPin(int pin, int blinks, int delay) {

        Main.pin = pin;

        if (pin == 14) {
            controller.appandText("sanding power off signal");
        }

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
        }

        PrintWriter printWriter = new PrintWriter(port.getOutputStream());
        String msg = new DecimalFormat("00").format(pin) + " "
                + new DecimalFormat("00").format(blinks) + " "
                + new DecimalFormat("0000").format(delay);
        printWriter.print(msg);

        printWriter.flush();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
        printWriter.close();
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


        inputthread = new Thread() {
            @Override
            public void run() {
                Scanner scanner = new Scanner(port.getInputStream());

                while (scanner.hasNextLine() && connected) {
                    try {
                        String line = scanner.nextLine();
                        System.out.println(line);
                        controller.appandText(line);
                    } catch (Exception ex) {
                        System.out.println("Error getting data	....");
                    }
                }
                scanner.close();
            }
        };
        inputthread.start();

    }

}
