package Client;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTextArea;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;

public class ClientGUI {
	private JFrame frmToChatChat;
	private String username;
	private String info_localport ;
	private String info_Server;
	private JTextArea allMsgFromallUsers; // The Big Msg from all users
	private JTextArea textArea_msg; // What User Type
	private Socket skt;
	private ThreadCLIENT threadCLIENT;
	/**
	 * Create the application.
	 * @param skt 
	 * @param threadCLIENT 
	 * @throws IOException 
	 */
	
	public ClientGUI(String info_username,String info_localport, String info_Server, Socket skt, ThreadCLIENT threadCLIENT ) throws IOException {	
		this.username = info_username;
		this.info_localport = info_localport;
		this.info_Server = info_Server;
		this.skt = skt;
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
		frmToChatChat.setBounds(100, 100, 504, 564);
		
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

		JButton btn_connected = new JButton("Who Is Online?");
		btn_connected.addActionListener(new ActionListener() { // Whoisonline has Pressed
			public void actionPerformed(ActionEvent arg0) {
				threadCLIENT.addNextMessage(username+"<getnames>");       
			}
		});
		btn_connected.setBounds(326, 16, 141, 29);
		frmToChatChat.getContentPane().add(btn_connected);
		JButton btn_reset = new JButton("Clear");
		btn_reset.setBounds(326, 55, 141, 29);
		btn_reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) { // Reset has Pressed
				allMsgFromallUsers.setText("");
			}
		});
		frmToChatChat.getContentPane().add(btn_reset);

		textArea_msg = new JTextArea();
		textArea_msg.setBounds(15, 438, 356, 54);
		textArea_msg.setFont(new Font("Courier New", Font.PLAIN, 20));
		textArea_msg.setBackground(Color.MAGENTA);
		frmToChatChat.getContentPane().add(textArea_msg);

		JButton btn_send = new JButton("Send");
		btn_send.setBounds(386, 438, 81, 54);
		btn_send.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { // Send Button has Clicked
				if(!textArea_msg.getText().trim().equals(""))
				{
					String info_username = getUsername();
					String UserInput = textArea_msg.getText();

					threadCLIENT.addNextMessage("["+ info_username +"]: " + UserInput);
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
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(15, 95, 452, 327);
		frmToChatChat.getContentPane().add(scrollPane);
		
				allMsgFromallUsers = new JTextArea();
				scrollPane.setViewportView(allMsgFromallUsers);
				allMsgFromallUsers.setLineWrap(true);
				allMsgFromallUsers.setText(
						 "   (*)To Write Private Message: \r\n"
						+ "        Write @<name>|<msg>\r\n\r\n "
						+ "  (*)To close the chat, press X\r\n"
						+ " ____________________________________");
				allMsgFromallUsers.setFont(new Font("Courier New", Font.PLAIN, 20));
				allMsgFromallUsers.setBackground(Color.CYAN);
				allMsgFromallUsers.setEditable(false);
	}

	/* ************************** Setters and Getters ************************** */

	private String getUsername() {
		return this.username;
	}
	public void setNewMsg(String msg) {
		System.err.println(msg);
		String temp = allMsgFromallUsers.getText() + "\n" + msg;
		allMsgFromallUsers.setText(temp);
	}		
}