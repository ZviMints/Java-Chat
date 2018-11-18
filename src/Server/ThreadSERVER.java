/**
 * This class represent Server THREAD!
 * @author Tzvi Mints And Or Abuhazira
 */
package Server;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ThreadSERVER implements Runnable {
	private Socket skt;
	private PrintWriter output;
	private Scanner input;
	public String name = "";

	/* ************************** Constructor ************************** */
	/**
	 * Construct Thread Server
	 * @param server is the current server
	 * @param skt is the current socket
	 */
	public ThreadSERVER(Socket skt) {
		this.skt = skt;
	}

	/* ************************** Setters And Getters ************************** */
	/**
	 * Getters for PrintWriter
	 * @return PrintWriter of currect socket
	 */
	private PrintWriter getWriter()
	{
		return this.output;
	}
	/**
	 * This method is responsible to return ThreadServer Obj by Name
	 * @param name is the client name
	 * @return  ThreadServer Thread
	 */
	private ThreadSERVER GetClientByName(String name)
	{
		ThreadSERVER ans = null;
		for(ThreadSERVER client : Server.getClients()) 
		{
			if(name.equals(client.name))
				ans = client;
		}
		return ans;
	}
	/**
	 * This method is responsible to send back all the names in the client list
	 * @return all names in client list in this form "name_1,name_2,name_3.."
	 */
	public String getNames()
	{
		String s = "";
		for(ThreadSERVER client : Server.getClients())
		{
			if(s!="")
				s+=",";
			s = s + client.name;
		}
		return s;
	}

	/* ************************** Broadcast ************************** */
	/**
	 * This method send message from server to all clients in Server client list
	 * @param message is the message we want to deliver
	 */
	public void Broadcast(String message){
		Server.setText(message);

		for(ThreadSERVER client : Server.getClients())
		{
			PrintWriter pw_oftheclient = client.getWriter();
			pw_oftheclient.write(message + "\n");
			pw_oftheclient.flush();
		}
	}
	/* ************************** Private ************************** */
	/**
	 * This method send PRIVATE message to user
	 * @param to is the name of the client that need to get message
	 * @param from is the name of the client that send the message
	 * @param message is the message we want to deliver
	 * @param LIST is true if we want to send Who's online message and false if its regular private message
	 */
	public void Private(String to, String from,String message, boolean LIST) { // LIST IS FOR SHOWING WHO ONLINE
		boolean found = false;
		for(ThreadSERVER client : Server.getClients()) 
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
						catch(Exception e) // Wrong format of private message
						// ex . @message or other.
						{
							int index_name = msg.indexOf("]");
							int index_triangle = msg.indexOf(">");
							String username_from = msg.substring(index_triangle+2,index_name);
							String temp = "Wrong Format! Try: '@<name>|<msg>'";
							Private(username_from,username_from,temp,false);
						}
					}
					else // It's not a private message
					{
						if(msg.contains("<close>")) // Closing a Thread
						{
							int index_triangle = msg.indexOf("<");
							String username = msg.substring(0,index_triangle);
							Server.getClients().remove(GetClientByName(username));
							Server.count--;
							Server.setText("*****************" + "\n" +
									"Closing \""+username+"\"..." + "\n" +
									"Left Online:"+Server.count + "\n" +
									"\""+ username +"\" Removed From Client List" + "\n" +
									"*****************");
							Broadcast("<update>"+Server.count+" -> ["+ username +"] Has Disconnected");
							skt.close();
						}
						else if(msg.contains("<getnames>")) // Send back the names that online right now
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