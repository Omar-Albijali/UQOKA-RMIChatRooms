package com.oka;

import javax.swing.*;
import java.awt.*;

public class ChatWindow {

    private JFrame frame;
    private JTextArea textArea;

    // Constructor to initialize the window and text area
    public ChatWindow(String title) {
        frame = new JFrame(title);

        textArea = new JTextArea();
        textArea.setEditable(false);  // Make the text area non-editable
        textArea.setBackground(Color.BLACK);  // Set background color to black
        textArea.setForeground(Color.WHITE);  // Set text color to white
        textArea.setFont(new Font("Arial", Font.PLAIN, 16));  // Set font size and style

        // Set up the frame and add the text area
        frame.setSize(500, 600);
        frame.setAlwaysOnTop(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new JScrollPane(textArea));  // Add the text area to the frame with scroll
    }

    // Method to start the window
    public void start() {
        frame.setVisible(true);
    }

    // Method to close the window
    public void close() {
        frame.setVisible(false);
        frame.dispose();
    }

    // Method to print text on the window, appending it with a new line
    public void print(String text) {
        textArea.append(text + "\n");
    }

}
