import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.awt.event.*;
public class TerminalWrapper implements ActionListener {
	
    public static void main(String[] args)  throws IOException{
        new TerminalWrapper();
    }
	
	public TerminalWrapper() throws IOException {
		int port = 8080;
        InetAddress address;
        Scanner in = new Scanner(System.in);
        System.out.println("Enter the hostname of the server:");
        String ip = in.nextLine();
        if (ip.contains(":")) {
            String[] what = ip.split(":");
            address = InetAddress.getByName(what[0]);
            port = Integer.parseInt(what[1]);
        } else {
            address = InetAddress.getByName(ip);
        }
        System.out.println("Connecting to " + address);
        BaseClient bc = new BaseClient(address,port);
		bc.addActionListener(this);
        System.out.println("Connected!");
        System.out.println("Enter a name:");
		System.out.print(">");
        bc.setName(in.nextLine());
        while(true) {
			System.out.print(">");
            bc.sendMsg(in.nextLine());
        }
	}
	
	public void actionPerformed(ActionEvent e) {
		BaseClient bc = (BaseClient)e.getSource();
		System.out.print("\r");
		while(bc.hasMsg()) {
			System.out.println(bc.getMsg());
		}
		System.out.print('>');
	}
}