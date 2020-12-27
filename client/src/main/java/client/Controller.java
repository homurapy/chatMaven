package client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    TextArea mainTextArea, clientsList;

    @FXML
    TextField messageUser, loginFieldForReg,passwordFieldForReg,loginField,passwordField;

    @FXML
    HBox autorizationField;

    @FXML
    Button ConnectBtn, sendMsgBtn;


    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private boolean authStatus;
    private String nickname;

    @Override
    public void initialize (URL url, ResourceBundle resourceBundle) {
        setAuthStatus(false);


    }
public void connect(){
        try {
            socket = new Socket("localhost", 8189);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            new Thread(() -> {
                  while (true){
                    try {
                        String inputStr = in.readUTF();
                        String[] authWord =inputStr.split(" ");
                    if(authWord[0].equals("/authOk") && authWord.length == 2){
                        setAuthStatus(true);
                        nickname = authWord[1];
                        break;
                        }
                  } catch (IOException e) {
                    e.printStackTrace();
                    }
                }
                while (true) {
                    try {
                        String str = in.readUTF();
                        String[] strings = str.split(" ");
                        if(strings[0].equals("/changenick") && strings.length == 2){
                            nickname = strings[1];
                            Platform.runLater(() -> {
                            ((Stage) mainTextArea.getScene().getWindow()).setTitle("Java Chat Client: " + nickname);
                            });
                        }
                        else {
                            mainTextArea.appendText(str + System.lineSeparator());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
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
            String str = messageUser.getText();
            out.writeUTF(str);
            messageUser.clear();
            messageUser.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setAuthStatus (boolean authStatus) {
        this.authStatus = authStatus;
        if(authStatus == true) {
            autorizationField.setVisible(false);
            mainTextArea.setVisible(true);
            messageUser.setVisible(true);
            clientsList.setVisible(true);
        }else {
            autorizationField.setVisible(true);
            mainTextArea.setVisible(false);
            messageUser.setVisible(false);
            clientsList.setVisible(false);
            nickname = "";

        }
        Platform.runLater(() -> {
            if (nickname.isEmpty()) {
                ((Stage) mainTextArea.getScene().getWindow()).setTitle("Java Chat Client");
            }
            else {
                ((Stage) mainTextArea.getScene().getWindow()).setTitle("Java Chat Client: " + nickname);
            }
        });
    }

    public void sendReg (ActionEvent actionEvent) {
        connect();
        if (loginField.getText() != null && passwordField != null) {
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
}
