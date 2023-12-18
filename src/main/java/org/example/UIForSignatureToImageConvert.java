package org.example;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class UIForSignatureToImageConvert {

    private static final File STAMP_MODEL_NO_SIGNATURE = new File("img/Black Creative Consultant Logo.jpg");
    private static final File STAMP_WITH_SIGNATURE = new File("img/Stamp_with_signature.jpg");
    private static final String RED_CONSOLE_COLOR = "\u001B[31m";
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

        String operation;


        isTrue = true;
        do {
            System.out.println("""
                    Do you want to specify the PDF path
                    Enter 1 or 'yes' - if yes
                    Enter 0 or 'no' - if no
                    """);
            operation = scanner.nextLine().toLowerCase().trim();
            switch (operation) {
                case "1", "yes" -> {
                    pdfPath = enterPDFPath();
                    isTrue = false;
                }
                case "0", "no" -> {
                    System.out.println("Your file will be saved in - 'signatureToPDF/pdfs'\n");
                    isTrue = false;
                }
                default -> System.out.println("wrong operator");
            }
        } while (isTrue);

    }

    private static String enterPDFPath() {
        Scanner scanner = new Scanner(System.in);
        String pdfPath = null;

        boolean secondTurn = false;
        do {
            if (secondTurn) {
                System.out.println(RED_CONSOLE_COLOR + " --- WARNING!!! '" + pdfPath + "' is not valid path ---" + RESET_CONSOLE_COLOR);
            }
            System.out.println("please enter valid PDF file absolute path");
            pdfPath = scanner.nextLine();
            secondTurn = true;
        } while (!Files.exists(Path.of(pdfPath)));
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
            BufferedImage image = ImageIO.read(STAMP_MODEL_NO_SIGNATURE);

            // Create a Graphics2D object to draw on the image
            Graphics2D g2d = image.createGraphics();

            // Set font properties
            Font font = new Font("Freestyle Script", Font.ITALIC, 100);
            g2d.setFont(font);

            // Set text color
            g2d.setColor(Color.RED);

            // Set the position where the text will be drawn
            final int x = 195;
            final int y = 270;

            // Add your text to the image
            g2d.drawString(signature, x, y);

            // Dispose of the Graphics2D object
            g2d.dispose();

            // Save the modified image
            ImageIO.write(image, "jpg", STAMP_WITH_SIGNATURE);

            System.out.println("Text added to the image successfully.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
