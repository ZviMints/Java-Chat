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
	private String info_username;
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
		this.info_username = info_username;
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
		frmToChatChat.setTitle("T&O Chat: "+info_username+" Chat");
		frmToChatChat.setBounds(100, 100, 454, 501);
		
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


		JLabel info_username = new JLabel("Username: "+this.info_username);
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
				String m = Server.Server.whoIsOnline();
			    JOptionPane.showMessageDialog(null, m);           
			}
		});
		btn_connected.setBounds(272, 16, 141, 29);
		frmToChatChat.getContentPane().add(btn_connected);
		JButton btn_reset = new JButton("Clear");
		btn_reset.setBounds(272, 55, 141, 29);
		btn_reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) { // Reset has Pressed
				allMsgFromallUsers.setText("");
			}
		});
		frmToChatChat.getContentPane().add(btn_reset);

		textArea_msg = new JTextArea();
		textArea_msg.setBounds(15, 375, 306, 54);
		textArea_msg.setFont(new Font("Courier New", Font.PLAIN, 20));
		textArea_msg.setBackground(Color.MAGENTA);
		frmToChatChat.getContentPane().add(textArea_msg);

		JButton btn_send = new JButton("Send");
		btn_send.setBounds(332, 375, 81, 54);
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
		scrollPane.setBounds(15, 95, 398, 235);
		frmToChatChat.getContentPane().add(scrollPane);
		
				allMsgFromallUsers = new JTextArea();
				scrollPane.setViewportView(allMsgFromallUsers);
				allMsgFromallUsers.setLineWrap(true);
				allMsgFromallUsers.setText(this.info_username+", Welcome To Chat");
				allMsgFromallUsers.setFont(new Font("Courier New", Font.PLAIN, 20));
				allMsgFromallUsers.setBackground(Color.CYAN);
				allMsgFromallUsers.setEditable(false);
				
				JLabel lbl_WhoIsOnline = new JLabel("<"+Server.Server.count+">"+" People are online right now.");
				lbl_WhoIsOnline.setBounds(15, 331, 291, 34);
				frmToChatChat.getContentPane().add(lbl_WhoIsOnline);
	}

	/* ************************** Setters and Getters ************************** */

	private String getUsername() {
		return this.info_username;
	}
	public void setNewMsg(String msg) {
		String temp = allMsgFromallUsers.getText() + "\n" + msg;
		allMsgFromallUsers.setText(temp);
	}		
}