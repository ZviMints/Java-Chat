/**
 * This class represent the Server
 * @version 1.0
 * @author Tzvi Mints And Or Abuhazira
 */
package Server;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Server {
	private static final int PORT = 9999; // Init the PORT for the server
	String currnet_username = ""; // Pick name from the client
	private static List<ThreadSERVER> clients; // list of all clients 
	private static ServerSocket myServer; // Init ServerSocket
	public static int count = 0; // Number of clients online


	/* **************************  Constructor ************************** */
	/**
	 * This method is responsible to init the server as Constructor
	 * @param PORT is the PORT of the server
	 */
	public Server(int PORT)
	{
	}
	/* **************************  Methods ************************** */
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
	/**
	 * This method is responsible for getting new users to chat
	 * @param myServer is the ServerSocket
	 */
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
						out.println("OK"); // There no such name at list.
						flag = true;
						skt.close();
					}
					else
					{
						out.println("NO"); // The name alredy in use
						flag = false;
						skt.close();
					}
				}
			}
			catch (Exception e){
				// All good, the Socket is closed.
			}	
			if(flag) // flag=true means that the name is current and we can open socket properly
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
	/**
	 * This method is responsible to send Client List as Object
	 * @return ThreadSERVER LIST
	 */
	public static List<ThreadSERVER> getClients()
	{
		return clients;
	}
	/**
	 * This method checks whether the server window is open or closed
	 * @return true if frame is close, false if else.
	 */
	private static boolean Closed()
	{
		return  !frame.isVisible();
	}
	/**
	 * This method is responsible to set text at TextArea (server side)
	 * @param msg is the message we want to appear on the TextArea
	 */
	public static void setText(String msg) {
		if(msg.contains("<update>"))
		{
			if(count!=0)
				lbl_user.setText("CLIENTS NAMES:"+getNames());
			else
				lbl_user.setText("CLIENTS NAMES: NULL");
			lbl_number.setText("ONLINE:"+count);
		}
		msg_TA.setText(msg_TA.getText() + "\n" + msg);
	}
	/**
	 * this method is responsible to check if the name is alredy in use.
	 * @param name is the name we get from client
	 * @return true if the name are in use, false if else.
	 */
	public boolean ContainName(String name)
	{
		for(ThreadSERVER client : getClients())
		{
			if(client.name.equals(name))
				return true;
		}
		return false;
	}
	/**
	 * This method is responsible to send back all the names in the client list
	 * @return all names in client list in this form "name_1,name_2,name_3.."
	 */
	public static String getNames()
	{
		String s = "";
		for(ThreadSERVER client : getClients())
		{
			if(s!="")
				s+=",";
			s = s + client.name;
		}
		return s;
	}
	/**
	 * The Frame after the user press "Start the Server"
	 */
	/* ************************** InitWindow_AfterStart ************************** */
	static JTextArea msg_TA; // The TextArea that shows all the messages at the server
	static JFrame login_frame; // main JFrame
	static JLabel lbl_user; // Label that represent the names of the clients that online
	static JLabel lbl_number; // Label that represent the number of online clients
	public static void InitWindow_AfterStart()
	{
		login_frame = new JFrame();
		login_frame.setTitle("T&O SERVER");
		login_frame.setBounds(400, 200, 490, 602);
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
		lbl_number = new JLabel("ONLINE:"+count);
		lbl_number.setBounds(2, 480, 105, 20);
		lbl_number.setFont(new Font("Arial", Font.BOLD, 16));
		login_frame.getContentPane().add(lbl_number);

		lbl_user = new JLabel("CLIENTS NAMES: NULL");
		lbl_user.setBounds(2, 500, 500, 20);
		lbl_user.setFont(new Font("Arial", Font.BOLD, 16));
		login_frame.getContentPane().add(lbl_user);

		JButton btnNewButton_1 = new JButton("Clean");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				msg_TA.setText("");
			}});
		btnNewButton_1.setBounds(2, 530, 479, 29);
		login_frame.getContentPane().add(btnNewButton_1);

		msg_TA = new JTextArea("Server is ON!");
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(5, 25, 475, 450);

		msg_TA.setFont(new Font("Courier New", Font.PLAIN, 20));
		msg_TA.setBackground(Color.DARK_GRAY);
		msg_TA.setForeground(Color.GREEN);
		msg_TA.setEditable(false);
		msg_TA.setLineWrap(true);
		login_frame.getContentPane().add(msg_TA);
		ImageIcon icon = new ImageIcon("./img/icon.png"); // Set Icon to Chat
		login_frame.setIconImage(icon.getImage());

		JLabel lbl_host = new JLabel("The server is listening on: "+myServer.getInetAddress()+":"+myServer.getLocalPort());
		lbl_host.setBounds(2, 2, 500, 20);
		lbl_host.setFont(new Font("Arial", Font.BOLD, 16));
		login_frame.add(lbl_host);
		login_frame.setResizable(false);
		login_frame.setVisible(true);
		scrollPane.setViewportView(msg_TA);
		login_frame.getContentPane().add(scrollPane);

	}
	/* ************************** InitWindow_BeforeStart ************************** */
	/**
	 * The Frame before the user press "Start the Server"
	 */
	static JFrame frame;
	public static void InitWindow_BeforeStart() {
		frame = new JFrame("Start \"T&O\" Server");
		frame.setBounds(100,100,482,160);
		frame.setVisible(true);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) { 
				System.exit(0);
			}
		});
		frame.setResizable(false);
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
			}});}
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
