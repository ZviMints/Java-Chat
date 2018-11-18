/**
 * This Interface is responsible to make Server send private and broadcast messages
 * @author Tzvi Mints and Or Abuhazira
 */
package Server;
public interface ServerInt {
	public void Broadcast(String message); // Send Broadcast Message
	public void Private(String to, String from,String message, boolean LIST); // Send Private Message
}
