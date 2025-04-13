package excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import utils.SearchMetrics;
import utils.SearchResult;

import java.io.*;
import java.nio.file.*;
import java.util.List;

/**
 * Utility class for recording BFS and DFS execution data into an Excel file.
 * Prevents instantiation.
 */
public class ExcelDataRecorder {

    private ExcelDataRecorder() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

    private static final String DIRECTORY_PATH = "src/main/resources/data_output/";

    /**
     * Writes BFS and DFS execution data to an Excel file.
     *
     * @param baseFileName    The base name of the file.
     * @param bfsData         List containing BFS execution data.
     * @param dfsData         List containing DFS execution data.
     * @param nodeCount       Number of nodes in the graph/tree.
     * @param isExecutionTime True if writing execution times, false for memory usage.
     * @param isTree          True if the data is for a tree, false for a graph.
     * @throws IOException If an error occurs during file operations.
     */
    public static void writeData(String baseFileName, List<Double> bfsData, List<Double> dfsData, int nodeCount,
                                 boolean isExecutionTime, boolean isTree) throws IOException {
        String fileName = generateFileName(baseFileName, isExecutionTime);
        Files.createDirectories(Paths.get(DIRECTORY_PATH));

        try (Workbook workbook = getWorkbook(fileName)) {
            Sheet sheet = getSheet(workbook, isExecutionTime);
            int columnOffset = calculateColumnOffset(isTree, nodeCount);
            int rowNumberToWrite = getNextEmptyRow(sheet, columnOffset);
            Row row = sheet.createRow(rowNumberToWrite);

            writeDataInColumns(bfsData, dfsData, columnOffset, row);
            saveWorkbook(workbook, fileName);
        }
    }

    /**
     * Generates the appropriate file name based on execution type.
     *
     * @param baseFileName    The base name of the file.
     * @param isExecutionTime True if writing execution times, false for memory usage.
     * @return The complete file path.
     */
    private static String generateFileName(String baseFileName, boolean isExecutionTime) {
        String fileNameModifier = isExecutionTime ? "ExecutionTimes" : "MemoryUsage";
        return Paths.get(DIRECTORY_PATH, baseFileName.replace("ExecutionTimes", fileNameModifier)).toString();
    }

    /**
     * Retrieves or creates an Excel workbook based on the file path.
     *
     * @param filePath The path to the Excel file.
     * @return The workbook instance.
     * @throws IOException If an error occurs while reading the file.
     */
    private static Workbook getWorkbook(String filePath) throws IOException {
        if (Files.exists(Paths.get(filePath))) {
            try (InputStream is = Files.newInputStream(Paths.get(filePath))) {
                return WorkbookFactory.create(is);
            }
        }
        return new XSSFWorkbook();
    }

    /**
     * Retrieves or creates a sheet for execution times or memory usage.
     *
     * @param workbook        The Excel workbook.
     * @param isExecutionTime True if writing execution times, false for memory usage.
     * @return The sheet instance.
     */
    private static Sheet getSheet(Workbook workbook, boolean isExecutionTime) {
        String sheetName = isExecutionTime ? "Execution Times" : "Memory Usage";
        Sheet sheet = workbook.getSheet(sheetName);

        if (sheet == null) {
            sheet = workbook.createSheet(sheetName);
            initializeHeaderRow(sheet, isExecutionTime);
        }
        return sheet;
    }

    /**
     * Initializes the header row with appropriate column names.
     *
     * @param sheet           The sheet to initialize.
     * @param isExecutionTime True if writing execution times, false for memory usage.
     */
    private static void initializeHeaderRow(Sheet sheet, boolean isExecutionTime) {
        Row headerRow = sheet.createRow(0);
        String[] headers = isExecutionTime
                ? new String[]{"Graph BFS Execution Time 10000 (s)", "Graph DFS Execution Time 10000 (s)",
                "Graph BFS Execution Time 1000 (s)", "Graph DFS Execution Time 1000 (s)",
                "Tree BFS Execution Time 10000 (s)", "Tree DFS Execution Time 10000 (s)",
                "Tree BFS Execution Time 1000 (s)", "Tree DFS Execution Time 1000 (s)"}
                : new String[]{"Graph BFS Memory Usage 10000 (bytes)", "Graph DFS Memory Usage 10000 (bytes)",
                "Graph BFS Memory Usage 1000 (bytes)", "Graph DFS Memory Usage 1000 (bytes)",
                "Tree BFS Memory Usage 10000 (bytes)", "Tree DFS Memory Usage 10000 (bytes)",
                "Tree BFS Memory Usage 1000 (bytes)", "Tree DFS Memory Usage 1000 (bytes)"};

        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }
    }

    /**
     * Writes BFS and DFS data into the appropriate columns.
     */
    private static void writeDataInColumns(List<Double> bfsData, List<Double> dfsData, int columnOffset, Row row) {
        if (!bfsData.isEmpty()) {
            row.createCell(columnOffset, CellType.NUMERIC).setCellValue(bfsData.get(0));
        }
        if (!dfsData.isEmpty()) {
            row.createCell(columnOffset + 1, CellType.NUMERIC).setCellValue(dfsData.get(0));
        }
    }

    /**
     * Determines the column offset based on whether the structure is a tree or graph.
     */
    private static int calculateColumnOffset(boolean isTree, int nodeCount) {
        return isTree ? (nodeCount == 10000 ? 4 : 6) : (nodeCount == 10000 ? 0 : 2);
    }

    /**
     * Finds the next available row for writing data.
     */
    private static int getNextEmptyRow(Sheet sheet, int startColumnIndex) {
        int lastRowNum = sheet.getLastRowNum();
        for (int rowNum = 1; rowNum <= lastRowNum; rowNum++) {
            Row row = sheet.getRow(rowNum);
            if (isRowEmpty(row, startColumnIndex)) {
                return rowNum;
            }
        }
        return lastRowNum + 1;
    }

    /**
     * Checks if a row is empty based on the given column index range.
     */
    private static boolean isRowEmpty(Row row, int startColumnIndex) {
        for (int colIndex = startColumnIndex; colIndex < startColumnIndex + 2; colIndex++) {
            Cell cell = row.getCell(colIndex);
            if (cell != null && !cell.toString().trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Saves the workbook to the specified file path.
     */
    private static void saveWorkbook(Workbook workbook, String filePath) throws IOException {
        try (OutputStream outputStream = Files.newOutputStream(Paths.get(filePath))) {
            workbook.write(outputStream);
        }
    }
    
    //code newly added for communication & computation time
    public static void writeMetricsData(String fileName, SearchResult[] results, int nodeCount) throws IOException {
        String filePath = Paths.get(DIRECTORY_PATH, fileName).toString();
        Files.createDirectories(Paths.get(DIRECTORY_PATH));

        try (Workbook workbook = getWorkbook(filePath)) {
            Sheet sheet = workbook.createSheet("Metrics");
            
            // Crează header
            Row headerRow = sheet.createRow(0);
            String[] headers = {
                "Algorithm", "Total Time (ms)", "Computation Time (ms)", 
                "Communication Time (ms)", "Memory Usage (bytes)", "Nodes Processed"
            };
            
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }
            
            for (int i = 0; i < results.length; i++) {
                SearchMetrics metrics = results[i].getMetrics();
                Row row = sheet.createRow(i + 1);
                
                row.createCell(0).setCellValue(metrics.getAlgorithmType());
                row.createCell(1).setCellValue(metrics.getTotalTime() / 1_000_000.0);
                row.createCell(2).setCellValue(metrics.getComputationTime() / 1_000_000.0);
                row.createCell(3).setCellValue(metrics.getCommunicationTime() / 1_000_000.0);
                row.createCell(4).setCellValue(metrics.getMemoryUsage());
                row.createCell(5).setCellValue(metrics.getNodesProcessed());
            }
   
            saveWorkbook(workbook, filePath);
        }
    }
}
