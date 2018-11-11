/**
 * This class represent Server
 * @author Tzvi Mints And Or Abuhazira
 */
package Client;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.Set;

public class Client {
	private static final String host = "localhost";
	private static final int PORT = 9999;
	private String username;
	private String serverhost;
	private int serverport;


	/* ************************** Setters and Getters ************************** */

	public Client(String clientName, String host, int port) {
		this.username = clientName;
		this.serverhost = host;
		this.serverport = port;
	}

	/* ************************** Methods ************************** */

	private void startClient()
	{
		try {
			Socket skt = new Socket(serverhost, serverport);
			Thread.sleep(1000); // waiting for network communication for 1000 ms
			ThreadCLIENT threadCLIENT = new ThreadCLIENT(skt, username);
			Thread serverAccessThread = new Thread(threadCLIENT); // Open new Thread for each user
			serverAccessThread.start();

		    PrintStream output = new PrintStream(skt.getOutputStream());
		    // send UserName to server
		    output.println(username);
	
		  
		}
		catch(Exception e)
		{
			System.err.println("Connection error!, Server Maybe Close");
		}
	}

	/* ************************** Main ************************** */
	public static void main(String[] args)
	{
		String get_username = null; 
		Scanner scan = new Scanner(System.in);
		System.out.println("Enter username:");
		while(get_username == null || get_username.trim().equals(""))
		{
			get_username = scan.nextLine();
			if(get_username.trim().equals("")) // remove whitespaces
			{
				System.out.println("Invalid username, try again");
			}
		}
		// Now there a user with name == get_username
		Client client = new Client(get_username,host,PORT);
		client.startClient();
	}
}