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
		} catch (Exception e) {
            e.printStackTrace();
        }
            while (!socket.isClosed()) {
                String line;
                try {
					if ((line=in.readLine())!=null && line.length()>0) {
						System.out.println("Received message:" + line);
						if (line.charAt(0)==C_NAME) {
							this.name = line.substring(1);
							server.broadcastMsg(name + " connected");
							System.out.println(name + " connected");
						} else if (line.charAt(0)==C_END){
							System.out.println("User logged out");
							server.broadcastMsg(name + " left the room.");
							socket.close();
							break;
						}
						else {
							server.pushMsgs(Server.formatMsg(name,line));
						}
					}
                    Thread.sleep(10);
                } catch (SocketException e) {
                    e.printStackTrace();
					try {socket.close();}catch(Exception e1){};
					server.broadcastMsg(name + " disconnected");
					System.out.println("Ending this worker...");
					break;
                } catch (Exception e) {
					e.printStackTrace();
				}
				
            }
        
    
    }
    
    public boolean isAlive() {
        return !socket.isClosed();
    }
    
    public void sendMsg(String msg) {
        out.println(msg);
    }
	
	public String getName() {
		return this.name;
	}
}