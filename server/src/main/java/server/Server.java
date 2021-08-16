package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.log4j.Logger;


public class Server {
    private static final Logger logger = Logger.getLogger(Server.class);
    private Map<String, ClientHandler> clients;

    public Server () {
        try {
            ConfigRead config = ConfigRead.readConfig(ConfigRead.DEFAULT_CONFIG);
            ServerSocket serverSocket = new ServerSocket(config.getPort(), config.getBackLog(), config.getHost());
            clients = new ConcurrentHashMap<>();
            while (true) {
                System.out.println("Server was started! Await connection clients");
                Socket socket = serverSocket.accept();
                logger.debug("Server was started");
                ExecutorService service = Executors.newCachedThreadPool();
                service.execute(()->new ClientHandler(this, socket));

            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Server are having trouble connecting");
        }
        }

    public void subscribe (ClientHandler client){
            clients.put(client.getNickname(), client);
            client.sendMsg("Wellcome to chart!!!");
            listChart();
            logger.debug("User " + client.getNickname() + " add to chart");
    }

    public void unsubscribe (ClientHandler client) {
        if (client.getNickname() != null) {
            clients.remove(client.getNickname(), client);
            client.sendMsg(client.getNickname() + " left the chat");
            logger.debug("user " + client.getNickname() + " left the chat");
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
        logger.debug("Personal message from " + sender + " to "+receiver);
    }

}
