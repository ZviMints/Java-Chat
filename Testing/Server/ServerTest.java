package Server;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import Client.Client;

class ServerTest {

	@Test
	void testServer(){
		try 
		{
			Server wrong_port = new Server(1023); 
			wrong_port.startServer();
			fail("Could init Server with saved PORTS");
		}
		catch(Exception e)
		{
			// All Good.
		}
	}
}
