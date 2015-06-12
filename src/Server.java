import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Server {
    public static final int MAX_CONNECTIONS = 20;
    protected static final String SERVER_NAME = "SERVER";
    protected int port;
    protected LinkedList<Runnable> workers;
    protected ThreadPoolExecutor tpe;
    
    public static void main(String[] args) {
        new Server(8080).start();
    }
    
    public Server(int port) {
        this.port = port;
        workers = new LinkedList<>();
        tpe = new ThreadPoolExecutor(MAX_CONNECTIONS/2, MAX_CONNECTIONS, 5L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
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
				ServerWorker cur = new ServerWorker(this, client);
				workers.add(cur);
                tpe.execute(cur);
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
		System.out.println("Sent messages to " + workers.size() + " clients");
        for (Iterator<Runnable> it = workers.iterator(); it.hasNext();) {
            ServerWorker worker = (ServerWorker)it.next();
            if (worker.isAlive()) {
				System.out.println("Sent message to " + worker.getName());
                worker.sendMsg(msg);
            }
            else {
				System.out.println(worker.getName()+" died.");
                it.remove();
            }
        }
        
    }
    
    public static String formatMsg(String name, String msg) {
        return "[" + name + "]@"+ (new Date()) + ":" + (msg.charAt(msg.length()-1)=='\n'? msg : (msg + '\n') );
    }
}