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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Server {
	private static int PORT ; // Init the PORT for the server
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
					Scanner sc = new Scanner (skt.getInputStream());
					this.currnet_username = sc.nextLine();
					setText("New Client: \"" + this.currnet_username + "\"" + "\n"
							+ "Host:" + skt.getInetAddress().getHostAddress() + "\n"
							+ "***********************************" );

					ThreadSERVER client = new ThreadSERVER(this, skt); // Init Obj of ClientThread
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
	public boolean ContainName(String name)
	{
		for(ThreadSERVER client : getClients())
		{
			if(client.name.equals(name))
				return true;
		}
		return false;
	}
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
	static JTextArea msg_TA;
	static JFrame login_frame;
	static JLabel lbl_user;
	static JLabel lbl_number;
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
				frame.setVisible(false);
				frame.dispose();
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
