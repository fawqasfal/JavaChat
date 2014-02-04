import java.net.*;
import java.io.*;
import java.util.*;
public class Client {
	String name;
	String address;
	Socket host;
	PrintWriter writeToHost;
	BufferedReader readFromHost;
	ArrayList chatNames;
	public static void main(String[] args) {
		Scanner nameReader = new Scanner(System.in);
		System.out.println("What will your name be?");
		String ourName = nameReader.nextLine();
		System.out.println("What IP will you connect to?");
		String ourAddress = nameReader.nextLine();
		new Client(ourName,ourAddress).connectToHost();
	}
	public Client(String name, String address) {
		this.name = name;
		this.address = address;
	}
	public void connectToHost() {
		try { 
			this.host = new Socket(this.address,Maim.port);
			this.writeToHost = new PrintWriter(host.getOutputStream());
			writeToHost.println(this.name + " connected.");
			writeToHost.flush();
			this.readFromHost = new BufferedReader(new InputStreamReader(host.getInputStream()));
			this.readFromHost.ready();
			System.out.println("Connected to host successfully.");
			this.go();
		} catch (IOException e) {
			System.out.println("Error connecting to host's server -- are you sure host exists at IP?");
			e.printStackTrace();
		}
	}
	public void go() {
		Thread innerReader = new Thread(new InnerReader());
		innerReader.start();
		Thread innerWriter = new Thread(new InnerWriter());
		innerWriter.start();
		Runtime.getRuntime().addShutdownHook(new Thread() {
    		public void run() {
    			writeToHost.println("Client " + name + " closed.");
    			writeToHost.flush();
    			System.out.println("Closed connection.");
     		}
 		});
	}
	public class InnerReader implements Runnable {
		@SuppressWarnings("unchecked")
		public void run() {
			try {
				String message = new String();
				while ((message = readFromHost.readLine()) != null) {
					if (message.length() >  10 && message.substring(0,10).equals("\\forcenick")) {
						name = message.substring(10,message.length());
					}
					else {
						System.out.println(message);
					}
				}
			} 
			catch(Exception e) {
				System.out.println("Host closed.");
				e.printStackTrace();
			}
		}
	}

	public class InnerWriter implements Runnable {
		public void run() {
			try {
				Scanner consoleReader = new Scanner(System.in);
				while (true) {
					String magic = consoleReader.nextLine();
					if (magic.length() > 5 && magic.substring(0,5).equals("\\nick")) {
						System.out.println("You have changed your name to " + magic.substring(5,magic.length()));
						writeToHost.println(name + " has changed his name to " + magic.substring(5,magic.length()));
						writeToHost.println("\\nick" + magic.substring(5,magic.length()));
						writeToHost.flush();
						name = magic.substring(5,magic.length()); 
					}
					else if (magic.equals("\\listall")) {
						System.out.println("Requesting list of members from host.");
						writeToHost.println("\\listall");
						writeToHost.flush();
					}
					else {
					writeToHost.println(name + ": " + magic);
					writeToHost.flush();
					}
				}
			} catch (Exception e) {
				System.exit(0);
			}
		}
	}
}