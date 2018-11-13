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
			if(s!="")
				s+=",";
			s = s + client.name;
		}
		return s;
	}

	/* ************************** Broadcast ************************** */
	public void Broadcast(String message){
		Server.setText(message);

		for(ThreadSERVER client : myServer.getClients())
		{
			PrintWriter pw_oftheclient = client.getWriter();
			pw_oftheclient.write(message + "\n");
			pw_oftheclient.flush();
		}
	}
	/* ************************** Private ************************** */
	public void Private(String to, String from,String message, boolean LIST) { // LIST IS FOR SHOWING WHO ONLINE
		boolean found = false;
		for(ThreadSERVER client : myServer.getClients()) 
		{
			if(client.name.equals(to))
			{
				PrintWriter pw_oftheclient = client.getWriter();
				if(!LIST)
				{
					pw_oftheclient.write(message + "\n");
					pw_oftheclient.flush();
					found = true;
					Server.setText(message);
				}
				else
				{
					pw_oftheclient.write(Server.count+" online users: " + message + "\n");
					pw_oftheclient.flush();	
					Server.setText(Server.count+" online users: " + message);
				}
			}
		}
		if(!LIST && !found)
		{
			Private(from,from,"-> Cant find \""+to+"\"",false);	
		}
	}
	/* ************************** Run ( SERVER THREAD ) ************************** */
	@Override
	public void run() {
		try {
			this.output = new PrintWriter(skt.getOutputStream(),false);
			Broadcast("<update>"+Server.count+" -> ["+ name +"] Has Connected");
			input = new Scanner(skt.getInputStream());
			while(skt.isConnected()) // There connection with the server the current CLIENT
			{
				if(input.hasNextLine())
				{
					String msg = input.nextLine();
					Server.setText(msg);

					if(msg.contains("@")) // Private msg
					{
						try {
							// MUST BE OF THE FORM @<name>|<msg>
							int index_a = msg.indexOf("@");
							int index_name = msg.indexOf("]");
							int index_line = msg.indexOf("|");
							int index_triangle = msg.indexOf(">");
							String time = msg.substring(1,index_triangle);
							String username_to = msg.substring(index_a+1,index_line);
							String username_from = msg.substring(index_triangle+2,index_name);
							String deliver = msg.substring(index_line+1);
							System.out.println(username_to+","+username_from+","+deliver);
							Private(username_to,username_from,"<"+time+">"+"[Private From "+username_from+"]: "+deliver,false);
						}
						catch(Exception e)
						{
							int index_name = msg.indexOf("]");
							int index_triangle = msg.indexOf(">");
							String username_from = msg.substring(index_triangle+2,index_name);
							String temp = "Wrong Format! Try: '@<name>|<msg>'";
							Private(username_from,username_from,temp,false);
						}
					}
					else
					{
						if(msg.contains("<close>"))
						{
							int index_triangle = msg.indexOf("<");
							String username = msg.substring(0,index_triangle);
							myServer.getClients().remove(GetClientByName(username));
							Server.count--;
							Server.setText("*****************" + "\n" +
									"Closing \""+username+"\"..." + "\n" +
									"Left Online:"+Server.count + "\n" +
									"\""+ username +"\" Removed From Client List" + "\n" +
									"*****************");
							Broadcast("<update>"+Server.count+" -> ["+ username +"] Has Disconnected");
							skt.close();
						}
						else if(msg.contains("<getnames>"))
						{
							int index_triangle = msg.indexOf("<");
							String username = msg.substring(0,index_triangle);
							String temp = getNames();
							Private(username,username,temp,true);
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