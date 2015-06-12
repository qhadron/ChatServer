import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.awt.event.*;
public class BaseClient {
    protected Socket socket;
    protected BufferedReader in;
    protected PrintWriter out;
    protected ConcurrentLinkedQueue<String> msgQueue;
	protected LinkedList<ActionListener> listening;
    protected boolean running;
    protected Thread listenWorker;
    public BaseClient(InetAddress address, int port) throws IOException{
        socket = new Socket(address, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()),true);
        msgQueue = new ConcurrentLinkedQueue<>();
		listening = new LinkedList<>();
        running = true;
        listenWorker = new Thread(new ServerListener());
		listenWorker.start();
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
    
	public void addActionListener(ActionListener listener) {
		listening.add(listener);
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
                    if  ((line = in.readLine())!=null&&line.length()>0) {
						msgQueue.add(line);
						for (ActionListener cur : listening)
							cur.actionPerformed(new ActionEvent(BaseClient.this,ActionEvent.ACTION_PERFORMED,"MSG Received"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
					running = false;
                }
            }
        }
    }
    
    
}