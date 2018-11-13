/**
 * This class represent Server
 * @author Tzvi Mints And Or Abuhazira
 */
package Server;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
		InitWindow_AfterStart();
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
		while(true) {
			try {
				Socket skt = myServer.accept();

				// Get username of new connection
				this.currnet_username = (new Scanner ( skt.getInputStream() )).nextLine();
				System.out.println("New Client: \"" + this.currnet_username + "\"" + "\n"
						+ "Host:" + skt.getInetAddress().getHostAddress() + "\n"
						+ "***********************************" );

				ThreadSERVER client = new ThreadSERVER(this, skt); // Init Obj of ClientThread
				clients.add(client); // client has added to the list
				client.name = this.currnet_username;
				count++; // inc online users
				Thread thread = new Thread(client); // Made a new Thread called thread
				thread.start(); // Now thread.run will work
			} catch (Exception e){
				System.err.println("Fail on accept: "+PORT);
			}	
		}
	}

	/* ************************** Setters And Getters ************************** */
	public static void print(String s)
	{
		System.out.println(s);
	}
	public static String whoIsOnline()
	{
		String s = "";
		for(ThreadSERVER thread : clients)
		{
			s += thread.name + "\n";
		}
		return s;
	}
	public List<ThreadSERVER> getClients()
	{
		return clients;
	}
	public Server(int PORT)
	{
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

	/* ************************** InitWindow_AfterStart ************************** */
	static JTextField msg_TA;
	static JFrame login_frame;
	public static void InitWindow_AfterStart()
	{
		login_frame = new JFrame();
		login_frame.setTitle("T&O Chat: Login\r\n");
		login_frame.setBounds(400, 400, 500, 500);
		login_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		login_frame.getContentPane().setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(40, 40, 400, 400);
		login_frame.getContentPane().add(scrollPane);
		JTextArea msg_TA = new JTextArea();
		scrollPane.setViewportView(msg_TA);
		msg_TA.setLineWrap(true);
		msg_TA.setText("Server is ON!");
		msg_TA.setFont(new Font("Courier New", Font.PLAIN, 20));
		msg_TA.setBackground(Color.CYAN);
		msg_TA.setEditable(false);
		
		ImageIcon icon = new ImageIcon("./img/icon.png"); // Set Icon to Chat
		login_frame.setIconImage(icon.getImage());
		login_frame.setVisible(true);
	}
	/* ************************** InitWindow_BeforeStart ************************** */
	static JFrame frame;
	public static void InitWindow_BeforeStart() {
		frame = new JFrame("Start \"T&O\" Server");
		frame.setBounds(100,100,500,200);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(frame.DISPOSE_ON_CLOSE);
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
	private static boolean Closed()
	{
		return  !frame.isVisible();
	}
}
