/**
 * This class represent Server
 * @author Tzvi Mints And Or Abuhazira
 */
package Client;
import java.net.Socket;
import java.util.Scanner;

public class Client {
	private static final String host = "localhost";
	private static final int PORT = 9999;
	private String username;
	private String serverhost;
	private int serverport;
	private Scanner userScanner;

	/* ************************** Setters and Getters ************************** */

	public Client(String clientName, String host, int port) {
		this.username = clientName;
		this.serverhost = host;
		this.serverport = port;
	}

	/* ************************** Methods ************************** */

	private void startClient(Scanner scan)
	{
		try {
			Socket skt = new Socket(serverhost, serverport);
			Thread.sleep(1000); // waiting for network communication for 1000 ms
			ServerThread serverThread = new ServerThread(skt, username);
			Thread serverAccessThread = new Thread(serverThread);
			serverAccessThread.start();

			while(serverAccessThread.isAlive())
			{
				if(scan.hasNextLine())
				{
					serverThread.addNextMessage(scan.nextLine());
				}
			}
		}
		catch(Exception e)
		{
			System.err.println("Connection error!, Server Maybe Close");
		}
	}



	/* ************************** Main ************************** */
	public static void main(String[] args)
	{
		String ClientName = null;
		Scanner scan = new Scanner(System.in);
		System.out.println("Enter username:");
		while(ClientName == null || ClientName.trim().equals(""))
		{
			ClientName = scan.nextLine();
			if(ClientName.trim().equals("")) // remove whitespaces
			{
				System.out.println("Invalid username, try again");
			}
		}
		// Now there a user with name == ClientName
		Client client = new Client(ClientName,host,PORT);
		client.startClient(scan);
	}
}
