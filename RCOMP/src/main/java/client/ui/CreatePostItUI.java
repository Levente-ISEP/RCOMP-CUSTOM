package client.ui;

import java.io.*;
import java.util.Arrays;
import java.util.List;

import static postitService.uploadImage.uploadImage;
import static postitService.uploadText.uploadText;

public class CreatePostItUI implements Runnable {

    static final int ACK_CODE = 2;
    static final int ERR_CODE = 3;

    BufferedReader in;
    DataInputStream sIn;
    DataOutputStream sOut;

    public CreatePostItUI(BufferedReader in, DataOutputStream sOut, DataInputStream sIn) {
        this.in = in;
        this.sIn = sIn;
        this.sOut = sOut;
    }

    @Override
    public void run() {
        try {
            String board = showAndSelectBoard(in);
            UploadContent(in, board);
        } catch (Exception e) {
            System.out.println("Error" + e.getMessage());
        }

    }
    public static String selectCell(BufferedReader in) throws IOException {
        String data = "";
        String readPermissions;
        String writePermissions;
        do {
            System.out.println("\nEnter the row number: ");
            String row = in.readLine();

            System.out.println("\nEnter the column number: ");
            String column = in.readLine();

            data = data.concat(row + ";" + column +"\0");

            return data;

        } while(true);
    }

    public static void UploadContent(BufferedReader in, String board) throws Exception{
        System.out.println("Select content type: \n1. Text\n2. Image");
        int option = in.read();
        if(option>2){
            System.out.println("\nInvalid option! Try again:");
            System.out.println("Select content type: \n1. Text\n2. Image");
            option = in.read();

            switch (option){
                case 1:
                    uploadText();
                    break;
                case 2:
                    uploadImage();
                    break;
                default:
                    break;
            }
        }


    }
    public static String showAndSelectBoard(BufferedReader in) throws Exception{
        File dir = new File(".");
        List<String> list = Arrays.asList(dir.list(
                new FilenameFilter() {
                    @Override public boolean accept(File dir, String name) {
                        return name.endsWith(".html");
                    }
                }
        ));

        System.out.println("\nSelect a board by index:");

        int idx = 1;
        for (String s:
             list) {
            System.out.println(idx+". "+s);
            idx++;
        }
        int option = Integer.parseInt(in.readLine());
        if(option>list.size()){
            System.out.println("\nInvalid option! Try again:");
            System.out.println("Select board by number: ");
            option = Integer.parseInt(in.readLine());
        }
        return list.get(option-1);
    }
}
