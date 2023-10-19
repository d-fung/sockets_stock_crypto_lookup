# SOFE 4790 Assignment 1
Instructor: Dr. Ahmed Badr\
TA: Ammar Elmoghazy\
\
David Fung
100767734 \
October 18, 2023
\
\
This project application was created to provide a graphical user interface for users to search for the real-time price of any stock or cryptocurrency using the security's ticker. This application also features a user sign-up and login system that can verify users before allowing access to server methods. The client-server communication was implemented using sockets with multi-threaded handling of client connections.
## Instructions to Run Application
1. Open the folder in command line
2. Run the Server using this command
- if on Windows: `java -cp ".;lib\* Server`
- if on Unix: `java -cp ".:lib/* Server`
3. Run the Client using this command `java Client`
4. Enter in username and password into the GUI and click "Sign up"
5. Using the same username and password, click "Log in"
6. Enter any valid stock ticker or cryptocurrency ticker and click "Get Stock Price" or "Get Crypto Price"

**Possible Issues:**
1. Ensure that a personal RAPIDAPI key was provided in the Server.java file for the `private final static String APIKEY = ""` variable
2. Ensure that you have subscribed to the [Twelve Data API](https://rapidapi.com/twelvedata/api/twelve-data1) on RapidAPI.
3. Ensure that the provided library JAR files have been correctly referenced in the project build, or you may have to recompile the java files
- on Windows: `javac -cp ".;lib\*" *.java`
- on Unix: `javac -cp ".:lib/*" *.java`
