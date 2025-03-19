package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import executors.GraphExecutor;
import executors.TreeExecutor;

public class MainUI extends JFrame {
    private JComboBox<String> structureChoice;
    private JComboBox<String> nodeChoice;
    private JComboBox<String> executionChoice;
    private JTextArea outputArea;
    private JButton runButton;

    private static final String filePathGraph10000 = "src/main/resources/data_input/graph10000.txt";
    private static final String filePathGraph1000 = "src/main/resources/data_input/graph1000.txt";
    private static final String filePathTree10000 = "src/main/resources/data_input/tree10000.txt";
    private static final String filePathTree1000 = "src/main/resources/data_input/tree1000.txt";

    public MainUI() {
        setTitle("Graph & Tree Executor");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(3, 2, 10, 10));
        
        structureChoice = new JComboBox<>(new String[]{"Graph", "Tree"});
        nodeChoice = new JComboBox<>(new String[]{"1000", "10000"});
        executionChoice = new JComboBox<>(new String[]{"Sequential", "Parallel"});
        runButton = new JButton("Run");
        runButton.setBackground(new Color(70, 130, 180));
        runButton.setForeground(Color.WHITE);
        runButton.setFont(new Font("Arial", Font.BOLD, 14));
        
        topPanel.add(new JLabel("Select Data Structure:"));
        topPanel.add(structureChoice);
        topPanel.add(new JLabel("Select Number of Nodes:"));
        topPanel.add(nodeChoice);
        topPanel.add(new JLabel("Select Execution Type:"));
        topPanel.add(executionChoice);

        outputArea = new JTextArea(10, 40);
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        outputArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JScrollPane scrollPane = new JScrollPane(outputArea);

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(runButton);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                executeAlgorithm();
            }
        });
    }

    private void executeAlgorithm() {
        String structure = (String) structureChoice.getSelectedItem();
        int nodes = Integer.parseInt((String) nodeChoice.getSelectedItem());
        String execution = (String) executionChoice.getSelectedItem();

        String fileName = (structure.equals("Graph"))
                ? (nodes == 1000 ? filePathGraph1000 : filePathGraph10000)
                : (nodes == 1000 ? filePathTree1000 : filePathTree10000);

        if (structure.equals("Graph")) {
            if (execution.equals("Sequential")) {
                GraphExecutor.runSequentialMethods(fileName, 0, nodes);
                outputArea.setText("Graph execution (Sequential) completed.");
            } else {
                try {
                    GraphExecutor.runParallelMethods(fileName, 0);
                    outputArea.setText("Graph execution (Parallel) completed.");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    outputArea.setText("Error in parallel execution.");
                }
            }
        } else {
            if (execution.equals("Sequential")) {
                TreeExecutor.runTreeMethods(fileName, new java.util.Scanner(System.in), nodes);
                outputArea.setText("Tree execution (Sequential) completed.");
            } else {
                outputArea.setText("Parallel execution is not available for trees.");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
        	MainUI frame = new MainUI();
            frame.setVisible(true);
        });
    }
}