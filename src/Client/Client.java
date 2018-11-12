/**
 * This class represent Server
 * @author Tzvi Mints And Or Abuhazira
 */
package Client;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

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

	/* ************************** InitWindow ************************** */
	public static void InitWindow()
	{
		JFrame login_frame = new JFrame();
		login_frame.setTitle("T&O Chat: Login\r\n");
		login_frame.setBounds(100, 100, 522, 188);
		login_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		login_frame.getContentPane().setLayout(null);

		JTextField txtEnterNameHere = new JTextField();
		txtEnterNameHere.setBounds(230, 67, 254, 49);
		login_frame.getContentPane().add(txtEnterNameHere);
		txtEnterNameHere.setColumns(10);
		ImageIcon icon = new ImageIcon("./img/icon.png"); // Set Icon to Chat
		login_frame.setIconImage(icon.getImage());
		JLabel login_title = new JLabel(new ImageIcon("./img/login_title.png"));
		JLabel login_button = new JLabel(new ImageIcon("./img/login.png"));

		login_frame.add(login_button);
		login_frame.add(login_title);
		login_title.setBounds(260, 10, 204, 69);
		login_button.setBounds(20, 60, 204, 69);
		login_frame.setVisible(true);

		// On Click "Login"
		login_button.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if(txtEnterNameHere.getText().contains(" ")) // remove whitespaces
				{
					JOptionPane.showMessageDialog(null, "Invalid name! \n the name contain spaces or \n"
							+"alredy in use"); 
					/// NEED TO ADD SEARCH FOR USED
				}
				else
				{
					// NEED TO SEND txtEnterNameHere.getText() and CLOSE .
				}
			}
		});
	}
	
	/* ************************** Main ************************** */
	public static void main(String[] args)
	{
		InitWindow();
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