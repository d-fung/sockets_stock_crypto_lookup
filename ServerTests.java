//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertTrue;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;


public class ServerTests {
    

    private Server server;

    @Before
    public void initializeServer() throws Exception {
        server = new Server("testing");
    }

    @Test
    public void testAddUser(){
        server.addUser("user1", "password1");
        assertTrue(server.checkUser("user1", "password1"));
    }

    @Test
    public void testCheckUser(){
        server.addUser("user2", "password2");
        assertTrue(server.checkUser("user2", "password2"));
        assertFalse(server.checkUser("user2", "wrong_password"));
        assertFalse(server.checkUser("wrong_user", "password2"));
    }

    @Test
    public void testGetStockPrice(){
        assertNotNull(Server.getStockPrice("aapl"));
    }

    @Test 
    public void testGetCryptoPrice(){
        assertNotNull(Server.getCryptoPrice("btc"));
    }

}
