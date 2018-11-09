/**
 * This class represent Server
 * @author Tzvi Mints And Or Abuhazira
 * Cloned: https://gist.github.com/fliedonion/1002293af6fd043fbd6e729c13018562
 */
package Client;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import java.awt.Font;
import java.io.*;

public class ServerThread implements Runnable {
	private Socket skt;
	private String username;
	private boolean isAlive;
	private final LinkedList<String> msgLL;
	private boolean hasMsg = false;
	private ClientGUI gui;


	/* ************************** Setters and Getters ************************** */

	public ServerThread(Socket skt, String username) {
		this.skt = skt;
		this.username = username;
		msgLL = new LinkedList<String>();
	}
	/* ************************** Methods ************************** */

	public void addNextMessage(String message){
		synchronized (msgLL){
			hasMsg = true;
			msgLL.push(message);
		}
	}
	@Override
	public void run() {
		try {
		gui = new ClientGUI(username,""+skt.getLocalAddress(),""+skt.getRemoteSocketAddress(), skt);
			PrintWriter out = new PrintWriter(skt.getOutputStream(),false);
			InputStream in = skt.getInputStream();
			Scanner serverin = new Scanner(in); 
			while(skt.isConnected()){
				if(in.available() > 0){
					if(serverin.hasNextLine()){
						System.err.println(serverin.nextLine());
					}
				}
				if(hasMsg)
				{
					String msgtodeliever = "";
					synchronized(msgLL){
						msgtodeliever = msgLL.pop();
						hasMsg = !msgLL.isEmpty();
					}
				}
				gui.setNewMsg(serverin.nextLine());

				
			}
		}	  
		catch(Exception e)
		{
			System.err.println("Error! From ServerThread");
		}
	}
}
