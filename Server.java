import java.net.*;
import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;
 
public class Server extends JFrame
{
    //A JTextArea to hold the information received from clients
    JTextArea chatBox = new JTextArea();
     
    public static void main(String[] args)
    {
        new Server();
    }
     
    public Server()
    {
        //We need to set up a layout for our window
        setLayout(new BorderLayout());
        //Only display text, do not allow editing
        chatBox.setEditable(false);
        //Add our chatbox in the center with scrolling abilities
        add(new JScrollPane(chatBox), BorderLayout.CENTER);
         
        setTitle("Chat Server");
        setSize(550,400);
        //If the user closes then exit out
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Show the frame
        setVisible(true);
         
        //We need a try-catch because lots of errors can be thrown
        try {
            ServerSocket sSocket = new ServerSocket(5000);
            chatBox.append("Server started at: " + new Date());
             
            //Loop that runs server functions
            while(true) {
                //Wait for a client to connect
                Socket socket = sSocket.accept();
             
                //Create a new custom thread to handle the connection
                ClientThread cT = new ClientThread(socket);
                 
                //Start the thread!
                new Thread(cT).start();
                 
            }
        } catch(IOException exception) {
            System.out.println("Error: " + exception);
        }
    }
     
    //Here we create the ClientThread inner class and have it implement Runnable
    //This means that it can be used as a thread
    class ClientThread implements Runnable
    {
        Socket threadSocket;
         
        //This constructor will be passed the socket
        public ClientThread(Socket socket)
        {
            //Here we set the socket to a local variable so we can use it later
            threadSocket = socket;
        }
         
        public void run()
        {
            //All this should look familiar
            try {
                //Create the streams
                PrintWriter output = new PrintWriter(threadSocket.getOutputStream(), true);
                BufferedReader input = new BufferedReader(new InputStreamReader(threadSocket.getInputStream()));
                 
                //Tell the client that he/she has connected
                output.println("You have connected at: " + new Date());
                chatBox.append("Client connected\n");
                 
                while (true) {
                    //This will wait until a line of text has been sent
                    String chatInput = input.readLine();
                    //Add the chat to the text box
                    chatBox.append(chatInput+"\n");
                    System.out.println(chatInput);
                }
            } catch(IOException exception) {
                System.out.println("Error: " + exception);
            }
        }
    }
}