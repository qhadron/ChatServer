import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Server {
    public static final int MAX_CONNECTIONS = 20;
    protected static final String SERVER_NAME = "SERVER";
    protected int port;
    protected BlockingQueue<Runnable> workers;
    protected ThreadPoolExecutor tpe;
    
    public static void main(String[] args) {
        new Server(8080).start();
    }
    
    public Server(int port) {
        this.port = port;
        workers = new LinkedBlockingQueue<>();
        tpe = new ThreadPoolExecutor(MAX_CONNECTIONS/2, MAX_CONNECTIONS, 5L, TimeUnit.SECONDS, workers);
    }
    public void start() {
        ServerSocket s;
        try {
            // create the main server socket
            s = new ServerSocket(port,MAX_CONNECTIONS,InetAddress.getByName("0.0.0.0"));
            System.out.println("Server starting up @"+s.getInetAddress()+":"+ port);

            while(true) {
                Socket client = s.accept();
                System.out.println("Accepted socket");
                tpe.execute(new ServerWorker(this, client));
            }
          
        } catch (Exception e) {
          e.printStackTrace();
          return;
        }
    }
    
    /**
     * Broadcasts a messages as the SERVER user
     * @param msg The message to send
     */
    public void broadcastMsg(String msg) {
        pushMsgs(formatMsg(SERVER_NAME,msg));
    }
    
    /**
     * Sends messages out as-is
     * @param msg The message to send
     */
    public synchronized void pushMsgs(String msg) {
        for (Iterator<Runnable> it = workers.iterator(); it.hasNext();) {
            ServerWorker worker = (ServerWorker)it.next();
            if (worker.isAlive()) {
                worker.sendMsg(msg);
            }
            else {
                it.remove();
            }
        }
        
    }
    
    public static String formatMsg(String name, String msg) {
        return "[" + name + "]@"+ (new Date()) + ":" + (msg.charAt(msg.length()-1)=='\n'? msg : (msg + '\n') );
    }
}