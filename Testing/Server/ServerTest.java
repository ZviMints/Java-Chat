package Server;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ServerTest {

	@Test
	void testServer() {
		Server server = new Server(9999); // Init Server with port PORT
		server.startServer(); // Start running the Server
		Server server2 = new Server(9999); // Init Server with port PORT
		server2.startServer(); // Start running the Server

		}


//	@Test At Server
//	void test2Users_SameName() throws IOException {  // Check if two users are in the same name		
//		Client client = new Client("test","localhost",9999);
//		client.startClient();
//		Client client2 = new Client("test","localhost",9999);
//		client2.startClient();
//		if(!client2.skt.isClosed()) // If Socket is Available
//		{
//			// All Good.
//		}
//		else
//		{
//			fail("Could init 2 clients with same name!");	
//		}
//	}


	//	@Test
	//	void testBroadcast() { // Test Broadcast message
	//		fail("Not yet implemented");
	//	}
	//
	//	@Test
	//	void testPrivate() { // Test Private message
	//		fail("Not yet implemented");
	//	}
	//
	//	@Test
	//	void testOnline() { // Test Who is Online
	//		fail("Not yet implemented");
	//	}

//	@Test
//	void testGetClients() {
//		fail("Not yet implemented");
//	}
//
//
//	@Test
//	void testContainName() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testGetNames() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testInitWindow_AfterStart() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testInitWindow_BeforeStart() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testMain() {
//		fail("Not yet implemented");
//	}
//
}
