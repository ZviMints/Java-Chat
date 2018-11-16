package Client;
import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime; 
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTextArea;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;

public class ClientGUI{
	private JFrame frmToChatChat;
	private String username;
	private String info_localport ;
	private String info_Server;
	private JTextArea allMsgFromallUsers; // The Big Msg from all users
	private JTextArea textArea_msg; // What User Type
	private int count = 1;
	private ThreadCLIENT threadCLIENT;
	private JLabel online_lbl;
	private JButton btn_send;

	/**
	 * Create the application.
	 * @param skt 
	 * @param threadCLIENT 
	 * @throws IOException 
	 */

	public ClientGUI(String info_username,String info_localport, String info_Server, ThreadCLIENT threadCLIENT ) throws IOException {	
		this.username = info_username;
		this.info_localport = info_localport;
		this.info_Server = info_Server;
		initialize();
		this.frmToChatChat.setVisible(true);
		this.threadCLIENT = threadCLIENT;

	}
	/**
	 * Initialize the contents of the frame.
	 * @throws IOException 
	 */
	private void initialize() throws IOException {
		frmToChatChat = new JFrame();
		frmToChatChat.setTitle("T&O Chat: "+username+" Chat");
		frmToChatChat.setBounds(100, 100, 714, 591);

		frmToChatChat.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) { // Closing the frame
				try {
					threadCLIENT.stop();
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.exit(0);
			}
		});
		ImageIcon icon = new ImageIcon("./img/icon.png"); // Set Icon to Chat
		frmToChatChat.setIconImage(icon.getImage());
		frmToChatChat.getContentPane().setLayout(null);


		JLabel info_username = new JLabel("Username: "+this.username);
		info_username.setBounds(15, 0, 189, 34);
		frmToChatChat.getContentPane().add(info_username);

		JLabel info_localport = new JLabel("Localport: "+this.info_localport);
		info_localport.setBounds(15, 32, 178, 20);
		frmToChatChat.getContentPane().add(info_localport);

		JLabel info_Server = new JLabel("Server: "+this.info_Server);
		info_Server.setBounds(15, 59, 174, 20);
		frmToChatChat.getContentPane().add(info_Server);

		JButton btn_connected = new JButton(new ImageIcon("./img/WIO.png"));
		btn_connected.addActionListener(new ActionListener() { // Whoisonline has Pressed
                                                               // JButton Action
			public void actionPerformed(ActionEvent arg0) {
				threadCLIENT.addNextMessage(username+"<getnames>");       
			}
		});
		btn_connected.setBounds(536, 16, 141, 29);
		frmToChatChat.getContentPane().add(btn_connected);
		JButton btn_reset = new JButton(new ImageIcon("./img/CLEAR.png"));
		btn_reset.setBounds(536, 55, 141, 29);
		btn_reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) { // Reset has Pressed
				                                            // JButton Action
				allMsgFromallUsers.setText("");
			}
		});
		frmToChatChat.getContentPane().add(btn_reset);		
		final ImageIcon imageIcon_textArea_msg = new ImageIcon("./img/textArea_msg.png");
	    textArea_msg = new JTextArea() { // Making TextArea From Image
	      Image image = imageIcon_textArea_msg.getImage();
	      {
	        setOpaque(false);
	      }
	      public void paint(Graphics g) {
	        g.drawImage(image, 0, 0, this);
	        super.paint(g);
	      }
	    };	
		textArea_msg.setBounds(15, 465, 566, 54);
		textArea_msg.setFont(new Font("Courier New", Font.PLAIN, 20));
		textArea_msg.setBackground(Color.LIGHT_GRAY);
		textArea_msg.setForeground(Color.BLACK);

		frmToChatChat.getContentPane().add(textArea_msg);
		textArea_msg.addKeyListener(new KeyListener() { // Pressing Enter Send Message
			@Override
				public void keyPressed(KeyEvent e) {
					if(e.getKeyChar()==KeyEvent.VK_ENTER) {
						btn_send.doClick();
						}	
				}
			@Override
			public void keyReleased(KeyEvent e) {}
			@Override
			public void keyTyped(KeyEvent e) {}
		});
		btn_send = new JButton(new ImageIcon("./img/SEND.png"));
		btn_send.setBounds(596, 465, 81, 54);
		btn_send.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { // Send Button has Clicked
                                                         // JButton Action				
				if(!textArea_msg.getText().trim().equals(""))
				{

					String info_username = getUsername();
					String UserInput = textArea_msg.getText();

					threadCLIENT.addNextMessage("<"+getTime()+">"+"["+info_username +"]: " + UserInput);
					textArea_msg.setText("");
				}

				else
				{
					String message = "Error! \n"
							+ "You Must Type Somthing Before Sending a Message!\n";
					JOptionPane.showMessageDialog(new JFrame(), message, "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		frmToChatChat.getContentPane().add(btn_send);
		frmToChatChat.setResizable(false);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(15, 95, 662, 327);
		frmToChatChat.getContentPane().add(scrollPane);		
		final ImageIcon AllMessages = new ImageIcon("./img/AllMessages.png");
		allMsgFromallUsers = new JTextArea() { // Making TextArea From Image
	      Image image = AllMessages.getImage();
	      {
	        setOpaque(false);
	      }
	      public void paint(Graphics g) {
	        g.drawImage(image, 0, 0, this);
	        super.paint(g);
	      }
	    };	
	    allMsgFromallUsers.setBackground(Color.CYAN);
	    allMsgFromallUsers.setForeground(Color.BLACK);	  
		scrollPane.setViewportView(allMsgFromallUsers);
		allMsgFromallUsers.setLineWrap(true);
		allMsgFromallUsers.setText(
				"             (*)To Write Private Message: \r\n"
				+ "                 Write @<name>|<msg>\r\n"
				+ "******************************************************");
		allMsgFromallUsers.setFont(new Font("Courier New", Font.PLAIN, 20));
		allMsgFromallUsers.setBackground(Color.CYAN);
		allMsgFromallUsers.setForeground(Color.BLACK);
		allMsgFromallUsers.setEditable(false);

		online_lbl = new JLabel("<"+getCount()+"> People are online to chat right now.");
		online_lbl.setFont(new Font("Arial", Font.ITALIC, 17));
		online_lbl.setBounds(15, 429, 366, 20);
		frmToChatChat.getContentPane().add(online_lbl);
	}
	/* ************************** Setters and Getters ************************** */
	private int getCount()
	{
		return count;
	}
	private String getUsername() {
		return this.username;
	}
	public void setNewMsg(String msg) {
		if(msg.contains("<update>"))
		{
			String s_count = msg.replaceAll("<update>", "");
			count = Integer.parseInt(s_count.substring(0, 1));	
			online_lbl.setText("<"+count+"> People are online to chat right now.");
			msg = s_count.substring(1);

		}
		else if(msg.equals("<closeme>")) // gets here only if server close himself
		{
			System.exit(0);
		}
		String temp = allMsgFromallUsers.getText() + "\n" + msg;
		allMsgFromallUsers.setText(temp);
	}
	public String getTime()
	{
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");  
		LocalDateTime now = LocalDateTime.now();  
		return dtf.format(now);
	}	
}