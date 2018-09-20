package ceov2.org;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.NumberUtils;

import java.io.IOException;
import java.util.ArrayList;

public class ServerCommunications {
    //server variables

    Socket clientSocket;
    ArrayList<String> messagesToDealWith=new ArrayList<String>();

    public ServerCommunications(String ipAddress,int port){
        try {

            SocketHints socketHints = new SocketHints();
            //attempt to connect to the server for 2 seconds
            socketHints.connectTimeout = 2000;
            clientSocket = Gdx.net.newClientSocket(Net.Protocol.TCP, ipAddress, port, socketHints);

        }catch (GdxRuntimeException e){
            System.out.println("failed to connect to server");
        }
    }
//read the messages sent by the server into an ArrayList
void readMessages(){
    //read all data server has sent
    String serverMessages=readClientsMessage();

    int numOfMessages=1;
    //count how many newLine characters exist
for (int x=0;x!=serverMessages.length();x++){
    if (serverMessages.charAt(x)=='\n'){
        //if the newLine character is the last character of the message, don't increase numOfMessages
        if (x!=serverMessages.length()-1) {
            numOfMessages++;
        }
    }
}
//separate the messages by line
String[] separatedMessages=serverMessages.split("\n");
//loop through messages and add them to the queue of messages that need to be dealt with
for (int x=0;x!=numOfMessages;x++){
    if (separatedMessages[x].length()>0) {
        messagesToDealWith.add(separatedMessages[x]);
        System.out.println("received by client: "+ separatedMessages[x]);
    }

}
}

//read all data in the inputStream of the client into a String and return it
    String readClientsMessage(){
        byte[] messageByte=new byte[1000];
        try {
            try {
                //check is the socket is connected
                if (clientSocket.isConnected()) {
                    //check if there is data to be read
                    if (clientSocket.getInputStream().available() != 0) {
                        //read data into messageByte
                        clientSocket.getInputStream().read(messageByte);
                    }
                }
            }catch (NullPointerException e){

            }
        }catch (IOException e){
            System.out.println("message failed to load");
        }
        //convert data into a String
        String message=new String(messageByte);
        //the string message above has length 1000, because that is the size of the byte array it was
        //created with, .trim() removes the extra "empty" characters
        message=message.trim();
        return message;
    }
//send any String message to the server
    void sendMessageToServer(String message){
       System.out.println("sent to server: "+message);
        try {
            try {
                clientSocket.getOutputStream().write(message.getBytes());
                clientSocket.getOutputStream().flush();
            }catch (NullPointerException e){
                 System.out.println("socket does not exist");
            }
        } catch (IOException e) {
           System.out.println("message failed to write");
        }
    }
//return the first message in the queue
    String getFirstClientMessageInQueue(){
        if (messagesToDealWith.size()>0) {
            return messagesToDealWith.get(0);
        }else{
            return null;
        }
    }
//remove the first message in the queue, called once the message has been dealt with
    void removeFirstClientMessageInQueue(){
        messagesToDealWith.remove(0);
    }

//close data streams
    void closeStreams(){
        try {
            try {
                clientSocket.getInputStream().close();
                clientSocket.getOutputStream().close();
            }catch (NullPointerException e){
                System.out.println("clientSocket object doesn't exist");
            }
        }catch (IOException e){
            System.out.println("failed to close streams,likely due to streams not existing");
        }
    }

//get the state of the connection
    boolean getState(){
        boolean connected=false;
        try {
            if (clientSocket.isConnected() == true) {
                connected = true;
            }
        }catch (NullPointerException e){

        }
        return connected;
    }
}
