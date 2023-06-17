package client.ui;

import java.io.File;
import java.util.Scanner;

import static boardService.GridGenerator.generateHTMLGrid;
public class GenerateHTMLUI {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the number of rows: ");
        int rows = scanner.nextInt();

        System.out.print("Enter the number of columns: ");
        int cols = scanner.nextInt();

        System.out.print("Enter the name of board: ");
        scanner.nextLine();
        String name = scanner.nextLine();
        File f = new File(name.replaceAll("\\s","")+".html");
        while(f.exists()){
            System.out.println("Board with name: "+name+" exists. Try a new one:");
            name = scanner.nextLine();
            f = new File(name.replaceAll("\\s","")+".html");
        }
        scanner.close();
        generateHTMLGrid(rows, cols, name);
    }
}
