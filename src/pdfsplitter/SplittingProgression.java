package pdfsplitter;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.swing.*;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;

public class SplittingProgression {

    private boolean leftToBeFirst = true;

    boolean overwrite;

    Scene scene;

    String original_filename, filenameSplitted = ""; //имя оригинального файла

    FileOutputStream writer;

    PdfReader reader;

    Document sourceDocument;

    File file;

    PdfCopy pdfCopy;

    static Pane root;

    static Stage myStage;



    public void process(String originalfilename, boolean overwrite) {

        //переменные
        PdfImportedPage importedPage;

        PdfDictionary pageDict;

        boolean existCoverpage = false, existSplited = false;

        PdfRectangle[] rect;

        String tempFileName;

        boolean rightToBeFirst = false;

        //this.original_filename = originalfilename;

        int n;

       try {

            original_filename = originalfilename.replaceAll("XXX"," ");

          // JOptionPane.showMessageDialog(null, original_filename);
            myStage = new Stage();
            myStage.setResizable(false);
            myStage.initStyle(StageStyle.UNDECORATED);
            myStage.initModality(Modality.APPLICATION_MODAL);

            reader = new PdfReader(original_filename);

            rect = getRectangles(reader);

            n = reader.getNumberOfPages();


                //проверяется формат страниц исходного файла
            if ((reader.getNumberOfPages()==1) || (reader.getPageSize(1).getWidth() * 1.5 > reader.getPageSize(2).getWidth()))
                sourceDocument = new Document(new Rectangle(reader.getPageSizeWithRotation(1).getWidth() / 2,
                        reader.getPageSizeWithRotation(1).getHeight()));
            else {
                sourceDocument = new Document(reader.getPageSizeWithRotation(1));
                existCoverpage = true;
            }

            //если надо создать новый
            if (!overwrite) {
                filenameSplitted = original_filename.substring(0, original_filename.length() - 4) + "_splitted.pdf";
                file = new File(filenameSplitted);

                //если выходной файл уже существует
                if (file.exists()) {

                    if (showOverwiteDialog(filenameSplitted)) {
                        tempFileName = String.valueOf(Math.random());
                        existSplited = true;
                        file = File.createTempFile(tempFileName, ".pdf");
                        overwrite = true;
                    } else quitMethod();
                }

            }

            //создать временный файл
            else {
                tempFileName = String.valueOf(Math.random());
                file = File.createTempFile(tempFileName, ".pdf");
            }


            if (!existCoverpage)
                if (!showSplitWindow(original_filename)) rightToBeFirst = true;

            writer = new FileOutputStream(file);

            pdfCopy = new PdfCopy(sourceDocument, writer);

            sourceDocument.open();


            for (int i = 1; i <= n; i++) {

                pageDict = reader.getPageN(i);
                pageDict.put(PdfName.MEDIABOX, rect[0]);

                //проверка условия что файл ландскейп и правая часть должна быть первой
                if ((i == 1) && (!existCoverpage) && (rightToBeFirst))
                    pageDict.put(PdfName.MEDIABOX, rect[1]);

                importedPage = pdfCopy.getImportedPage(reader, i);
                //importedPage.setWidth(reader.getPageSize(1).getWidth() / 2);
                pdfCopy.addPage(importedPage);

                if (((existCoverpage) & i == 1) || ((existCoverpage) & i == n)) continue;

                //если правая должна быть первой, то игнорировать повторное копирование правой
                if ((!existCoverpage) && (i == 1)) continue;

                pageDict = reader.getPageN(i);
                pageDict.put(PdfName.MEDIABOX, rect[1]);
                importedPage = pdfCopy.getImportedPage(reader, i);
                pdfCopy.addPage(importedPage);
            }

            //добавление последнего элемента в случае ландскейп и когда правая должна быть первой
            if (!existCoverpage) {
                pageDict = reader.getPageN(1);

                if (rightToBeFirst) pageDict.put(PdfName.MEDIABOX, rect[0]);
                else pageDict.put(PdfName.MEDIABOX, rect[1]);
                importedPage = pdfCopy.getImportedPage(reader, 1);
                //importedPage.setWidth(reader.getPageSize(1).getWidth() / 2);
                pdfCopy.addPage(importedPage);
            }


        }

        // Блоки catche-ов
        catch (NullPointerException ex) {
            showDialogWindow(
                    "Unknown error occurred while reading file. Program will be closed. Contact Karen for resolving.",
                    original_filename);
            quitMethod();
        } catch (DocumentException error) {
            showDialogWindow(
                    "Unknown error occurred while tying create PDF document. Program will be closed. Contact Karen for resolving.",
                    original_filename);
            quitMethod();
        } catch (FileNotFoundException error) {
            showDialogWindow(
                    "Unknown error occurred while tying create splitted file. Program will be closed. Contact Karen for resolving.",
                    writer.toString());
            quitMethod();
        } catch (IOException error) {
            showDialogWindow(
                    "Unknown error occurred while reading file. Program will be closed. Contact Karen for resolving.",
                    original_filename);
            quitMethod();
        } finally

        {
            try {
                if (sourceDocument != null) sourceDocument.close();
                if (writer != null) writer.close();
                if (reader != null) reader.close();
                if (pdfCopy != null) pdfCopy.close();

                sourceDocument = null;
                writer = null;
                reader = null;
                pdfCopy = null;
                System.gc();
            } catch (IOException ex) {

                showDialogWindow (
                        "Error while closing streams.",
                        original_filename);
                System.gc();
            }
        }


        //overwrite existing file
        if (overwrite)

        {
            int result = overwriteMeth(existSplited);
            if (result == 1) quitMethod();
        }

        showDialogWindow(
                "File was successfully splitted",
                original_filename);
        quitMethod();
    }

    void quitMethod() {

        try {
            if (sourceDocument != null) sourceDocument.close();
            if (writer != null) writer.close();
            if (reader != null) reader.close();
            if (pdfCopy != null) pdfCopy.close();

            sourceDocument = null;
            writer = null;
            reader = null;
            pdfCopy = null;
            System.gc();
            Platform.exit();
            System.exit(0);

        } catch (IOException ex) {

            showDialogWindow(
                    "Error while closing streams.",
                    original_filename);
            System.gc();
            System.exit(0);
        }

    }

    int overwriteMeth(boolean exSpl) {


        Path FROM = Paths.get(file.getAbsolutePath());
        Path TO;
        File to = new File(filenameSplitted);

        if (exSpl)
            TO = Paths.get(to.getAbsolutePath());
        else TO = Paths.get(new File(original_filename).getAbsolutePath());
        CopyOption[] options = new CopyOption[]{
                StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.COPY_ATTRIBUTES
        };
        File file = new File(TO.toString());
        file.setWritable(true);

        try {
            Files.delete(TO);
            Files.copy(FROM, TO, options);
        } catch (IOException ex) {

            showDialogWindow(
                    "File is opened in another program. Close it and try again",
                    original_filename);
            return 1;
        }

        try {
            Files.delete(FROM);
        } catch (IOException ex) {
            showDialogWindow(
                    "Error while deleting temporary file. Contact Karen for support",
                    original_filename);
            return 0;
        }

        return 0;
    }

    public PdfRectangle[] getRectangles(PdfReader pdfReader) throws NullPointerException {

        if (pdfReader != null) {

            PdfRectangle[] rectangleArray = new PdfRectangle[2];

            if (pdfReader.getNumberOfPages()==1)
            {
                rectangleArray[0] = new PdfRectangle(pdfReader.getPageSize(1).getWidth() / 2,
                        pdfReader.getPageSize(1).getHeight());

                rectangleArray[1] = new PdfRectangle((pdfReader.getPageSize(1).getWidth() / 2), 0, pdfReader.getPageSize(1).getWidth(),
                        pdfReader.getPageSize(1).getHeight());
            }


                else
            {
                rectangleArray[0] = new PdfRectangle(pdfReader.getPageSize(2).getWidth() / 2,
                        pdfReader.getPageSize(2).getHeight());

                rectangleArray[1] = new PdfRectangle((pdfReader.getPageSize(2).getWidth() / 2), 0, pdfReader.getPageSize(2).getWidth(),
                        pdfReader.getPageSize(1).getHeight());
            }
            return rectangleArray;

        }
        return null;
    }

    public  boolean showSplitWindow (String filename) throws IOException{

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("SplitWindow.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);


            SplitWindowController controller = loader.getController();
            String name = filename.substring(filename.lastIndexOf('\\')+1);

            String labeltext = controller.SplitWindowLabel.getText().substring(0,9) +
                    name + " " + controller.SplitWindowLabel.getText().substring(9);

            controller.SplitWindowLabel.setText(labeltext);

            double labelheight = controller.SplitWindowLabel.getPrefHeight();

            Text theText = new Text(labeltext);

            theText.setFont(controller.SplitWindowLabel.getFont());

            double width = theText.getBoundsInLocal().getWidth();

            controller.SplitWindowLabel.wrapTextProperty().setValue(Boolean.TRUE);

            if (width > scene.getWidth())

                controller.SplitWindowLabel.setMinHeight(labelheight*2);


            myStage.setScene(scene);
            myStage.showAndWait();

            if (controller.quit)
                quitMethod();

            return controller.leftToBeFirst;

    }


    public void showDialogWindow (String text, String title) {

        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("DialogWindow.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);


            DialogWindowController controller = loader.getController();
            String name = title.substring(title.lastIndexOf('\\') + 1);

            controller.TittleLabel.setText(name);
            //Font font = new Font(controller.TittleLabel.getFont(),controller.TittleLabel.getFont().getSize());

            Text theText = new Text(text);
            theText.setFont(controller.TittleLabel.getFont());
            double width = theText.getBoundsInLocal().getWidth();
            controller.TittleLabel.wrapTextProperty().setValue(Boolean.TRUE);


            controller.MessageTextLabel.setText(text);
            double labelheight = controller.MessageTextLabel.getPrefHeight();
            if (width > scene.getWidth())

                controller.MessageTextLabel.setMinHeight(labelheight * 2);

            myStage.setScene(scene);
            myStage.showAndWait();

            quitMethod();

        }catch (IOException ex){}
     }



    public boolean showOverwiteDialog (String filename) throws IOException{

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("OverwritingWindow.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);


            OverwritingWindowController controller = loader.getController();
            String name = filename.substring(filename.lastIndexOf('\\')+1);

            String labeltext = controller.OverwriteWindLabel.getText().substring(0,10)
                    +name+controller.OverwriteWindLabel.getText().substring(10);

            controller.OverwriteWindLabel.setText(labeltext);

            Text theText = new Text(labeltext);
            theText.setFont(controller.OverwriteWindLabel.getFont());
            double width = theText.getBoundsInLocal().getWidth();
            controller.OverwriteWindLabel.wrapTextProperty().setValue(Boolean.TRUE);

            double labelheight = controller.OverwriteWindLabel.getPrefHeight();
            if (width > scene.getWidth())
                controller.OverwriteWindLabel.setMinHeight(labelheight*2);

            myStage.setScene(scene);
            myStage.showAndWait();

            return controller.contin;
        }

    }

