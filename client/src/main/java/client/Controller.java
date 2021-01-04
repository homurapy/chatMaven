package client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    TextArea mainTextArea, clientsList;

    @FXML
    TextField messageUser,loginField, passwordField;

    @FXML
    HBox autorizationField;

    @FXML
    Button ConnectBtn, sendMsgBtn;

    @FXML
    GridPane Main;

    @FXML
    Label serverResponse;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private boolean authStatus;
    private String nickname;
    private File file;
    private String login;


    @Override
    public void initialize (URL url, ResourceBundle resourceBundle) {
        authStatus = false;
        Platform.runLater(() -> ((Stage) Main.getScene().getWindow()).setOnCloseRequest(t -> {
            sendMsg("/end");
            Platform.exit();
        }));

    }

    public void connect () {
        try {
if (socket ==null || socket.isClosed() ) {
    socket = new Socket("localhost", 8420);
    in = new DataInputStream(socket.getInputStream());
    out = new DataOutputStream(socket.getOutputStream());
    new Thread(() -> {
        try {
            while (true) {

                String str = in.readUTF();
                String[] authWord = str.split(" ");
                if (authWord[0].equals("/authOk") && authWord.length == 2) {
                    setAuthStatus(true);
                    nickname = authWord[1];
                        String source = ".\\client\\src\\main\\resources\\history_"+login+".txt";

                        file = new File(source);
                        logChart(file, source, nickname);
                    break;
                }
                else {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            serverResponse.setText(str);
                        }
                    });
                }
            }
            while (true) {
                String str = in.readUTF();
                String[] strings = str.split(" ");

                if (strings[0].equals("/changenick") && strings.length == 2) {
                    nickname = strings[1];
                    Platform.runLater(() -> {
                        ((Stage) mainTextArea.getScene().getWindow()).setTitle("Java Chat Client: " + nickname);
                    });
                } else if (strings[0].equals("/listchart") && strings.length >= 2) {
                    clientsList.clear();
                    for (int i = 1; i < strings.length; i++) {
                        clientsList.appendText(strings[i] + System.lineSeparator());
                    }
                } else {
                    mainTextArea.appendText(str + System.lineSeparator());
                    doDataOutputStream(file,str);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }).start();
}
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg () {
        try {

            String str = messageUser.getText();
            out.writeUTF(str);
            messageUser.clear();
            messageUser.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg (String s) {
        try {
            if (socket != null && !socket.isClosed()) {
                String str = messageUser.getText();
                out.writeUTF(str);
                messageUser.clear();
                messageUser.requestFocus();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setAuthStatus (boolean authStatus) {
        this.authStatus = authStatus;
        if (authStatus) {
            autorizationField.setVisible(false);
            autorizationField.setManaged(false);
            mainTextArea.setVisible(true);
            mainTextArea.setManaged(true);
            clientsList.setVisible(true);
            clientsList.setManaged(true);
            messageUser.setVisible(true);
            messageUser.setManaged(true);
            sendMsgBtn.setVisible(true);
            sendMsgBtn.setManaged(true);

        } else {
            autorizationField.setVisible(true);
            autorizationField.setManaged(true);
            mainTextArea.setVisible(false);
            mainTextArea.setManaged(false);
            clientsList.setVisible(false);
            clientsList.setManaged(false);
            messageUser.setVisible(false);
            messageUser.setManaged(false);
            sendMsgBtn.setVisible(false);
            sendMsgBtn.setManaged(false);
            serverResponse.setVisible(false);
            nickname = "";

        }
        Platform.runLater(() -> {
            if (nickname.isEmpty()) {
                ((Stage) Main.getScene().getWindow()).setTitle("Java Chat Client");
            } else {
                ((Stage) Main.getScene().getWindow()).setTitle("Java Chat Client: " + nickname);
            }
        });
    }

    public void sendReg (ActionEvent actionEvent) {
        connect();
        if (loginField.getText() != null && passwordField != null) {
            login = loginField.getText();
            String str = "/tryauth " + loginField.getText() + " " + passwordField.getText();
            loginField.clear();
            passwordField.clear();
            try {
                out.writeUTF(str);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void callRegistration (ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/registration.fxml"));
            Parent root = fxmlLoader.load();
            stage.setTitle("Registration");
            stage.setScene(new Scene(root, 200, 200));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void  logChart(File file, String source, String nickname){
        //при отсутствии файла - создание файла истории
        if (!Files.isRegularFile(Path.of(source))){
            try {
                Files.createFile(Path.of(source));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            doDataInputStream(mainTextArea,file);
        }

    }
    static void doDataOutputStream(File file, String value) {
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(file, true))) {
            dos.writeUTF(value);
        } catch (Exception e) {
            throw new RuntimeException("SWW", e);
        }
    }
    static void doDataInputStream(TextArea textArea,File file) {
        Platform.runLater(() -> {
            try (DataInputStream dis = new DataInputStream(new FileInputStream(file))) {

                while (dis.readByte() != -1) {

                    String st = dis.readUTF();
                    textArea.appendText(st+System.lineSeparator());
                }
            } catch (Exception e) {
                throw new RuntimeException("SWW", e);
            }
        });
    }


}
