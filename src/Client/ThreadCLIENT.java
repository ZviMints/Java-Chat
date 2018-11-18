/**
 * This class represent Client THREAD!
 * @author Tzvi Mints And Or Abuhazira
 */
package Client;
import java.net.Socket;
import java.util.Scanner;
import java.util.Vector;
import java.io.*;

public class ThreadCLIENT implements Runnable {
	private Socket skt;
	private String username;
	private final Vector<String> msgV; // Vector is synchronized!
	private boolean hasMsg = false;
	private ClientGUI gui;
	boolean exit = false;
	private Scanner ClientIN;
	
	// Client Messages:
	// <close> : Disconnect the current user
	// <getnames> : Get the online client list names
	// Note: No need to CS Messages for Broadcast and Private.
	
	/* ************************** Setters and Getters ************************** */
	/**
	 * Construct of the Thread
	 * @param skt is the current socket
	 * @param username is the current username
	 */
	public ThreadCLIENT(Socket skt, String username) {
		this.skt = skt;
		this.username = username;
		msgV = new Vector<String>(); 
	}
	/* ************************** addNextMessege ************************** */
	/**
	 * This method is responsible to add Message to the Link List
	 * @param message is the added message
	 */
	public void addNextMessage(String message){
			hasMsg = true;
			msgV.add(message);
		}
	/* ************************** Stop function ************************** */
	/**
	 * this method is responsible to stop the Thread and send to Server close message
	 * @throws IOException
	 */
	public void stop() throws IOException{
		PrintWriter output = new PrintWriter(skt.getOutputStream(),false);
		output.println(username+"<close>");
		output.flush();
		exit = true; // Close the Thread
	}
	/* ************************** Run THREAD method ************************** */
	@Override
	public void run() {
		while(!exit)
		{
			try {
				gui = new ClientGUI(username,""+skt.getLocalAddress(),""+skt.getRemoteSocketAddress(),this);		
				PrintWriter output = new PrintWriter(skt.getOutputStream(),false);
				InputStream input = skt.getInputStream();
				ClientIN = new Scanner(input); 
				while(skt.isConnected()){ // Someone else sent message
					if(input.available() > 0)
					{
						gui.setNewMsg(ClientIN.nextLine());
					}
					if(hasMsg) // if This Thread Send Message
					{
						String msgtodeliever = "";
						msgtodeliever = msgV.get(0);
						msgV.remove(0);
						hasMsg = !msgV.isEmpty();
						output.println(msgtodeliever);
						output.flush();
					}
				}
			}	  
			catch(Exception e)
			{
				System.err.println("Error! From ServerThread");
			}
		}
	}
}