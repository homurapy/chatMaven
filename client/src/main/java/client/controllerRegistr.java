package client;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import javafx.scene.control.Label;

public class controllerRegistr{
    @FXML
    TextField loginFieldReg,passwordFieldReg, nicknameFieldReg;

    @FXML
    Label result;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public void registrationToChat (){

            try{
                if(socket == null || socket.isClosed()){
                socket = new Socket("localhost", 8750);
                in = new DataInputStream(socket.getInputStream());
                out = new DataOutputStream(socket.getOutputStream());
                String str ="/registration "+ loginFieldReg.getText()+" "+ passwordFieldReg.getText()+ " "+ nicknameFieldReg.getText();
                out.writeUTF(str);
                loginFieldReg.clear();
                passwordFieldReg.clear();
                nicknameFieldReg.clear();
                String answer = in.readUTF();
                result.setText(answer);
                if (answer.equals("Registration comlied")) {
                    out.writeUTF("/end");
                }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {

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
        }
   }



