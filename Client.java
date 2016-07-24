import java.io.*;
import java.net.*;
//We no longer need a scanner to get input!
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
 
public class Client extends JFrame
{
    JTextField inputField = new JTextField();
    JTextArea chatBox = new JTextArea();
     
    PrintWriter output;
    BufferedReader input;
     
    public static void main(String[] args)
    {
        new Client();
    }
     
    public Client()
    {
        //Create a panel with the UI for getting input from user
        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        p.add(new JLabel("Type here:"), BorderLayout.WEST);
        p.add(inputField, BorderLayout.CENTER);
         
        setLayout(new BorderLayout());
        //Add the chatBox and the panel for getting user input
        add(new JScrollPane(chatBox), BorderLayout.CENTER);
        add(p, BorderLayout.SOUTH);
         
        //Add an action listener on the text field 
        //so we can get what the user is typing
        inputField.addActionListener(new TextFieldListener());
         
        //Housekeeping stuff
        setTitle("Chat Client");
        setSize(550,550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
         
        try {
            Socket socket = new Socket("localhost",5000);
            output = new PrintWriter(socket.getOutputStream(), true);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             
            //This will wait for the server to send the string to the client saying a connection
            //has been made.
            String inputString = input.readLine();
            chatBox.append(inputString+"\n");
        } catch (IOException exception) {
            System.out.println("Error: " + exception);
        }
    }
     
    //This class handles the text field
    class TextFieldListener implements ActionListener
    {
        //This method will be called when the user hits enter
        public void actionPerformed(ActionEvent e) {
            try {
                String userInput = inputField.getText();
                output.println(userInput);
                chatBox.append(userInput+"\n");
                inputField.setText("");
            } catch (Exception ex) {
                System.out.println(ex);
            }
        }
    }
}