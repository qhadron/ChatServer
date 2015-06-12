import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class TerminalWrapper {
    public static void main(String[] args) throws IOException{
        int port = 31415;
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
        System.out.println("Connected!");
        System.out.println("Enter a name:");
        bc.setName(in.nextLine());
        while(true) {
            while (bc.hasMsg()) {
                System.out.println(bc.getMsg());
            }
            bc.sendMsg(in.nextLine());
        }
    }
    
    
}