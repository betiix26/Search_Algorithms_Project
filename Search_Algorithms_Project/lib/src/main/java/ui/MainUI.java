package ui;

import javax.swing.*;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;

import executors.GraphExecutor;
import executors.TreeExecutor;
import utils.SearchMetrics;
import utils.SearchResult;

/**
 * Main UI class for executing graph and tree algorithms. Provides a graphical
 * interface using Swing components.
 */
public class MainUI extends JFrame {

	private static final long serialVersionUID = 1L;

	// UI Components
	private JComboBox<String> structureChoice;
	private JComboBox<String> nodeChoice;
	private JComboBox<String> executionChoice;
	private JTextArea outputArea;
	private JButton runButton;

	// File paths for different datasets
	private static final String FILE_PATH_GRAPH_10000 = "src/main/resources/data_input/graph_10000.txt";
	private static final String FILE_PATH_TREE_10000 = "src/main/resources/data_input/tree_10000.txt";
	private static final String FILE_PATH_GRAPH_1000 = "src/main/resources/data_input/graph_1000.txt";
	private static final String FILE_PATH_TREE_1000 = "src/main/resources/data_input/tree_1000.txt";

	/**
	 * Constructor: Initializes the UI components and layout.
	 */
	public MainUI() {
		setTitle("Graph & Tree Executor");
		setSize(500, 400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		setLocationRelativeTo(null);

		initComponents();
		addComponentsToFrame();
		attachEventHandlers();
	}

	/**
	 * Initializes the UI components.
	 */
	private void initComponents() {
		structureChoice = new JComboBox<>(new String[] { "Graph", "Tree" });
		nodeChoice = new JComboBox<>(new String[] { "1000", "10000" });
		executionChoice = new JComboBox<>(new String[] { "Sequential", "Parallel" });
		runButton = new JButton("Run");

		// Customize button appearance
		runButton.setBackground(new Color(70, 130, 180));
		runButton.setForeground(Color.WHITE);
		runButton.setFont(new Font("Arial", Font.BOLD, 14));

		outputArea = new JTextArea(10, 40);
		outputArea.setEditable(false);
		outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
		outputArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
	}

	/**
	 * Adds UI components to the frame.
	 */
	private void addComponentsToFrame() {
		// Panel for selection options
		JPanel topPanel = new JPanel(new GridLayout(3, 2, 10, 10));
		topPanel.add(new JLabel("Select Data Structure:"));
		topPanel.add(structureChoice);
		topPanel.add(new JLabel("Select Number of Nodes:"));
		topPanel.add(nodeChoice);
		topPanel.add(new JLabel("Select Execution Type:"));
		topPanel.add(executionChoice);

		// Scroll pane for output area
		JScrollPane scrollPane = new JScrollPane(outputArea);

		// Panel for run button
		JPanel bottomPanel = new JPanel();
		bottomPanel.add(runButton);

		// Add panels to the main frame
		add(topPanel, BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);
		add(bottomPanel, BorderLayout.SOUTH);
	}

	/**
	 * Attaches event listeners to UI components.
	 */
	private void attachEventHandlers() {
		runButton.addActionListener(e -> executeAlgorithm());
	}

	/**
	 * Executes the selected algorithm based on user input.
	 */
	private void executeAlgorithm() {
		String structure = (String) structureChoice.getSelectedItem();
		int nodes = Integer.parseInt((String) nodeChoice.getSelectedItem());
		String execution = (String) executionChoice.getSelectedItem();

		String fileName = getFileName(structure, nodes);

		if ("Sequential".equals(execution)) {
			executeSequential(structure, fileName, nodes);
		} else {
			executeParallel(structure, fileName);
		}
	}

	/**
	 * Returns the corresponding file path based on user selection.
	 *
	 * @param structure The selected data structure (Graph/Tree).
	 * @param nodes     The number of nodes (1000/10000).
	 * @return The file path for the selected dataset.
	 */
	private String getFileName(String structure, int nodes) {
		if ("Graph".equals(structure)) {
			return (nodes == 1000) ? FILE_PATH_GRAPH_1000 : FILE_PATH_GRAPH_10000;
		} else {
			return (nodes == 1000) ? FILE_PATH_TREE_1000 : FILE_PATH_TREE_10000;
		}
	}

	/**
	 * Executes the selected algorithm sequentially.
	 *
	 * @param structure The selected data structure (Graph/Tree).
	 * @param fileName  The file path for input data.
	 * @param nodes     The number of nodes.
	 */
	/**
	 * Executes the selected algorithm sequentially.
	 *
	 * @param structure The selected data structure (Graph/Tree).
	 * @param fileName  The file path for input data.
	 * @param nodes     The number of nodes.
	 */
	private void executeSequential(String structure, String fileName, int nodes) {
	    if ("Graph".equals(structure)) {
	        try {
	            SearchResult[] results = GraphExecutor.runSequentialMethodsWithMetrics(fileName, 0);
	            
	            StringBuilder sb = new StringBuilder();
	            sb.append("Graph execution (Sequential) completed.\n\n");
	            
	            for (SearchResult result : results) {
	                SearchMetrics metrics = result.getMetrics();
	                sb.append(metrics.getAlgorithmType()).append(" Results:\n")
	                  .append("Total Time: ").append(metrics.getTotalTime() / 1_000_000).append(" ms\n")
	                  .append("Computation Time: ").append(metrics.getComputationTime() / 1_000_000).append(" ms\n")
	                  .append("Memory Used: ").append(metrics.getMemoryUsage()).append(" bytes\n")
	                  .append("Nodes Processed: ").append(metrics.getNodesProcessed()).append("\n\n");
	            }
	            
	            outputArea.setText(sb.toString());
	            saveSequentialResultsToExcel(results, nodes);
	            
	        } catch (Exception ex) {
	            ex.printStackTrace();
	            outputArea.setText("Error in sequential execution: " + ex.getMessage());
	        }
	    } else {
	        try {
	            SearchResult[] results = TreeExecutor.runTreeMethodsWithMetrics(fileName);
	            
	            StringBuilder sb = new StringBuilder();
	            sb.append("Tree execution (Sequential) completed.\n\n");
	            
	            for (SearchResult result : results) {
	                SearchMetrics metrics = result.getMetrics();
	                sb.append(metrics.getAlgorithmType()).append(" Results:\n")
	                  .append("Total Time: ").append(metrics.getTotalTime() / 1_000_000).append(" ms\n")
	                  .append("Computation Time: ").append(metrics.getComputationTime() / 1_000_000).append(" ms\n")
	                  .append("Memory Used: ").append(metrics.getMemoryUsage()).append(" bytes\n")
	                  .append("Nodes Processed: ").append(metrics.getNodesProcessed()).append("\n\n");
	            }
	            
	            outputArea.setText(sb.toString());
	            saveSequentialResultsToExcel(results, nodes);
	            
	        } catch (Exception ex) {
	            ex.printStackTrace();
	            outputArea.setText("Error in tree execution: " + ex.getMessage());
	        }
	    }
	}

	private void saveSequentialResultsToExcel(SearchResult[] results, int nodeCount) {
	    try (XSSFWorkbook workbook = new XSSFWorkbook()) {
	        XSSFSheet sheet = workbook.createSheet("SequentialResults");
	        
	        // Create header
	        XSSFRow headerRow = sheet.createRow(0);
	        headerRow.createCell(0).setCellValue("Algorithm");
	        headerRow.createCell(1).setCellValue("Total Time (ms)");
	        headerRow.createCell(2).setCellValue("Computation Time (ms)");
	        headerRow.createCell(3).setCellValue("Memory Usage (bytes)");
	        headerRow.createCell(4).setCellValue("Nodes Processed");
	        headerRow.createCell(5).setCellValue("Node Count");
	        
	        // Add data
	        for (int i = 0; i < results.length; i++) {
	            SearchMetrics metrics = results[i].getMetrics();
	            XSSFRow dataRow = sheet.createRow(i + 1);
	            dataRow.createCell(0).setCellValue(metrics.getAlgorithmType());
	            dataRow.createCell(1).setCellValue(metrics.getTotalTime() / 1_000_000);
	            dataRow.createCell(2).setCellValue(metrics.getComputationTime() / 1_000_000);
	            dataRow.createCell(3).setCellValue(metrics.getMemoryUsage());
	            dataRow.createCell(4).setCellValue(metrics.getNodesProcessed());
	            dataRow.createCell(5).setCellValue(nodeCount);
	        }
	        
	        // Save file
	        try (FileOutputStream fileOut = new FileOutputStream("SequentialResults.xlsx")) {
	            workbook.write(fileOut);
	            outputArea.append("\nResults saved to SequentialResults.xlsx");
	        }
	        
	    } catch (IOException e) {
	        e.printStackTrace();
	        outputArea.append("\nError saving sequential results to Excel.");
	    }
	}

	/**
	 * Executes the selected algorithm in parallel (only for Graphs).
	 *
	 * @param structure The selected data structure (Graph/Tree).
	 * @param fileName  The file path for input data.
	 */
	private void executeParallel(String structure, String fileName) {
	    if ("Graph".equals(structure)) {
	        try {
	            SearchResult[] results = GraphExecutor.runParallelMethods(fileName, 0);
	            
	            StringBuilder sb = new StringBuilder();
	            sb.append("Graph execution (Parallel) completed.\n\n");
	            
	            for (SearchResult result : results) {
	                SearchMetrics metrics = result.getMetrics();
	                sb.append(metrics.getAlgorithmType()).append(" Results:\n")
	                  .append("Total Time: ").append(metrics.getTotalTime() / 1_000_000).append(" ms\n")
	                  .append("Computation Time: ").append(metrics.getComputationTime() / 1_000_000).append(" ms\n")
	                  .append("Communication Time: ").append(metrics.getCommunicationTime() / 1_000_000).append(" ms\n")
	                  .append("Memory Used: ").append(metrics.getMemoryUsage()).append(" bytes\n")
	                  .append("Nodes Processed: ").append(metrics.getNodesProcessed()).append("\n\n");
	            }
	            
	            outputArea.setText(sb.toString());
	            saveParallelResultsToExcel(results);
	            
	        } catch (Exception ex) {
	            ex.printStackTrace();
	            outputArea.setText("Error in parallel execution: " + ex.getMessage());
	        }
	    } else {
	        outputArea.setText("Parallel execution is not available for trees.");
	    }
	}

	private void saveParallelResultsToExcel(SearchResult[] results) {
	    try (XSSFWorkbook workbook = new XSSFWorkbook()) {
	        XSSFSheet sheet = workbook.createSheet("ParallelResults");
	        
	        // Create header
	        XSSFRow headerRow = sheet.createRow(0);
	        headerRow.createCell(0).setCellValue("Algorithm");
	        headerRow.createCell(1).setCellValue("Total Time (ms)");
	        headerRow.createCell(2).setCellValue("Computation Time (ms)");
	        headerRow.createCell(3).setCellValue("Communication Time (ms)");
	        headerRow.createCell(4).setCellValue("Memory Usage (bytes)");
	        headerRow.createCell(5).setCellValue("Nodes Processed");
	        
	        // Add data
	        for (int i = 0; i < results.length; i++) {
	            SearchMetrics metrics = results[i].getMetrics();
	            XSSFRow dataRow = sheet.createRow(i + 1);
	            dataRow.createCell(0).setCellValue(metrics.getAlgorithmType());
	            dataRow.createCell(1).setCellValue(metrics.getTotalTime() / 1_000_000);
	            dataRow.createCell(2).setCellValue(metrics.getComputationTime() / 1_000_000);
	            dataRow.createCell(3).setCellValue(metrics.getCommunicationTime() / 1_000_000);
	            dataRow.createCell(4).setCellValue(metrics.getMemoryUsage());
	            dataRow.createCell(5).setCellValue(metrics.getNodesProcessed());
	        }
	        
	        // Save file
	        try (FileOutputStream fileOut = new FileOutputStream("ParallelResults.xlsx")) {
	            workbook.write(fileOut);
	            outputArea.append("\nResults saved to ParallelResults.xlsx");
	        }
	        
	    } catch (IOException e) {
	        e.printStackTrace();
	        outputArea.append("\nError saving parallel results to Excel.");
	    }
	}

	private void saveResultsToExcel(SearchResult[] results) {
	    try (XSSFWorkbook workbook = new XSSFWorkbook()) {
	        XSSFSheet sheet = workbook.createSheet("ParallelResults");
	        
	        // Create header
	        XSSFRow headerRow = sheet.createRow(0);
	        headerRow.createCell(0).setCellValue("Algorithm");
	        headerRow.createCell(1).setCellValue("Total Time (ms)");
	        headerRow.createCell(2).setCellValue("Computation Time (ms)");
	        headerRow.createCell(3).setCellValue("Communication Time (ms)");
	        headerRow.createCell(4).setCellValue("Memory Usage (bytes)");
	        headerRow.createCell(5).setCellValue("Nodes Processed");
	        
	        // Add data
	        for (int i = 0; i < results.length; i++) {
	            SearchMetrics metrics = results[i].getMetrics();
	            XSSFRow dataRow = sheet.createRow(i + 1);
	            dataRow.createCell(0).setCellValue(metrics.getAlgorithmType());
	            dataRow.createCell(1).setCellValue(metrics.getTotalTime() / 1_000_000);
	            dataRow.createCell(2).setCellValue(metrics.getComputationTime() / 1_000_000);
	            dataRow.createCell(3).setCellValue(metrics.getCommunicationTime() / 1_000_000);
	            dataRow.createCell(4).setCellValue(metrics.getMemoryUsage());
	            dataRow.createCell(5).setCellValue(metrics.getNodesProcessed());
	        }
	        
	        // Save file
	        try (FileOutputStream fileOut = new FileOutputStream("ParallelResults.xlsx")) {
	            workbook.write(fileOut);
	            outputArea.append("\nResults saved to ParallelResults.xlsx");
	        }
	        
	    } catch (IOException e) {
	        e.printStackTrace();
	        outputArea.append("\nError saving results to Excel.");
	    }
	}

	
	/**
	 * Main method: Launches the UI.
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			MainUI frame = new MainUI();
			frame.setVisible(true);
		});
	}
}
