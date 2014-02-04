import java.io.*;
import java.util.*;
public class Maim {
	static final int port = 9001;
	static final String localhost = "localhost";
	public static void main(String[] args) throws IOException {
		Scanner nameReader = new Scanner(System.in);
		System.out.println("Welcome to Maim Chad (Corruption of AIM Chat): A Shitty Version of IRC");
		try {
		    Thread.sleep(500);
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}
		System.out.println("Do you want to start your own chat room for friends to connect to, or do you want to join another person's chat?");
		System.out.println("If the former, type 'CLIENT', no quotes. If the latter, type 'HOST', no quotes.");
		System.out.println("Mr. K, to check the functionality of this project on your own computer - open two CMD windows, run HOST in one, and run CLIENT in another. In the CLIENT one, connec to the IP 'localhost'.");
		System.out.println("Some features you might want to check out  :
			If you are the host, you can
			1. Shadowban - Shadowbanned users cannot send messages in the chat, but they can read them. Type \"\\ban[username]\" to  do this.
			2. Hardban - Hardbanned users cannot send or read messages in the chat. Type \"\\hardban[username]\" to do this.
			If you are either host or client you can : 
			1. Set your nickname - Type \"\\nick[newusername\" to do this. Try seeing what happens if you set your name to an existing name in the chat!
		String type = nameReader.nextLine();
			2. See who's online - Type \"\\listall\" to do this.)");
		while (!type.equals("HOST") && !type.equals("CLIENT")) {
			System.out.println("HOST or CLIENT, dude.");
			type = nameReader.nextLine();
			System.out.println(type);
		}
		if (type.equals("HOST")) { 
			Host.main(args);
		} 
		else if (type.equals("CLIENT")) {
			Client.main(args);
		}
	}	
}