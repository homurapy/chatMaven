package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Server {
    private Map<String, ClientHandler> clients;

    public Server () {
        try {
            SQLHandler.connect();
            ServerSocket serverSocket = new ServerSocket(8188);
            clients = new ConcurrentHashMap<>();
            while (true) {
                System.out.println("Server was started! Await connection clients");
                Socket socket = serverSocket.accept();
                ClientHandler c = new ClientHandler(this, socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            SQLHandler.disconnect();
        }
        }

    public void subscribe (ClientHandler client){
            clients.put(client.getNickname(), client);
            client.sendMsg("Wellcome to chart!!!");
            listChart();
    }

    public void unsubscribe (ClientHandler client) {
        clients.remove(client.getNickname(), client);
        client.sendMsg(client.getNickname() + " left the chat");
        listChart();
    }

    public void broadcastMsg(String msg) {
        for (ClientHandler c : clients.values()) {
            c.sendMsg(msg);
        }
    }
    public boolean isNickInChat(String nick){
        if (clients.keySet().contains(nick)){
            return true;
        }
        else {
            return false;
        }
    }
    public void listChart(){
    StringBuilder bn = new StringBuilder();
        bn.append("/listchart");
        for (String nick: clients.keySet()) {
        bn.append(" " + nick);
        }
        broadcastMsg(bn.toString());
        }
}
