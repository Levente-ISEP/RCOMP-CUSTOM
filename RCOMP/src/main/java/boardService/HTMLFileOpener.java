package boardService;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class HTMLFileOpener {
    public static void openFile(String path) {
        File htmlFile = new File(path);
        if (htmlFile.exists()) {
            try {
                Desktop.getDesktop().open(htmlFile);
            } catch (IOException e) {
                System.out.println("Error opening HTML file: " + e.getMessage());
            }
        } else {
            System.out.println("HTML file does not exist.");
        }
    }
}
