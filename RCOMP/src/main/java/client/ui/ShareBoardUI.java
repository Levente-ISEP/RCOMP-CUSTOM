package client.ui;

import client.SharedBoardApp;
import messageUtils.SBPMessage;

import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static boardService.GridGenerator.generateHTMLGrid;

public class ShareBoardUI implements Runnable {

    static final int ACK_CODE = 2;
    static final int ERR_CODE = 3;

    BufferedReader in;
    DataInputStream sIn;
    DataOutputStream sOut;

    public ShareBoardUI(BufferedReader in, DataOutputStream sOut, DataInputStream sIn) {
        this.in = in;
        this.sIn = sIn;
        this.sOut = sOut;
    }

    @Override
    public void run() {
        enterBoardData(in);
    }
    public static void enterBoardData(BufferedReader in){
        int rows = 0;
        int cols = 0;
        String name = "";
        while((rows == 0 || rows > 20) || (cols == 0 || cols > 10)) {
            try {
                System.out.print("Enter the number of rows (1-20): ");
                rows = Integer.parseInt(in.readLine());
                System.out.print("Enter the number of columns (1-10): ");
                cols = Integer.parseInt(in.readLine());
                System.out.print("Enter the name of board: ");
                name = in.readLine();
                File f = new File(name.replaceAll("\\s","")+".html");
                while(f.exists()){
                    System.out.println("Board with name: "+name+" exists. Try a new one:");
                    name =in.readLine();
                    f = new File(name.replaceAll("\\s","")+".html");
                }
            }catch(Exception e){
                System.out.println("Error"+e.getMessage());
            }
        }
        generateHTMLGrid(rows, cols, name);
    }
    public static String selectUsersToShare(BufferedReader in) throws IOException {
        String data = "";
        String readPermissions;
        String writePermissions;
        do {
            System.out.println("\nEnter the email addresses of the people you want to add to the board  (0 to stop adding)");
            String email = in.readLine();
            if(!email.equals("0")) {
                System.out.println("\nRead permissions? (Y/N)"); //Permission
                do {
                    readPermissions = in.readLine();
                    if(!readPermissions.equals("Y") && !readPermissions.equals("N")) {
                        System.out.println("\nInvalid option. Please, try again:");
                    } else {
                        break;
                    }
                } while(true);
                System.out.println("\nWrite permissions? (Y/N)");
                do {
                    writePermissions = in.readLine();
                    if(!writePermissions.equals("Y") && !writePermissions.equals("N")) {
                        System.out.println("\nInvalid option. Please, try again:");
                    } else {
                        break;
                    }
                } while(true);
                data = data.concat(email + ";" + readPermissions + ";" + writePermissions + "\0"); //User info (add)
            } else {
                return data;
            }

        } while(true);
    }

    public static String showAndSelectBoard(SBPMessage responseMessage, BufferedReader in) throws IOException {
        String message = responseMessage.data();
        int currentIndex = 0;
        int currentBoardNumber = 1;
        char currentChar;
        String currentBoardName = "";
        List<String> boardsNames = new ArrayList<>();

        System.out.println("\nSelect a board:\n");

        while(currentIndex < (responseMessage.d_length_1() + responseMessage.d_length_2())) {
            while((currentChar = message.charAt(currentIndex)) != '\0') {
                currentBoardName = currentBoardName.concat(String.valueOf(currentChar));
                currentIndex++;
            }
            System.out.printf("%d - %s\n", currentBoardNumber, currentBoardName);  //Print Board information
            boardsNames.add(currentBoardName);
            currentBoardNumber++;
            currentIndex++;
            currentBoardName = "";
        }
        do {
            try {
                int option = Integer.parseInt(in.readLine());
                if(option < 0 || option > boardsNames.size()) {
                    throw new Exception();
                }
                return (boardsNames.get(option - 1));
            } catch(Exception e) {
                System.out.println("\nInvalid option. Please, try again:");
            }
        } while(true);
    }
}
