package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String nickname;

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
                        String[] strings = str.split(" ");
                        if (str.startsWith("/tryauth") && strings.length == 3) {
                            String nickDB = SQLHandler.getNickOnLoginPass(strings[1], strings[2]);
                            if (nickDB != null) {
                                if (server.isNickInChat(nickDB)) {
                                    out.writeUTF("A user with this nickname already exists in the chat");
                                } else {
                                    this.nickname = nickDB;
                                    str = "/authOk " + nickname;
                                    server.broadcastMsg(nickname + " joined the chat");
                                    out.writeUTF(str);
                                    server.subscribe(this);
                                    break;
                                }
                            } else {
                                out.writeUTF("Login or password is not correct");
                            }
                        }
                    }
                    while (true) {
                        String strChat = in.readUTF();
                    if (strChat.startsWith("/")) {
                        String[] strings = strChat.split(" ");
                        // смена nickname в чате
                        if (strings[0].equals("/changenick") && strings.length == 2) {
                            System.out.println(strings[0]);
                            if (!SQLHandler.isNickInDb(strings[1])) {
                                SQLHandler.tryToUpdateNickInDb(strings[1], this.nickname);
                                server.broadcastMsg("User was changed nickname from " + this.nickname + " to " + strings[1]);
                                server.unsubscribe(this);
                                System.out.println(this.nickname);
                                this.nickname = strings[1];
                                server.subscribe(this);
                            } else {
                                out.writeUTF("The user with this nickname already exists in the chat");
                            }
                        }

                        if (strChat.equals("/end")) {
                            break;
                        }
                    }
                        else {
                            server.broadcastMsg(this.nickname +" : " + strChat);}
                        System.out.println(strChat);
                    }

                } catch (IOException | SQLException e) {
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
                    server.unsubscribe(this);
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg (String msg) {
        try {

            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNickname () {
        return nickname;
    }
}
