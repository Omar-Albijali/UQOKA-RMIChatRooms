package com.oka;

import javax.swing.*;
import java.awt.*;

public class ChatWindow {

    private final JFrame frame;
    private final JTextArea textArea;

    public ChatWindow(String title) {
        frame = new JFrame(title);


        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setBackground(Color.BLACK);
        textArea.setForeground(Color.WHITE);
        textArea.setFont(new Font("Arial", Font.PLAIN, 16));


        frame.setSize(500, 600);
        frame.setAlwaysOnTop(true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(new JScrollPane(textArea));
    }


    public void start() {
        frame.setVisible(true);
    }

    public void close() {
        frame.setVisible(false);
        frame.dispose();
    }

    public void print(String text) {
        textArea.append(text + "\n");
    }

}
