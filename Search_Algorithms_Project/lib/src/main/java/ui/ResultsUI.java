package ui;

import javax.swing.*;
import java.awt.*;

/**
 * A simple UI window for displaying results.
 * The results are displayed in a non-editable text area inside a scroll pane.
 */
public class ResultsUI extends JFrame {
    
    private static final long serialVersionUID = 1L;
    
    private JTextArea resultsArea;

    /**
     * Constructor to initialize the results window.
     * 
     * @param title The title of the window.
     */
    public ResultsUI(String title) {
        setTitle(title);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        initComponents();
        addComponentsToFrame();
    }

    /**
     * Initializes UI components.
     */
    private void initComponents() {
        resultsArea = new JTextArea();
        resultsArea.setEditable(false);
        resultsArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
    }

    /**
     * Adds components to the JFrame.
     */
    private void addComponentsToFrame() {
        JScrollPane scrollPane = new JScrollPane(resultsArea);
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Appends text to the results area.
     * 
     * @param text The text to append.
     */
    public void appendText(String text) {
        resultsArea.append(text + "\n");
    }
}
