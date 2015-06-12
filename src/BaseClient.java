import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class BaseClient {
    protected Socket socket;
    protected BufferedReader in;
    protected PrintWriter out;
    protected ConcurrentLinkedQueue<String> msgQueue;
    protected boolean running;
    protected Thread listenWorker;
    public BaseClient(InetAddress address, int port) throws IOException{
        socket = new Socket(address, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()),true);
        msgQueue = new ConcurrentLinkedQueue<>();
        running = true;
        listenWorker = new Thread(new ServerListener());
    }
    
    public boolean hasMsg() {
        return !msgQueue.isEmpty();    
    }
    
    public void sendMsg(String msg) {
        out.println(msg);
    }
    
    public String getMsg() {
        return msgQueue.poll();
    }
    
    public void stop() {
        running = false;
        out.println(ServerWorker.C_END);
        try {
            listenWorker.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void setName(String name) {
        out.println(ServerWorker.C_NAME + name);
    }
    
    class ServerListener implements Runnable {
        
        @Override
        public void run() {
            String line;
            while(running) {
                try {
                    if  ((line = in.readLine())!=null) {
                        msgQueue.add(line);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    
}