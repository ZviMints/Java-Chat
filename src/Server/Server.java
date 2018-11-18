/**
 * This class represent Server

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
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Server {
	private static int PORT = 9999; // Init the PORT for the server
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
		Server.PORT = PORT;
	}
	/* **************************  Methods ************************** */
	/**
	 * This Method is Starting the Server, Init ServerSocket.
	 */
	public void startServer()
	{
		clients = new ArrayList<ThreadSERVER>(); // Init the client Arraylist
		try {
			myServer = new ServerSocket(PORT); // Open the Server
			acceptClients(myServer); // Now the Server can Accept clients
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(null, "Couldn't init the Server with "+PORT+" port");
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
					Scanner sc = new Scanner (skt.getInputStream());
					this.currnet_username = sc.nextLine();
					setText("New Client: \"" + this.currnet_username + "\"" + "\n"
							+ "Host:" + skt.getInetAddress().getHostAddress() + "\n"
							+ "***********************************" );
					ThreadSERVER client = new ThreadSERVER(skt); // Init Object of ClientThread
					clients.add(client); // client has added to the list
					client.name = this.currnet_username;
					count++; // increase online users
					Thread thread = new Thread(client); // Made a new Thread called thread
					thread.start(); // Now thread.run will work
				}
				catch (IOException e) {
					System.err.println("Fail on accept: "+PORT);
				}
			}
		}
	}
	public static boolean hasName(String s) {
		for(ThreadSERVER client : clients)
		{
			if(client.name.equals(s)) {
				System.out.println(client.name);
				return false;
			}
		}
		return true;
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
	/* ************************** InitWindow_AfterStart ************************** */
	static JTextArea msg_TA; // The TextArea that shows all the messages at the server
	static JFrame login_frame; // main JFrame
	static JLabel lbl_user; // Label that represent the names of the clients that online
	static JLabel lbl_number; // Label that represent the number of online clients
	/**
	 * This method opens GUI After click on "Start Server" Button
	 */
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

		//Frame JFrame
		login_frame.getContentPane().setLayout(null);
		lbl_number = new JLabel("ONLINE:"+count);
		lbl_number.setBounds(2, 480, 105, 20);
		lbl_number.setFont(new Font("Arial", Font.BOLD, 16));
		login_frame.getContentPane().add(lbl_number);

		//USER LIST Label
		lbl_user = new JLabel("CLIENTS NAMES: NULL");
		lbl_user.setBounds(2, 500, 500, 20);
		lbl_user.setFont(new Font("Arial", Font.BOLD, 16));
		login_frame.getContentPane().add(lbl_user);
		
		//Clean JButton
		JButton btnNewButton_1 = new JButton("Clean");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				msg_TA.setText("");
			}
		});
		btnNewButton_1.setBounds(2, 530, 479, 29);
		login_frame.getContentPane().add(btnNewButton_1);

		//All Messages TextArea
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
		
		//Host Label
		JLabel lbl_host = new JLabel("The server is listening on: "+myServer.getInetAddress()+":"+myServer.getLocalPort());
		lbl_host.setBounds(2, 2, 500, 20);
		lbl_host.setFont(new Font("Arial", Font.BOLD, 16));
		
		//JFrame
		login_frame.add(lbl_host);
		login_frame.setResizable(false);
		login_frame.setVisible(true);
		scrollPane.setViewportView(msg_TA);
		login_frame.getContentPane().add(scrollPane);

	}
	/* ************************** InitWindow_BeforeStart ************************** */
	/**
	 * This method opens GUI Before click on "Start Server" Button
	 */
	static JTextField PORT_Tf;
	static JLabel PORT_Label;
	static JFrame frame;
	public static void InitWindow_BeforeStart() {

		//Frame JFRAME
		frame = new JFrame();
		frame.setTitle("T&O Chat: Login\r\n");
		frame.setBounds(100, 100, 522, 188);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		//PORT Tf
		PORT_Tf = new JTextField();
		PORT_Tf.setText("9999");
		PORT_Tf.setFont(new Font("Courier New", Font.PLAIN, 20));
		PORT_Tf.setBounds(80,50,120,20);
		PORT_Tf.setColumns(10);

		//PORT Label
		PORT_Label= new JLabel("PORT");
		PORT_Label.setBounds(20, 50, 150, 20);
		PORT_Label.setFont(new Font("Courier New", Font.PLAIN, 20));
		
		ImageIcon icon = new ImageIcon("./img/icon.png"); // Set Icon to Chat
		frame.setIconImage(icon.getImage());
		frame.add(PORT_Tf);
		frame.add(PORT_Label);

		//Start button JButton
		JLabel start = new JLabel(new ImageIcon("./img/start.png"));
		start.setVisible(true);
		frame.add(start);
		start.setBounds(250, 40, 201, 49);

		// On Click "Start Server"
		start.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				// Close the last frame	
				PORT = Integer.parseInt(PORT_Tf.getText());
				if(PORT > 1023) {
				frame.setVisible(false);
				frame.dispose();
				}
				else
					JOptionPane.showMessageDialog(null, "Invalid PORT! \n"
							+ "Ports in [0,1023] are saved PORTs"); 
			}
		});
		frame.setVisible(true);
		frame.setResizable(false);
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
