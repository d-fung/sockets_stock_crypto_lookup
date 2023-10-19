// David Fung 100767734
// October 19, 2023
// SOFE 4790U Assignment 1

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;


public class ServerTests {
    
    private Server server;

    // Create a new server before running the unit tests
    @Before
    public void initializeServer() throws Exception {
        server = new Server("testing");
    }

    // This tests the method to add a new user to the userCredentials hashmap
    @Test
    public void testAddUser(){
        server.addUser("user1", "password1");
        assertTrue(server.checkUser("user1", "password1"));
    }

    // This tests the method of checking if the username and password matches
    // an existing user in the hashmap
    @Test
    public void testCheckUser(){
        server.addUser("user2", "password2");
        assertTrue(server.checkUser("user2", "password2"));
        assertFalse(server.checkUser("user2", "wrong_password"));
        assertFalse(server.checkUser("wrong_user", "password2"));
    }

    // This tests the method of checking if the API request is working correctly
    // Since the real-time price is always changing, we can just check if it is not null
    @Test
    public void testGetStockPrice(){
        assertNotNull(Server.getStockPrice("aapl"));
    }

    // Same as above but for cryptocurrency
    @Test 
    public void testGetCryptoPrice(){
        assertNotNull(Server.getCryptoPrice("btc"));
    }

}
