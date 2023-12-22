package org.example;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;

public class UIForSignatureToImageConvert {

    private static final File IMG_STAMP_MODEL_NO_SIGNATURE = new File(Objects.requireNonNull(UIForSignatureToImageConvert.class.getClassLoader().getResource("img/Black Creative Consultant Logo.jpg")).getPath());
    private static final File IMG_STAMP_WITH_SIGNATURE = new File(Objects.requireNonNull(UIForSignatureToImageConvert.class.getClassLoader().getResource("img/Stamp_with_signature.jpg")).getPath());
    private static final String RED_CONSOLE_COLOR = "\u001B[31m";
    private static final String YELLOW_CONSOLE_COLOR = "\u001B[33m";
    private static final String RESET_CONSOLE_COLOR = "\u001B[0m";


    //Start-point of the Console Interface
    public static void start() {
        Scanner scanner = new Scanner(System.in);
        String sign;
        String pdfPath;

        boolean isTrue = false;
        do {
            if (isTrue) {
                System.out.println("Please enter you signature " + RED_CONSOLE_COLOR + "(NO MORE THAN 3 SYMBOLS)" + RESET_CONSOLE_COLOR);
            } else {
                System.out.println("Please enter you signature (no more than 3 symbols)");
            }
            sign = scanner.nextLine();
            isTrue = true;
        } while (sign.length() > 3 || sign.isEmpty());


        addSignatureToStamp(sign);

        imageToPDF(new File(enterPDFPath()));
    }

    private static String enterPDFPath() {
        Scanner scanner = new Scanner(System.in);
        String pdfPath = null;
        Path path = null;
        Optional<Path> optionalPath = Optional.ofNullable(path);
        boolean secondTurn = false;
        do {
            if (secondTurn) {
                //if first time the path was incorrect the console will show WARNING
                System.out.println(RED_CONSOLE_COLOR + " --- WARNING!!! '" + pdfPath + "' is not valid path ---" + RESET_CONSOLE_COLOR);
            }
            System.out.println("Please enter valid PDF file absolute path");
            pdfPath = scanner.nextLine();

            try {
                optionalPath = Optional.ofNullable(Path.of(pdfPath));
            } catch (InvalidPathException e) {
                System.out.println(RED_CONSOLE_COLOR + "----WARNING--- " + e.getMessage() + RESET_CONSOLE_COLOR);
                continue;
            }
            secondTurn = true;
        } while (!Files.exists(optionalPath.orElse(Path.of("PATH/THAT/DONT/EXIST"))));

        return pdfPath;

    }

    private static void addSignatureToStamp(String signature) {
        StringBuilder sb = new StringBuilder(signature);
        for (int i = 0; i < sb.length(); i++) {
            if (i % 2 == 0) {
                sb.insert((i + 1), '.');
            }
        }
        signature = sb.toString();
        try {
            // Load an existing image
            BufferedImage image = ImageIO.read(IMG_STAMP_MODEL_NO_SIGNATURE);

            // Create a Graphics2D object to draw on the image
            Graphics2D g2d = image.createGraphics();

            // Set font properties
            Font font = new Font("Freestyle Script", Font.ITALIC, 100);
            g2d.setFont(font);

            // Set text color
            g2d.setColor(Color.RED);

            // Set the position where the text will be drawn
            int x;
            if (signature.length() == 6) {
                x = 155;
            } else if (signature.length() == 4) {
                x = 190;
            } else {
                x = 220;
            }
            final int y = 270;

            // Add your text to the image
            g2d.drawString(signature.toUpperCase(), x, y);

            // Dispose of the Graphics2D object
            g2d.dispose();

            // Save the modified image
            ImageIO.write(image, "jpg", IMG_STAMP_WITH_SIGNATURE);

            System.out.println(YELLOW_CONSOLE_COLOR + "Text added to the image successfully." + RESET_CONSOLE_COLOR);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void imageToPDF(final File PDF_FILE) {
        // Load existing PDF document
        try (PDDocument document = Loader.loadPDF(PDF_FILE)) {

            // Create a new page
            PDPage page = document.getPage(0);

//            document.addPage(page);

            // Load image
            PDImageXObject image = PDImageXObject.createFromFile(IMG_STAMP_WITH_SIGNATURE.getPath(), document);

            System.out.println("is e=image empty - " + image.isEmpty());

            // Create content stream
            PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.PREPEND,
                    false, true);

            // Define the position and size of the image on the page
            float x = 500;
            float y = 0;
            float width = 100;
            float height = 100;

            // Draw the image on the page
            contentStream.drawImage(image, x, y, width, height);

            // Close the content stream
            contentStream.close();

            // Save the modified document
            document.save(PDF_FILE);

            // Close the document
            document.close();

            System.out.println("Image added to PDF successfully.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
