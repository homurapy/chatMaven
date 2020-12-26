package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public ClientHandler (Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            new Thread(() -> {
                try {
                    while (true) {
                        String str = in.readUTF();
                        System.out.println("Message from client: " + str);
                        if (str.startsWith("/")) {
                            String[] strings = str.split(" ");
                            if(strings[0].equals("/tryauth") && strings.length == 3){

//                                String nick = SQLHandler.getNickOnLoginPass(strings[1], strings[2]);
//                                if(nick != null ){
                                    str = "/authOk ";
                                    out.writeUTF(str);
//                                }
                            }
                        }
                        if (str.equals("/end")) {
                            break;
                        }
                        System.out.println(str);
                        server.broadcastMsg(str);
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
                    server.unsubscribe(this);
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg (String msg) {
        try {
            out.writeUTF("ECHO: " + msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
