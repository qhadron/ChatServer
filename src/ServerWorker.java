import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class ServerWorker implements Runnable {
    public static final char C_NAME = '\u001B';
    public static final char C_END = '\u0017';
    
    protected Socket socket;
    protected Server server;
    protected String name;
    protected BufferedReader in;
    protected PrintWriter out;
    public ServerWorker(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;
        name = "Anon";
    }
    
    @Override
    public void run() {
        System.out.println("Worker spawned to handle request from " + socket.getInetAddress());
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            while (!socket.isClosed()) {
                String line;
                if ((line=in.readLine())!=null && line.length()>0) {
                    if (line.charAt(0)==C_NAME) {
                        this.name = line.substring(1);
                        server.broadcastMsg(name + " connected");
                    } else if (line.charAt(0)==C_END){
                        socket.close();
                        break;
                    }
                    else {
                        server.pushMsgs(Server.formatMsg(name,line));
                    }
                }
                try {
                    Thread.sleep(10);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    
    }
    
    public boolean isAlive() {
        return !socket.isClosed();
    }
    
    public void sendMsg(String msg) {
        out.println(msg);
    }
    
}