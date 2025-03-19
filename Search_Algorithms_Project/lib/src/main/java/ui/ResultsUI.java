package ui;

import javax.swing.*;
import java.awt.*;

public class ResultsUI extends JFrame {
    private JTextArea resultsArea;

    public ResultsUI(String title) {
        setTitle(title);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        resultsArea = new JTextArea();
        resultsArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultsArea);
        
        add(scrollPane, BorderLayout.CENTER);
    }

    public void appendText(String text) {
        resultsArea.append(text + "\n");
    }
}
