package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private Map<String, ClientHandler> clients;

    public Server () {
        try {
            ServerSocket serverSocket = new ServerSocket(8750);
            clients = new ConcurrentHashMap<>();
            while (true) {
                System.out.println("Server was started! Await connection clients");
                Socket socket = serverSocket.accept();
                ExecutorService service = Executors.newCachedThreadPool();
                service.execute(()->new ClientHandler(this, socket));

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        }

    public void subscribe (ClientHandler client){
            clients.put(client.getNickname(), client);
            client.sendMsg("Wellcome to chart!!!");
            listChart();
    }

    public void unsubscribe (ClientHandler client) {
        if (client.getNickname() != null) {
            clients.remove(client.getNickname(), client);
            client.sendMsg(client.getNickname() + " left the chat");
            listChart();
        }
    }

    public void broadcastMsg(String msg) {
            for (ClientHandler c : clients.values()) {

            c.sendMsg(msg);
            if(!msg.startsWith("/listchart")){
            c.doDataOutputStream(c.getFile(), msg);}
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
    public void unicastMsg(String msg, String sender,String receiver) {

        ClientHandler sndr = clients.get(sender);
        sndr.sendMsg(msg);
        ClientHandler rcvr = clients.get(receiver);
        rcvr.sendMsg(msg);
    }

}
