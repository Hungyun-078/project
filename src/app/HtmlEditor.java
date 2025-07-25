/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

/**
 *
 * @author sam20
 */
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HtmlEditor {

    public static void edit(String html) {
        // Path to the HTML file
        String htmlFilePath = html;
        String userID = SQL.getUserID(); // Example UserID

        try {
            // Read the HTML file
            Path path = Paths.get(htmlFilePath);
            String htmlContent = new String(Files.readAllBytes(path));

            // Check if UserID declaration already exists
            if (htmlContent.contains("let UserID = '" + userID + "';")) {
                System.out.println("UserID already exists in HTML file. No need to insert again.");
                return; // Exit the method if UserID already exists
            }

            // Find the position to insert the UserID
            int index = htmlContent.indexOf("let docksData = [];") + "let docksData = [];".length();
            if (index != -1) {
                // Insert the UserID declaration
                String modifiedHtmlContent = htmlContent.substring(0, index) +
                        "\nlet UserID = '" + userID + "';\n" +
                        htmlContent.substring(index);

                // Write the modified HTML content back to the file
                Files.write(path, modifiedHtmlContent.getBytes());

                System.out.println("UserID inserted into HTML file successfully.");
            } else {
                System.out.println("Position to insert UserID not found in HTML file.");
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to insert UserID into HTML file: " + e.getMessage());
        }
    }
    public static void remove(String html) {
        // Path to the HTML file
        String htmlFilePath = html;

        try {
            // Read the HTML file
            Path path = Paths.get(htmlFilePath);
            String htmlContent = new String(Files.readAllBytes(path));

            // Remove all occurrences of the UserID declaration and the following blank line
            while (htmlContent.contains("let UserID = '")) {
                int startIndex = htmlContent.indexOf("let UserID = '");
                int endIndex = htmlContent.indexOf("';", startIndex);
                if (startIndex != -1 && endIndex != -1) {
                    // Find the end of the line after the UserID declaration
                    int nextLineIndex = htmlContent.indexOf("\n", endIndex);
                    if (nextLineIndex != -1) {
                        // Remove the UserID line and the following blank line
                        int nextNonEmptyLineIndex = htmlContent.indexOf("\n", nextLineIndex + 1);
                        if (nextNonEmptyLineIndex != -1) {
                            htmlContent = htmlContent.substring(0, startIndex) +
                                    htmlContent.substring(nextNonEmptyLineIndex + 1);
                        } else {
                            // If no following non-empty line found, remove only the UserID line
                            htmlContent = htmlContent.substring(0, startIndex);
                        }
                    } else {
                        // If no following line found, remove only the UserID line
                        htmlContent = htmlContent.substring(0, startIndex);
                    }
                }
            }

            // Write the modified HTML content back to the file
            Files.write(path, htmlContent.getBytes());

            System.out.println("UserID removed from HTML file successfully.");

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to remove UserID from HTML file: " + e.getMessage());
        }
    }
}
