package boardService;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import static boardService.HTMLFileOpener.openFile;

public class GridGenerator {
    public static void generateHTMLGrid(int rows, int cols, String name) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n");
        html.append("<head>\n");
        html.append("<title>"+name+"</title>\n");
        html.append("<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js\"></script>\n");
        html.append("<script src=\"script.js\"></script>\n");
        html.append("<style>\n");
        html.append(".grid {\n");
        html.append("  display: grid;\n");
        html.append("  grid-template-columns: 150px repeat(" + cols + ", 1fr);\n");
        html.append("  grid-template-rows: 30px repeat(" + rows + ", " +(90/rows) +"vh);\n");
        html.append("  gap: 1px;\n");
        html.append("  padding: 10px;\n");
        html.append("}\n");
        html.append(".grid-item {\n");
        html.append("  background-color: lightblue;\n");
        html.append("  padding: 10px;\n");
        html.append("  color: white;\n");
        html.append("  font-size: 16px;");
        html.append("  text-align: center;\n");
        html.append("}\n");
        html.append(".grid-title {\n");
        html.append("  background-color: darkblue;\n");
        html.append("  border-style: solid;\n");
        html.append("  border-color: darkblue;\n");
        html.append("  padding: 10px;\n");
        html.append("  border-radius: 2px;\n");
        html.append("  color: white;\n");
        html.append("  font-size: 16px;");
        html.append("  font-weight: bold;");
        html.append("  text-align: center;\n");
        html.append("  vertical-align: middle;\n");
        html.append("}\n");

        html.append("</style>\n");
        html.append("</head>\n");
        html.append("<body>\n");
        html.append("<div class=\"grid\">\n");

        int id = 1;
        int rowId = 1;
        int colId = 1;
        for (int row = 0; row <= rows; row++) {
            for (int col = 0; col <= cols; col++) {
                if( (col == 0 && row == 0) ){
                    html.append("<div></div>");
                }
                else if( (row == 0 && col !=0) ){
                    html.append("<div id=\"grid-col-title" + rowId + "\" class=\"grid-title\">row title " + rowId + "</div>\n");
                    rowId++;
                }
                else if( (col == 0 && row != 0)){
                    html.append("<div id=\"grid-row-title" + colId + "\" class=\"grid-title\"> col title " + colId + "</div>\n");
                    colId++;
                }else{
                    html.append("<div id=\"grid-item-" + id + "\" class=\"grid-item\">cell id: " + id + "</div>\n");
                    id++;
                }

            }
        }

        html.append("</div>\n");
        html.append("</body>\n");
        html.append("</html>");
        String path = name.replaceAll("\\s","")+".html";
        // Write the HTML to a file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            writer.write(html.toString());
            System.out.println("\nHTML grid generated successfully.\n");
        } catch (IOException e) {
            System.out.println("Error generating HTML grid: " + e.getMessage());
        }
        openFile(path);
    }

}

