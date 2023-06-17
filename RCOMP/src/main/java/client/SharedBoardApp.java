package client;

import client.ui.CreatePostItUI;
import client.ui.ShareBoardUI;
import messageUtils.MessageService;
import messageUtils.SBPMessage;
import messageUtils.SharedConstants;

import java.io.*;
import java.net.*;



public class SharedBoardApp {
    static InetAddress serverIP;
    static Socket sock;
    static MessageService messageService;

    public static void main(String[] args) throws Exception {
        try {
            serverIP = InetAddress.getByName("127.0.0.1");
        } catch (UnknownHostException ex) {
            System.out.println("Invalid server specified: " + args[0]);
            System.exit(1);
        }
        try {
            sock = new Socket(serverIP, SharedConstants.DEFAULT_PORT);
            sock.setSoTimeout(SharedConstants.TIMEOUT * 1000);
        } catch (SocketException e) {
            System.out.println("Failed to establish timeout in TCP connection");
            System.exit(1);
        }
        catch (IOException e){
            System.out.println("Failed to establish TCP connection");
            System.exit(1);
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        DataOutputStream sOut = new DataOutputStream(sock.getOutputStream());
        DataInputStream sIn = new DataInputStream(sock.getInputStream());
        messageService = new MessageService();
        String option;
        System.out.println("Welcome!");

        do {
            System.out.println("Please, choose an option:\n");
            System.out.println("1 - Login");
            System.out.println("2 - Exit");

            option = in.readLine();
            switch(option) {
                case "1":
                        loggedUserMenu(in, sOut, sIn);
                    break;

                case "2":
                    if(disconnRequest(in, sOut, sIn)) {
                        System.out.println("\nMessage code: ACK\n");
                        sOut.close();
                        sIn.close();
                        sock.close();
                        System.exit(0);
                    } else {
                        System.out.println("\nThere was an error.\n");
                    }
                    break;

                default:
                    System.out.println("\nInvalid option.\n");
                    break;
            }
        } while (true);
    }

    public static void loggedUserMenu(BufferedReader in, DataOutputStream sOut, DataInputStream sIn) throws IOException {
        String option;

        do {
            System.out.println("\nMain Menu:\n");
            System.out.println("1 - Send a test request");
            System.out.println("2 - Share a board");
            System.out.println("3 - Archive a board");
            System.out.println("4 - Create a post-it");
            //System.out.println("5 - Update a post-it");
            System.out.println("0 - Logout");

            option = in.readLine();

            switch(option) {
                case "0":
                    return;
                case "1":
                    if(commtestRequest(in, sOut, sIn)) {
                        System.out.println("\nMessage code: ACK\n");
                    }
                    break;
                case "2":
                    new ShareBoardUI(in, sOut, sIn).run();
                    break;
                case "3":
                    //new ArchiveBoardUI(sOut, sIn, messageService).show();
                    break;
                case "4":
                    new CreatePostItUI(in, sOut, sIn).run();
                    break;
                //case "5":
                    //new EditPostItIU(in, sOut, sIn).run();
                   // break;
                default:
                    System.out.println("\nInvalid option.\n");
                    break;
            }
        } while (true);
    }

    public static boolean authRequest(BufferedReader in, DataOutputStream sOut, DataInputStream sIn) throws IOException {
        String username, password;
        System.out.println("\nUsername:");
        username = in.readLine();
        System.out.println("Password:");
        password = in.readLine();
        String data = username + "\0" + password + "\0";
        SBPMessage requestMessage = new SBPMessage(1, SharedConstants.AUTH_CODE, data);  //send message
        sendMessage(requestMessage, sOut);
        return(readMessage(sIn).code() == SharedConstants.ACK_CODE);  //read response
    }

    public static boolean commtestRequest(BufferedReader in, DataOutputStream sOut, DataInputStream sIn) throws IOException {
        SBPMessage requestMessage = new SBPMessage(SharedConstants.MESSAGE_VERSION, SharedConstants.COMMTEST_CODE, "");  //send message
        sendMessage(requestMessage, sOut);
        return readMessage(sIn).code() == SharedConstants.ACK_CODE; //read response
    }

    public static boolean disconnRequest(BufferedReader in, DataOutputStream sOut, DataInputStream sIn) throws IOException {
        SBPMessage requestMessage = new SBPMessage(SharedConstants.MESSAGE_VERSION, SharedConstants.DISCONN_CODE, "");  //send message
        sendMessage(requestMessage, sOut);
        return readMessage(sIn).code() == SharedConstants.ACK_CODE; //read response
    }

    public static SBPMessage ownedBoardsRequest(BufferedReader in, DataOutputStream sOut, DataInputStream sIn) throws IOException {
        SBPMessage requestMessage = new SBPMessage(SharedConstants.MESSAGE_VERSION, SharedConstants.OWNED_BOARDS_REQUEST_CODE, "");  //send message
        messageService.sendMessage(requestMessage, sOut);
        return readResponse(sIn);  //read response
    }

    public static SBPMessage shareBoardRequest(BufferedReader in, DataOutputStream sOut, DataInputStream sIn, String data) throws IOException {
        SBPMessage requestMessage = new SBPMessage(SharedConstants.MESSAGE_VERSION, SharedConstants.SHARE_BOARD_REQUEST_CODE, data); //send message
        messageService.sendMessage(requestMessage, sOut);
        return readResponse(sIn); //read response
    }






    //---------------------------------- UTIL ----------------------------------//

    public static SBPMessage readResponse(DataInputStream sIn) throws IOException {
        return messageService.readMessage(sIn);
    }

    public static void sendMessage(SBPMessage message, DataOutputStream sOut){
        try{
            messageService.sendMessage(message, sOut);
        }
        catch (Exception e){
            System.out.println("Could not send message due to the following error-> "+e.getMessage());
            System.exit(1);
        }
    }

    public static SBPMessage readMessage(DataInputStream sIn){
        try{
            return messageService.readMessage(sIn);
        }
        catch (SocketTimeoutException e){
            System.out.println("Connection Timeout!");
            System.exit(1);
        }
        catch (Exception e){
            System.out.println("Could not read message due to the following error -> "+e.getMessage());
            System.exit(1);
        }
        return null;
    }
}


