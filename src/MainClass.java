import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import pdfsplitter.SplittingProgression;

import javax.swing.*;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class MainClass extends Application {

    static String[] argum;

    public static void main(String[] args) throws IOException {

        argum = args;
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Class c = MainClass.class;

        String dirPath=c.getResource(c.getName() + ".class").toString();

        dirPath=dirPath.replaceAll("%20", " ");

        dirPath=dirPath.substring(10,dirPath.indexOf("PDF Splitter")+12);

        Path filepath = Paths.get(dirPath+"//namepass.txt");

         // JOptionPane.showMessageDialog(null, filepath.toAbsolutePath());
        //  String      dirPath= "C:\\Program Files\\PDF Splitter";

        BufferedReader br = null;

        try {


            br = new BufferedReader(new FileReader( dirPath+"//namepass.txt"));

            byte[] bytes = new byte[500];

            String str;

            boolean overwrite = false;

            int i = 0;

            while ((str=br.readLine())!=null) {

                if (str.equals("AAA"))

                {
                    overwrite = true;

                    break;
                }

                bytes[i]=(Byte.parseByte(str));

                i++;

            }

            if (br != null)

                br.close();

                Files.delete(filepath);

            String finalFilename = new String(bytes,"UTF-8");

            finalFilename = finalFilename.trim();

            if (overwrite)   new SplittingProgression().process(finalFilename, true);

            else  new SplittingProgression().process(finalFilename, false);

        }
             catch (IOException e) {

                 JOptionPane.showMessageDialog(null, "An error occurred while passing file name. " +
                         "Program will be closed. Contact Karen for support");

             }

        finally {

            try {
                if (br != null) {

                    br.close();

                    Files.delete(filepath);
                }

            } catch (IOException ex) {}



            System.exit(0);

        }

        Platform.exit();
    }
}







