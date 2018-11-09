/**
 * This class represent Server
 * @author Tzvi Mints And Or Abuhazira
 * Cloned: https://gist.github.com/fliedonion/1002293af6fd043fbd6e729c13018562
 */
package Server;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientThread implements Runnable {
	private Socket skt;
	private PrintWriter out;
	private Server myServer;
	
	
	public ClientThread(Server server, Socket skt) {
	this.myServer = server;
	this.skt = skt;
	}
	
	/* ************************** Setters And Getters ************************** */
	private PrintWriter getWriter()
	{
		return this.out;
	}
	
	@Override
	public void run() {
		try {
			this.out = new PrintWriter(skt.getOutputStream(),false);
			Scanner in = new Scanner(skt.getInputStream());
		
			while(skt.isConnected())
			{
				if(in.hasNextLine())
				{
					String msg = in.nextLine();
					for(ClientThread client : myServer.getClients()) // if there a messege from the client
						                                             // it will send it to all clients
					{
						PrintWriter clientOut = client.getWriter();
						if(clientOut != null ) // there a message from the client
						{
							clientOut.write(msg + "\n");
							clientOut.flush();
						}
					}
				}
			}
		
		
		}
		catch(Exception e)
		{
			System.err.println("Error from ClientThread");
		}
	}
}
