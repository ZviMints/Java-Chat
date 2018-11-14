/**
 * This class represent Server
 * @author Tzvi Mints And Or Abuhazira
 */
package Client;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
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
/**
 * Construct of the Client class
 * @param clientName is the current client name
 * @param host is the current host, ex "localhost"
 * @param port is the port server
 */
	public Client(String clientName, String host, int port) {
		this.username = clientName;
		this.serverhost = host;
		this.serverport = port;
	}
	/* ************************** Methods ************************** */
	/**
	 * Init the client after pressing "Login"
	 */
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
	/**
	 * This method is responsible to get Text from TextField 
	 * @return the name
	 */
	private static String getNameFromForm()
	{
		return txtEnterNameHere.getText();
	}
	/**
	 * This method checks whether the server window is open or closed
	 * @return true if frame is close, false if else.
	 */
	private static boolean Closed()
	{
		return  !login_frame.isVisible();
	}
	/* ************************** InitWindow ************************** */
	static JTextField txtEnterNameHere;
	static JFrame login_frame;
	static boolean AlredyInUse = false;
	private static JTextField tf_address;
	private static JTextField tf_port;
	public static void InitWindow()
	{
		login_frame = new JFrame();
		login_frame.setTitle("T&O Chat: Login\r\n");
		login_frame.setBounds(100, 100, 522, 188);
		login_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		login_frame.getContentPane().setLayout(null);

		txtEnterNameHere = new JTextField();
		txtEnterNameHere.setText("");
		txtEnterNameHere.setFont(new Font("Courier New", Font.PLAIN, 20));
		txtEnterNameHere.setBounds(230, 67, 254, 49);
		login_frame.getContentPane().add(txtEnterNameHere);
		txtEnterNameHere.setColumns(10);
		ImageIcon icon = new ImageIcon("./img/icon.png"); // Set Icon to Chat
		login_frame.setIconImage(icon.getImage());
		JLabel login_title = new JLabel(new ImageIcon("./img/login_title.png"));
		JLabel login_button = new JLabel(new ImageIcon("./img/login.png"));
		
		JLabel lbl_number = new JLabel("host:");
		lbl_number.setBounds(10, 7, 105, 20);
		lbl_number.setFont(new Font("Arial", Font.BOLD, 16));
		login_frame.getContentPane().add(lbl_number);

		JLabel lbl_user = new JLabel("port:");
		lbl_user.setBounds(10, 29, 500, 20);
		lbl_user.setFont(new Font("Arial", Font.BOLD, 16));
		login_frame.getContentPane().add(lbl_user);
		
		
		
		login_frame.getContentPane().add(login_button);
		login_frame.getContentPane().add(login_title);
		login_title.setBounds(260, 10, 204, 69);
		login_button.setBounds(20, 60, 204, 69);
		
		tf_address = new JTextField();
		tf_address.setText("localhost");
		tf_address.setBounds(60, 5, 111, 23);
		login_frame.getContentPane().add(tf_address);
		tf_address.setColumns(10);
		
		tf_port = new JTextField();
		tf_port.setText("9999\r\n");
		tf_port.setBounds(60, 26, 56, 26);
		login_frame.getContentPane().add(tf_port);
		tf_port.setColumns(10);
		login_frame.setVisible(true);
		login_frame.setResizable(false);
		login_button.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if(txtEnterNameHere.getText().contains(" ") || txtEnterNameHere.getText().trim().equals("")) // remove whitespaces
				{
					JOptionPane.showMessageDialog(null, "Invalid name! \n "
							+ "the name contain spaces or empty"); 
				}
				else
				{
					boolean ServerOpen = false;
					try {
						Socket skt = new Socket(host, PORT);
						ServerOpen = true;
						PrintWriter out = new PrintWriter(skt.getOutputStream(), true);
						BufferedReader in = new BufferedReader(new InputStreamReader(skt.getInputStream()));
						while(skt.isConnected())
						{					
							out.println(txtEnterNameHere.getText());
							if(in.readLine().equals("OK"))
							{
								skt.close();
								login_frame.setVisible(false);
								login_frame.dispose();
							}
							else
							{
								JOptionPane.showMessageDialog(null, "Invalid name! \n "
										+ "the name alredy in use."); 
								skt.close();
							}	
							System.out.println(in.readLine());
						}
					}
					catch (IOException e1) {
						if(!ServerOpen)
							JOptionPane.showMessageDialog(null, "Error, \n "
									+ "Can't connect to the Server on:" + "\n"
									+"host:"+tf_address.getText()+"\n"
											+ "port:"+tf_port.getText()+"."); 
					}	
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