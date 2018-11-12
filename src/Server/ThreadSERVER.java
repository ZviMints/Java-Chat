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
	private Scanner input;
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
	public String getOnlineList()
	{
		String s = "";
		for(ThreadSERVER client : myServer.getClients())
		{
			s = s + client.name + ",";
		}
		return s;
	}
	private ThreadSERVER GetClientByName(String name)
	{
		ThreadSERVER ans = null;
		for(ThreadSERVER client : myServer.getClients()) 
		{
			if(name.equals(client.name))
				ans = client;
		}
		return ans;
	}
	private String getNames()
	{
		String s = "";
		for(ThreadSERVER client : myServer.getClients())
		{
			s = s + client.name +"\n";
		}
		return s;
	}
	/* ************************** Broadcast ************************** */
	public void Broadcast(String message){
		for(ThreadSERVER client : myServer.getClients())
		{
			PrintWriter pw_oftheclient = client.getWriter();
			pw_oftheclient.write(message + "\n");
			pw_oftheclient.flush();
		}
	}
	/* ************************** Private ************************** */
	public void Private(String to,String from, String message) {
		for(ThreadSERVER client : myServer.getClients()) 
		{
			if(client.name.equals(to))
			{
				PrintWriter pw_oftheclient = client.getWriter();
				pw_oftheclient.write("[Private Msg From "+from+"]: "+message + "\n");
				pw_oftheclient.flush();
			}
		}
	}
	/* ************************** Run ( SERVER THREAD ) ************************** */
	@Override
	public void run() {
		try {
			this.output = new PrintWriter(skt.getOutputStream(),false);
			input = new Scanner(skt.getInputStream());
			while(skt.isConnected()) // There connection with the server the current CLIENT
			{
				if(input.hasNextLine())
				{
					String msg = input.nextLine();
					if(msg.contains("@")) // Private msg
					{
						System.out.println(msg);
						// MUST BE OF THE FORM @<name>|<msg>
						int index_a = msg.indexOf("@");
						int index_name = msg.indexOf("]");
						int index_line = msg.indexOf("|");
						String username_to = msg.substring(index_a+1,index_line);
						String username_from = msg.substring(1,index_name);
						String deliver = msg.substring(index_line+1);
						System.out.println(username_to+","+username_from+","+deliver);
						Private(username_to,username_from,deliver);
					}
					else
					{
						if(msg.contains("<close>"))
						{
							int index_triangle = msg.indexOf("<");
							String username = msg.substring(0,index_triangle);
							myServer.getClients().remove(GetClientByName(username));
							System.out.println("*****************");
							System.out.println("Closing \""+username+"\"...");
							Server.count--;
							System.out.println("Left Online:"+Server.count);
							System.out.println("\""+ username +"\" Removed From Client List");
							System.out.println("*****************");
							Broadcast(" -> ["+ username +"] Has Disconnected" + "\n");
							Broadcast("<update>"+Server.count);
							skt.close();
						}
						else if(msg.contains("<getnames>"))
						{
							int index_triangle = msg.indexOf("<");
							String username = msg.substring(0,index_triangle);
							Private(username,username,"<getnames>"+getNames());

						}
						else // BROADCAST MSG
						{
							Broadcast(msg);
						}
					}
				}
			}			
		}
		catch(Exception e)
		{
			System.err.println("Error from ThreadSERVER");
		}
	}
}