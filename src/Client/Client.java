/**
 * This class represent Server
 * @author Tzvi Mints And Or Abuhazira
 */
package Client;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.io.PrintStream;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import Server.ThreadSERVER;

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

	/* ************************** InitWindow Setters and Getters ************************** */
	private static String getNameFromForm()
	{
		return txtEnterNameHere.getText();
	}
	private static boolean Closed()
	{
		return  !login_frame.isVisible();
	}
	
	/* ************************** InitWindow ************************** */
	static JTextField txtEnterNameHere;
	static JFrame login_frame;
	static boolean AlredyInUse = false;
	public static void InitWindow()
	{
		login_frame = new JFrame();
		login_frame.setTitle("T&O Chat: Login\r\n");
		login_frame.setBounds(100, 100, 522, 188);
		login_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		login_frame.getContentPane().setLayout(null);

		txtEnterNameHere = new JTextField();
		txtEnterNameHere.setText("");
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

		login_button.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if(txtEnterNameHere.getText().contains(" ") || txtEnterNameHere.getText().trim().equals("")) // remove whitespaces
				{
					JOptionPane.showMessageDialog(null, "Invalid name! \n "
							+ "the name contain spaces or empty"); 
				}
//				else if(AlredyInUse(txtEnterNameHere.getText())) // if name alredy in use
//				{
//					JOptionPane.showMessageDialog(null, "Invalid name! \n "
//							+ "the name alredy in use"); 
//				}
				else
				{
					login_frame.setVisible(false);
					login_frame.dispose();
				}
			}
		});
	}

	/* ************************** Main ************************** */
	public static void main(String[] args) throws InterruptedException
	{
		InitWindow();
		while(true)
		{
			Thread.sleep(500);
			if(Closed())
			{
				Client client = new Client(getNameFromForm(),host,PORT);
				client.startClient();
				break;
			}
		}
	}
}