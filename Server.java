/**
 * This is a server that stores files. Has two options: PUT <filename> or GET <filename>
 * 
 * 
 * @author Drenguin (from hubpages.com), Modified by Arnav Aggarwal (arnav0908_at_gmail.com)
 * Around 30% of the code is from Drenguin, rest is orginal from Arnav Aggarwal
 * @version July 24 2016
 */

import java.net.*;
import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;
 

public class Server extends JFrame
{
    //A JTextArea to hold the information received from clients
    JTextArea chatBox = new JTextArea();
    public static Socket[] peerConnects = new Socket[10];
    //public static String[] peerNames = new String[10];
    public static int SERVER_PORT = 5555;     
    public static void main(String[] args)
    {
        new Server();
    }
     
    public Server()
    {
        //Set up a layout for window
        setLayout(new BorderLayout());
        //Only display text, do not allow editing
        chatBox.setEditable(false);
        //Add our chatbox in the center with scrolling abilities
        add(new JScrollPane(chatBox), BorderLayout.CENTER);
         
        setTitle("File Transfer Server");
        setSize(550,400);
        //If the user closes then exit out
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Show the frame
        setVisible(true);
         
        //try catch due to high number of possible errors.
        try {
            ServerSocket sSocket = new ServerSocket(SERVER_PORT);
            chatBox.append("Server started at: " + new Date());
             
            //Loop that runs server functions
            while(true) {
                //Wait for a client to connect
                Socket socket = sSocket.accept();
                System.out.println("recieved new connection on port: " + socket.getPort());
                for(int i = 0; i < peerConnects.length;i++)
                {
                	if(peerConnects[i] == null)
                	{
                		peerConnects[i] = socket;
                		System.out.println("Stored new socket at array index "+ i + " at port "+ socket.getPort());
                		break;
                	}
                }
             
                //Create a new custom thread to handle the connection
                ClientThread cT = new ClientThread(socket);
                 
                //Start the thread!
                new Thread(cT).start();
                 
            }
        } catch(IOException exception) 
        {
            System.out.println("Error: " + exception);
        }
    }
     
    //create the ClientThread inner class and have it implement Runnable
    //This means that it can be used as a thread
    class ClientThread implements Runnable
    {
        Socket threadSocket;
        public static final String SPECIAL_END_STRING = "..";

        //This constructor will be passed the socket
        public ClientThread(Socket socket)
        {
            //Here we set the socket to a local variable so we can use it later
            threadSocket = socket;
        }
         /*Run() method layout:
          * 
          * Read line from socket
          * if line begins with PUT
          * {
          *   create file specified in command 
          *   while read from socket is not equal to *special character*
          *   write to file what was read from socket
          *   close file 
          * }
          * if line begins with GET
          * {
          *   User gives .txt file name
          *   Find file with the header given
          *   print every line of the .txt file
          *   flush and close file
          *   
          * }
          * if line begins with CurUsers
          * {
          * Take global array (in server class)
          * print out all the user ports
          * }
          * print BYE to socket and exit 
          */
        
        public void run()
        {
    		try
    		{
    			
    			//InputStreamReader reads every character
    			InputStreamReader reader = new InputStreamReader(threadSocket.getInputStream());
    			// This is the server handle/ (Reads the whole line)
            	BufferedReader bsReader = new BufferedReader(reader);
            	
            	//This is the "everything" (mainly text) coming out of the socket. 
            	PrintWriter sOutput = new PrintWriter(threadSocket.getOutputStream(), true);
            	
            	sOutput.println("Connected to Arnav socket at " + new Date() + "\r\n");
            	
            	sOutput.println("Functions (case does not matter): PUT, GET, CurrUsers");
            	// input handler
            	String socketString = bsReader.readLine();
            	String[] arr = socketString.split(" ");

            	
            	//Check which function to carry out
            	if (arr[0].equalsIgnoreCase("PUT")) 
            	{
            		sOutput.println(">>> Received command PUT");
            		
            		//Server handler
            		FileWriter fileWriter = new FileWriter(arr[1]);
            		BufferedWriter bfwriter = new BufferedWriter(fileWriter);
            		// create file
            		File tempFile = new File(arr[1]);
            		String path = tempFile.getAbsolutePath();
            		sOutput.println(">>> Ready to start copying file to " + arr[1] + "\r\n");
            		sOutput.println(">>> Please type text. Press Enter to save line. QUIT or .. to exit");
            		while(true)
            		{
            			//Reads the buffer handler from the socket
            			socketString = bsReader.readLine();
            			if(socketString.equals(null) || socketString.equals(SPECIAL_END_STRING) || socketString.equals("QUIT") )
            			{
            				sOutput.println(">>> Special character or QUIT \r\n");
            				sOutput.println("Path at: " + path);
            				break;
            				
            			}
            			
            			//Writes line
            			bfwriter.write(socketString);
            			sOutput.println(">>> Stored line");
            		}
            		//Nothing is written to file unless closed. This takes care of flushing as well
            		bfwriter.close();
            		//Checks the size of file after it has been closed
            		long size = tempFile.length(); 
    				sOutput.println("Size of File is: " + size + " in bytes"); 
    				threadSocket.close();
    				sOutput.println(">>> GoodBye");
            				
            	} // end bracket of PUT
            	
            	//GET file from server
            	else if(arr[0].equalsIgnoreCase("GET"))
            	{
            		sOutput.println(">>> Recieved command GET");
            		// Reads what file client wants to retrieve
            		BufferedReader in = new BufferedReader(new FileReader(arr[1]));
    	        	String line;
    	        	sOutput.println(">>> Attempting to print out file: " + arr[1]);
            		File tempFile = new File(arr[1]);
    	        	long size = tempFile.length(); 
    	        	sOutput.println(arr[1] + "file is " + size + " bytes" );
    	        	
    	        	//Prints line by line
    	        	while((line = in.readLine()) != null)
    	        	{
    	        	    sOutput.println(line);
    	        	}
    	        	in.close();
    	        	sOutput.println(">>> GoodBye");
    	        	sOutput.flush();
    	        	sOutput.close();
    	        	
            	} //End bracket of GET
            	
            	//List current users
            	
            	else if(arr[0].equalsIgnoreCase("CurrUsers"))
            	{
            		sOutput.println(">>> Recieved command Current Users");
            		for(int m = 0; m < peerConnects.length; m++)
            		{
            			if(peerConnects[m] != null)
            			{
            				sOutput.println("Port number: " + peerConnects[m].getPort() + "// User number: " + m);
            				sOutput.flush();
            			}	
            		}
            		sOutput.println(">>> Goodbye!");
            		sOutput.close();	
            	}
            	
    		} // end of try
    		
    		catch(IOException exception)
    		{
                System.out.println("Error: " + exception);
                
                
            }

        } // end of run class
    } // end ClientThread class
}//end of server class