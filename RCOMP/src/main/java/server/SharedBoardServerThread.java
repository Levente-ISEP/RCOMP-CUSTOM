package server;

import messageUtils.MessageService;
import messageUtils.SBPMessage;
import messageUtils.SharedConstants;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class SharedBoardServerThread implements Runnable {

    private Socket s;
    private DataOutputStream sOut;
    private DataInputStream sIn;
    private static MessageService messageService;

    public SharedBoardServerThread(Socket cli_s) {
        s = cli_s;
    }

    public void run() {
        InetAddress clientIP;

        clientIP = s.getInetAddress();
        System.out.println("New client connection from " + clientIP.getHostAddress() +
                ", port number " + s.getPort());

        try {
            sOut = new DataOutputStream(s.getOutputStream());
            sIn = new DataInputStream(s.getInputStream());
            messageService = new MessageService();
            do {
                SBPMessage message = messageService.readMessage(sIn);

                switch (message.code()) {
                    case SharedConstants.COMMTEST_CODE:
                        System.out.println("Test request coming from " + clientIP.getHostAddress() + ", port number " + s.getPort());
                        commtestResponse(sOut, sIn);
                        break;

                    case SharedConstants.DISCONN_CODE:
                        System.out.println("Disconnect request coming from " + clientIP.getHostAddress() + ", port number " + s.getPort());
                        disconnResponse(sOut, sIn, s);
                        break;

                    case SharedConstants.AUTH_CODE:
                        System.out.println("Authentication request coming from " + clientIP.getHostAddress() + ", port number " + s.getPort());
                        authResponse(sOut, sIn, message);
                        break;

                    case SharedConstants.OWNED_BOARDS_REQUEST_CODE:
                        System.out.println("Owned boards request coming from " + clientIP.getHostAddress() + ", port number " + s.getPort());
                        ownedBoardsResponse(sOut, sIn);
                        break;

                    case SharedConstants.SHARE_BOARD_REQUEST_CODE:
                        System.out.println("Share board request coming from " + clientIP.getHostAddress() + ", port number " + s.getPort());
                        shareBoardResponse(message, sOut, sIn);
                        break;
                }
            } while (true);
        } catch (IOException ex) {
            System.out.println(clientIP.getHostAddress() + " disconnected successfully");
        }
    }

    public static void commtestResponse(DataOutputStream sOut, DataInputStream sIn) throws IOException {
        //write response
        SBPMessage responseMessage = new SBPMessage(1, SharedConstants.ACK_CODE, 0, 0, "");
        messageService.sendMessage(responseMessage, sOut);
    }

    public static void disconnResponse(DataOutputStream sOut, DataInputStream sIn, Socket s) throws IOException {
        //write response and close tcp connection
        SBPMessage responseMessage = new SBPMessage(1, SharedConstants.ACK_CODE, 0, 0, "");
        messageService.sendMessage(responseMessage, sOut);

        sOut.close();
        sIn.close();
        s.close();
    }

    public static void authResponse(DataOutputStream sOut, DataInputStream sIn, SBPMessage message) throws IOException {
        String username = "";
        String password = "";
        char currentChar;
        int currentCharIndex = 0;

        while (((currentChar = message.data().charAt(currentCharIndex)) != '\0') && currentCharIndex < (message.d_length_1() + message.d_length_2())) {  //read username
            username = username.concat(String.valueOf(currentChar));
            currentCharIndex++;
        }
        currentCharIndex++;

        while (((currentChar = message.data().charAt(currentCharIndex)) != '\0') && currentCharIndex < (message.d_length_1() + message.d_length_2())) {  //read password
            password = password.concat(String.valueOf(currentChar));
            currentCharIndex++;
        }

        SBPMessage responseMessage = new SBPMessage(1, SharedConstants.ACK_CODE, 0, 0, "");
        messageService.sendMessage(responseMessage, sOut);

    }

    public static void ownedBoardsResponse(DataOutputStream sOut, DataInputStream sIn) throws IOException {
        SBPMessage responseMessage;


            String data = "";


            data = data.concat("Random title" + "\0");


            responseMessage = new SBPMessage(SharedConstants.MESSAGE_VERSION, SharedConstants.OWNED_BOARDS_RESPONSE_CODE, data);

        //send response
        messageService.sendMessage(responseMessage, sOut);
    }

    public static void shareBoardResponse(SBPMessage message, DataOutputStream sOut, DataInputStream sIn) throws IOException {
        SBPMessage responseMessage;
        String notAddedUsers = "";
        char currentChar;
        int currentCharIndex = 0;
        boolean addedAtLeastOneUser = false;

        //read selected board name
        String boardName = "";

        while ((currentChar = message.data().charAt(currentCharIndex)) != '\0') {
            boardName = boardName.concat(String.valueOf(currentChar));
            currentCharIndex++;
        }

        currentCharIndex++;


        //read users info and share the board to them
        do {
            String userInfo = "";

            while ((currentChar = message.data().charAt(currentCharIndex)) != '\0') {
                userInfo = userInfo.concat(String.valueOf(currentChar));
                currentCharIndex++;
            }

            String[] splittedUserInfo = userInfo.split(";");



            currentCharIndex++;

        } while (currentCharIndex < (message.d_length_1() + message.d_length_2()));



        responseMessage = new SBPMessage(SharedConstants.MESSAGE_VERSION, SharedConstants.SHARE_BOARD_RESPONSE_CODE, "\nThe board was shared with the users successfully!\n");

        //finally send response
        messageService.sendMessage(responseMessage, sOut);
    }

    protected static void sendMessage(SBPMessage message, DataOutputStream sOut) {
        try {
            messageService.sendMessage(message, sOut);
        } catch (Exception e) {
            System.out.println("Could not send message due to the following error-> " + e.getMessage());
            System.exit(1);
        }
    }

    protected static SBPMessage readMessage(DataInputStream sIn) {
        try {
            return messageService.readMessage(sIn);
        } catch (SocketTimeoutException e) {
            System.out.println("Connection Timeout!");
            System.exit(1);
        } catch (Exception e) {
            System.out.println("Could not read message due to the following error -> " + e.getMessage());
            System.exit(1);
        }
        return null;
    }
}
