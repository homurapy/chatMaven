package server;

import server.sql.QuerySQL;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String nickname;
    private File file;

    public ClientHandler (Server server, Socket socket) {

        try {
            this.server = server;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            boolean reg = false;
            new Thread(() -> {
                try {
                    while (true) {
                        String str = in.readUTF();
                        System.out.println(str);
                        String[] strings = str.split(" ");
                        if (str.startsWith("/tryauth") && strings.length == 3) {
                            QuerySQL querySQL = new QuerySQL();
                            String nickDB = querySQL.tryNickFromLoginPass(strings[1], strings[2]);
                            if (nickDB != null) {
                                if (server.isNickInChat(nickDB)) {
                                    out.writeUTF("A user with this nickname already exists in the chat");
                                } else {
                                    this.nickname = nickDB;
                                    str = "/authOk " + nickname;
                                    //создание файла истории
                                    String source = ".\\server\\src\\main\\resources\\history_"+strings[1]+".txt";
                                    file = new File(source);
                                    logChart(source, nickname, file);

                                    server.broadcastMsg(nickname + " joined the chat");
                                    out.writeUTF(str);
                                    server.subscribe(this);
                                    break;
                                }
                            } else {
                                sendMsg("Login or password is not correct");
                            }
                        }
                        if (str.startsWith("/registration")){
                            QuerySQL querySQL = new QuerySQL();
                               if(strings.length ==4 && !querySQL.isLoginInDb(strings[1]) && !querySQL.isNickInDb(strings[3])){
                                querySQL.tryToRegistNewUser(strings[1],strings[2], strings[3]);
                                sendMsg("Registration comlied");
                                break;
                            }
                            else {
                                sendMsg("Incorrect login/nickname");
                            }
                       }

                    }
                    while (true) {
                        String strChat = in.readUTF();
                        System.out.println(strChat);
                    if (strChat.startsWith("/")) {
                        String[] strings = strChat.split(" ");
                        // смена nickname в чате
                        if (strings[0].equals("/changenick") && strings.length == 2) {
                            QuerySQL querySQL = new QuerySQL();
                            if (!querySQL.isNickInDb(strings[1])) {
                                querySQL.tryToUpdateNickInDb(strings[1], this.nickname);
                                server.broadcastMsg("User was changed nickname from " + this.nickname + " to " + strings[1]);
                                server.unsubscribe(this);
                                this.nickname = strings[1];
                                server.subscribe(this);
                            } else {
                                out.writeUTF("The user with this nickname already exists in the chat");
                            }
                        }
                        else if (strings[0].equals("/w") && strings.length > 2){
                            if (server.isNickInChat(strings[1])){
                                server.unicastMsg(this.nickname + " : " + strChat.substring(strChat.indexOf(strings[2])), this.nickname, strings[1]);
                            }
                            else {
                                sendMsg("User with nickname " + strings[1] + " missing in the chart");
                            }
                        }

                        if (strChat.equals("/end")) {
                            break;
                        }
                    }
                        else {
                            server.broadcastMsg(this.nickname +" : " + strChat);}

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    closeDataStream();
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
    public void closeDataStream(){
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
    public void  logChart(String source, String nickname, File file){
        //при отсутствии файла - создание файла истории
        if (!Files.isRegularFile(Path.of(source))){
            try {
                Files.createFile(Path.of(source));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            ArrayList <String> list  = doDataInputStream(file);
            int begin = 10;
            if(list.size()>begin){ begin = list.size()-begin ;}
            else { begin = 0;}

            for (int i = begin; i <list.size() ; i++) {
                sendMsg(list.get(i));
            }

        }

    }
    static void doDataOutputStream(File file, String value) {
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(file, true))) {
            dos.writeUTF(value);
        } catch (Exception e) {
            throw new RuntimeException("SWW", e);
        }
    }
    static ArrayList <String> doDataInputStream(File file) {

        try (DataInputStream dis = new DataInputStream(new FileInputStream(file))) {
            String s = dis.readUTF();
            ArrayList<String> list = new ArrayList<>();
            while ((s = dis.readUTF()) != null) {
                list.add(s);
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException("SWW", e);
        }
    }

    public File getFile () {
        return file;
    }
}
