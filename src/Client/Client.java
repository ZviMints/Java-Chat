/**
 * This class represent Server
 * @author Tzvi Mints And Or Abuhazira
 */
package Client;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;


public class Client {
	
	private static String host ;
	private static int PORT ;
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
	private static String getName_form()
	{
		return USERNAME_Tf.getText();
	}
	private static String getHOSTFromForm()
	{
		return IP_Tf.getText();
	}
	private static int getPORTFromForm()
	{
		return Integer.parseInt(PORT_Tf.getText());
	}
	private static boolean Closed()
	{
		return  !login_frame.isVisible();
	}
	/* ************************** InitWindow ************************** */
	static JTextField USERNAME_Tf;
	static JTextField IP_Tf;
	static JTextField PORT_Tf;
	static JLabel PORT_Lb;
	static JLabel IP_Lb;
	static JFrame login_frame;
	static boolean AlredyInUse = false;
	public static void InitWindow()
	{
		//Frame JFrame
		login_frame = new JFrame();
		login_frame.setTitle("T&O Chat: Login\r\n");
		login_frame.setBounds(100, 100, 522, 188);
		login_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		login_frame.getContentPane().setLayout(null);
		
		//IP TextField
		IP_Tf = new JTextField();
		IP_Tf.setText("localhost");
		IP_Tf.setFont(new Font("Courier New", Font.PLAIN, 20));
		IP_Tf.setBounds(65, 5, 120,20);
		IP_Tf.setColumns(10);
		
		//IP Label
		IP_Lb= new JLabel("IP");
		IP_Lb.setBounds(20, 5, 150, 20);
		
		//PORT TextField
		PORT_Tf = new JTextField();
		PORT_Tf.setText("9999");
		PORT_Tf.setFont(new Font("Courier New", Font.PLAIN, 20));
		PORT_Tf.setBounds(65, 30, 120,20);
		PORT_Tf.setColumns(10);
		
		//PORT Label
		PORT_Lb= new JLabel("PORT");
		PORT_Lb.setBounds(20, 30, 150, 20);
		
		//USERNAME TextField
		USERNAME_Tf = new JTextField();
		USERNAME_Tf.setText("");
		USERNAME_Tf.setFont(new Font("Courier New", Font.PLAIN, 20));
		USERNAME_Tf.setBounds(230, 67, 254, 49);
		
		//JFrame adding,visible and etc.
		login_frame.getContentPane().add(USERNAME_Tf);
		login_frame.getContentPane().add(PORT_Tf);
		login_frame.getContentPane().add(IP_Tf);
		login_frame.add(IP_Lb);
		login_frame.add(PORT_Lb);
		USERNAME_Tf.setColumns(10);
		ImageIcon icon = new ImageIcon("./img/icon.png"); // Set Icon to Chat
		login_frame.setIconImage(icon.getImage());
		JLabel login_title = new JLabel(new ImageIcon("./img/login_title.png"));
		JLabel login_button = new JLabel(new ImageIcon("./img/login.png"));
		login_frame.add(login_button);
		login_frame.add(login_title);
		login_title.setBounds(260, 10, 204, 69);
		login_button.setBounds(20, 60, 204, 69);
		login_frame.setVisible(true);
		login_frame.setResizable(false);
		                               // On Click "Login":
		login_button.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if(getName_form().contains(" ") || getName_form().trim().equals("")) // remove whitespaces
				{
					JOptionPane.showMessageDialog(null, "Invalid name! \n "
							+ "the name contain spaces or empty"); 
				}
				else
				{
					boolean ServerOpen = false; // Server Open = if Server is running
					try {
						host = getHOSTFromForm();
						PORT = getPORTFromForm();
						Socket skt = new Socket(host, PORT);
						ServerOpen = true;
						PrintWriter out = new PrintWriter(skt.getOutputStream(), true);
						BufferedReader in = new BufferedReader(new InputStreamReader(skt.getInputStream()));
						while(skt.isConnected())
						{					
							out.println(getName_form());
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
							JOptionPane.showMessageDialog(null, "Sorry, \n "
									+ "The Server is CLOSE"); 
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
				Client client = new Client(getName_form(),host,PORT);
				client.startClient();
				break;
			}
		}
	}
}