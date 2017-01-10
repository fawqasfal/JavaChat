import java.net.*;
import java.io.*;
import java.util.*;
public class Host {
	Socket clientReader;
	private String myName;
	ArrayList<ClientReader> clients;
	public Socket getSocket() {
		return clientReader;
	}
	public Host(String myName) {
		this.myName = myName;
	}
	public static void main(String[] args) {
		Scanner nameReader = new Scanner(System.in);
		System.out.println("What will your name be?");
		new Host(nameReader.nextLine()).setUpServer();
	} 
	public void setUpServer(){
		clients = new ArrayList<ClientReader>();
		try {
			ServerSocket server =  new ServerSocket(Maim.port);
			System.out.println("Server set up without fail. Now awaitting clients.");
			Thread newHostThread = new Thread(new ClientWriter());
			newHostThread.start();
			this.clientReader(server);
		} catch (IOException e) {
			System.out.println("Failure setting up server on host's network. Are you sure no one is using this port on this computer already?");
			System.exit(0);
		}
	}
	public void clientReader(ServerSocket server) {
		try {
			while (true) {
				clientReader = server.accept();
				ClientReader newReader = new ClientReader(clientReader);
				Thread newClientThread = new Thread(newReader);
				newClientThread.start();
				clients.add(newReader);
			}
		} catch (IOException e) {
			System.out.println("Failure to connect to client.");
			e.printStackTrace();
		}
	}
	public void sendAll(String message) {
		try {
			for (ClientReader client: clients) {
				if (!client.veryBadApple) {
				PrintWriter writerToClient = new PrintWriter(client.getSocket().getOutputStream());
				writerToClient.println(message);
				writerToClient.flush();
				}	
			}
		} catch (IOException e) {
			System.out.println("Error sending message " + message + " .");
			e.printStackTrace();
		}
	}

	public class ClientReader implements Runnable {
		int amtOfMessRec = 0;
		boolean badApple = false;
		boolean veryBadApple = false;
		String name = null;
		private BufferedReader consoleReader;
		private Socket clientConnection;
		public ClientReader(Socket clientSocket) {
			try {
				clientConnection = clientSocket;
				consoleReader = new BufferedReader(new InputStreamReader(this.clientConnection.getInputStream()));
				consoleReader.ready();
			} catch(IOException e) {
				System.out.println("Error getting input stream from client.");
				e.printStackTrace();
			}
		}
		public Socket getSocket() {
			return this.clientConnection;
		}
		public void badApple() {
			this.badApple = true;
		}
		public void veryBadApple() {
			this.veryBadApple = true;
		}
		public void nameChange() {
			try {
				PrintWriter listAllResponse = new PrintWriter(this.getSocket().getOutputStream());
				for (ClientReader client : clients) {
					while ((this.name.equals(client.name) && !this.equals(client)) || (this.name.equals(myName))) {
						sendAll("Sorry, " + client.name + " your nickname conflicts with another person's nickname in this chat.");
						System.out.println("Sorry, " + client.name + " your nickname conflicts with another person's nickname in this chat.");
						int guestNum = (int) Math.round(Math.random() * 1000);
						sendAll("Until you choose a different name, we're making you : Guest" + String.valueOf(guestNum));
						System.out.println("Until you choose a different name, we're making you : Guest" + String.valueOf(guestNum));
						this.name = "Guest" + String.valueOf(guestNum);
						listAllResponse.println("\\forcenick" + "Guest" + String.valueOf(guestNum));
						listAllResponse.flush();
					}	
				}
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		public void run() {
			String newMessage = new String();
			try {
				PrintWriter listAllResponse = new PrintWriter(this.getSocket().getOutputStream());
				while ((newMessage = this.consoleReader.readLine()) != null && !this.badApple) {
					if (newMessage.equals("\\listall")) {
						listAllResponse.println("ALL USERS :\n");
						for (ClientReader client : clients) {
							listAllResponse.println(client.name);
						}
						listAllResponse.println(myName + " " + clientReader.toString());
						listAllResponse.flush();
					}
					else if (newMessage.length() > 5 && newMessage.substring(0,5).equals("\\nick")) {
						this.name = newMessage.substring(5,newMessage.length());
						this.nameChange();
					}
					else {
						System.out.println(newMessage);	
						sendAll(newMessage);
						if (this.amtOfMessRec == 0 && this.name == null) {
						this.name = newMessage.substring(0,(newMessage.length() - " connected.".length()));
						}
						else {
						this.amtOfMessRec++;
						}
						this.nameChange();
					} 
				}
			} catch(IOException e) {
			}
		}

	}
	public class ClientWriter implements Runnable {
		public void run() {
			try {	
				Scanner consoleReader = new Scanner(System.in);
				while (true) {
					String magic = consoleReader.nextLine();
					if (magic.equals("\\listall")) {
						System.out.println("ALL USERS: \n");
						for (ClientReader client : clients)
							System.out.println(client.name + " "  + client.getSocket().toString());
					}
					else if (magic.length() > 8 && magic.substring(0,8).equals("\\hardban")) {
						boolean exist = false; 
						for (ClientReader client : clients) { 
							if (client.name.equals(magic.substring(8,magic.length()))) {
								client.veryBadApple();
								client.badApple();
								sendAll(client.name + "has been hardbanned. He cannot read or send messages to the chat.");
								System.out.println(client.name + " has been hardbanned. He cannot read or send messages to the chat.");
								exist = true;
							}
							if (exist == false) {
								System.out.println("This user does not exist.");
							}
						}
					}
					else if (magic.length() > 4 && magic.substring(0,4).equals("\\ban")) {
						boolean exist = false;
						for (ClientReader client : clients) {
							if (client.name.equals(magic.substring(4,magic.length()))) {
								client.badApple();
								sendAll(client.name + " has been shadowbanned. He can no longer send messages to the chat but he can read messages.");
								System.out.println(client.name + " has been shadowbanned. He can no longer send messages to the chat but he can read messages.");
								exist = true;
							}
						}
						if (exist == false) {
							System.out.println("This user doesn't exist.");
						}
					}
					else if (magic.length() > 5 && magic.substring(0,5).equals("\\nick")) {
						System.out.println("You have changed your name to " + magic.substring(5,magic.length()));
						sendAll(myName + " has changed his name to " + magic.substring(5,magic.length()));
						myName = magic.substring(5,magic.length()); 
					}

					else {
						sendAll(myName + ": " + magic);
						System.out.println(myName + ": " + magic);
					}
				}
			} catch(Exception e) {
				System.out.println("Closed connection.");
				e.printStackTrace();
			}
		}
	}
}