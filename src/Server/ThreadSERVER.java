/**
 * This class represent Server THREAD!
 * @author Tzvi Mints And Or Abuhazira
 */
package Server;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

public class ThreadSERVER implements Runnable {
	private Socket skt;
	private PrintWriter output;
	private Server myServer;
	public String name = "";

	/* ************************** Constructor ************************** */

	public ThreadSERVER(Server server, Socket skt) {
		this.myServer = server;
		this.skt = skt;
	}

	/* ************************** Setters And Getters ************************** */
	private PrintWriter getWriter()
	{
		return this.output;
	}
	/* ************************** Run ( SERVER THREAD ) ************************** */

	@Override
	public void run() {
		try {
			this.output = new PrintWriter(skt.getOutputStream(),false);
			Scanner in = new Scanner(skt.getInputStream());
			while(skt.isConnected()) // There connection with the server the current CLIENT
			{
				if(in.hasNextLine())
				{
					String msg = in.nextLine();
					for(ThreadSERVER client : myServer.getClients()) // if there a messege from the client
						// it will send it to all clients
					{
						PrintWriter pw_oftheclient = client.getWriter();
						if(msg.contains("@")) // Private msg
						{

						}
						if(pw_oftheclient != null ) // there a message from the client
						{
							pw_oftheclient.write(msg + "\n");
							pw_oftheclient.flush();
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