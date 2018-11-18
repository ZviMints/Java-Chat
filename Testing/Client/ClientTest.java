package Client;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.net.ServerSocket;
import org.junit.jupiter.api.Test;

class ClientTest {
	ServerSocket Server;
	// We Are assuming that Server is Working, After TestServer is completed.
	//Note: We are only need to check if there connection to the server, the server is the 
	//one who in charge of all other methods

	@Test
	void testClient() throws InterruptedException, IOException { // Test connection
		try {
			ServerSocket Server = new ServerSocket(9999);
			Client client = new Client("test","localhost",9998);
			client.startClient();
			fail("Couldnt Connect to the Server On Server PORT:"+Server.getLocalPort());
		}
		catch(Exception e)
		{
			// All Good.
		}
		try {
			Client client = new Client("test","localhost",9999);
			client.startClient();
		}
		catch(Exception e)
		{
			fail("Couldnt Connect to the Server On Server PORT:"+Server.getLocalPort());
		}
	}
}
