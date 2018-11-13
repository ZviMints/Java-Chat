/**
 * This class represent Server
 * @author Tzvi Mints And Or Abuhazira
 */
package Server;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Server {
	private static final int PORT = 9999; // Init the PORT for the server
	String currnet_username = ""; // for print Hello to server
	private static List<ThreadSERVER> clients; // list of all clients 
	private static ServerSocket myServer; //
	public static int count = 0;
	/**
	 * This Method is Starting the Server, Init ServerSocket.
	 */
	public void startServer()
	{
		clients = new ArrayList<ThreadSERVER>(); // init the client arraylist
		try {
			myServer = new ServerSocket(PORT); // Open the Server
			acceptClients(myServer); // Now the Server can Accept clients
		}
		catch(Exception e)
		{
			System.err.println("Couldn't init the Server with "+PORT+" port");
		}
	}

	/* ************************** Accept Clients ************************** */
	private void acceptClients(ServerSocket myServer) {
		InitWindow_AfterStart();
		while(true) {
			boolean flag = false;
			try {
				Socket skt = myServer.accept();
				while(skt.isConnected()) //  Checks if there is such a name in the system,
					                     // if NO, continues the process, 
					                     // if YES, will throw an error "the name in use" in the client window 
				{	
					PrintWriter out = new PrintWriter(skt.getOutputStream(), true);
					BufferedReader in = new BufferedReader(new InputStreamReader(skt.getInputStream()));
					String clientName = in.readLine();
					if(!ContainName(clientName))
					{
						out.println("OK");
						flag = true;
						skt.close();
					}
					else
					{
						out.println("NO");
						flag = false;
						skt.close();
					}
				}
			}
			catch (Exception e){
				// All good, the Socket is closed.
			}	
			if(flag)
			{
				try {
					Socket skt = myServer.accept();
					// Get username of new connection
					this.currnet_username = (new Scanner ( skt.getInputStream() )).nextLine();
					setText("New Client: \"" + this.currnet_username + "\"" + "\n"
							+ "Host:" + skt.getInetAddress().getHostAddress() + "\n"
							+ "***********************************" );

					ThreadSERVER client = new ThreadSERVER(this, skt); // Init Obj of ClientThread
					clients.add(client); // client has added to the list
					client.name = this.currnet_username;
					count++; // inc online users
					Thread thread = new Thread(client); // Made a new Thread called thread
					thread.start(); // Now thread.run will work
				} catch (IOException e) {
					System.err.println("Fail on accept: "+PORT);
				}
			}
		}
	}
	/* ************************** Setters And Getters ************************** */
	public static void print(String s)
	{
		System.out.println(s);
	}
	public static List<ThreadSERVER> getClients()
	{
		return clients;
	}
	public Server(int PORT)
	{
	}
	private static boolean Closed()
	{
		return  !frame.isVisible();
	}
	public static void setText(String msg) {
		msg_TA.setText(msg_TA.getText() + "\n" + msg);
	}
	public boolean ContainName(String name)
	{
		for(ThreadSERVER client : getClients())
		{
			if(client.name.equals(name))
				return true;
		}
		return false;
	}
	/* ************************** InitWindow_AfterStart ************************** */
	static JTextArea msg_TA;
	static JFrame login_frame;
	public static void InitWindow_AfterStart()
	{
		login_frame = new JFrame();
		login_frame.setTitle("T&O SERVER");
		login_frame.setBounds(400, 400, 505, 540);
		login_frame.addWindowListener(new WindowAdapter() {
			  public void windowClosing(WindowEvent we) { 
				for(ThreadSERVER client : getClients())
				{
					client.Broadcast("<closeme>");
				}
			    System.exit(0);
			  }
			});
		login_frame.getContentPane().setLayout(null);

		msg_TA = new JTextArea("Server is ON!");
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(5, 5, 475, 475);

		msg_TA.setFont(new Font("Courier New", Font.PLAIN, 20));
		msg_TA.setBackground(Color.DARK_GRAY);
		msg_TA.setForeground(Color.GREEN);
		msg_TA.setEditable(false);
		msg_TA.setLineWrap(true);
		login_frame.getContentPane().add(msg_TA);
		ImageIcon icon = new ImageIcon("./img/icon.png"); // Set Icon to Chat
		login_frame.setIconImage(icon.getImage());
		login_frame.setVisible(true);

		scrollPane.setViewportView(msg_TA);
		login_frame.getContentPane().add(scrollPane);

	}
	/* ************************** InitWindow_BeforeStart ************************** */
	static JFrame frame;
	public static void InitWindow_BeforeStart() {
		frame = new JFrame("Start \"T&O\" Server");
		frame.setBounds(100,100,500,200);
		frame.setVisible(true);
		frame.addWindowListener(new WindowAdapter() {
			  public void windowClosing(WindowEvent we) { 
			    System.exit(0);
			  }
			});
		
		ImageIcon icon = new ImageIcon("./img/icon.png"); // Set Icon to Chat
		frame.setIconImage(icon.getImage());

		// Adding Start Button
		JLabel start = new JLabel(new ImageIcon("./img/start.png"));
		start.setVisible(true);
		frame.add(start);
		start.setBounds(150, 40, 201, 49);

		// On Click "Start Server"
		start.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				// Close the last frame	

				frame.setVisible(false);
				frame.dispose();
			}
		});
	}
	/* ************************** Main ************************** */
	public static void main(String[] args) throws InterruptedException {
		InitWindow_BeforeStart();
		while(true)
		{
			Thread.sleep(500);
			if(Closed())
			{
				Server server = new Server(PORT); // Init Server with port PORT
				server.startServer(); // Start running the Server
				break;
			}
		}
	}
}
