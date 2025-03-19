package excel;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.file.*;
import java.util.List;

public class ExcelDataRecorder {

	private ExcelDataRecorder() {
		throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
	}

	private static final String DIRECTORY_PATH = "src/main/resources/data_output/";

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

	private static String generateFileName(String baseFileName, boolean isExecutionTime) {
		String fileNameModifier = isExecutionTime ? "ExecutionTimes" : "MemoryUsage";
		return DIRECTORY_PATH + baseFileName.replace("ExecutionTimes", fileNameModifier);
	}

	private static Workbook getWorkbook(String filePath) throws IOException {
		if (Files.exists(Paths.get(filePath))) {
			try (InputStream is = Files.newInputStream(Paths.get(filePath))) {
				return WorkbookFactory.create(is);
			}
		} else {
			return new XSSFWorkbook();
		}
	}

	private static Sheet getSheet(Workbook workbook, boolean isExecutionTime) {
		String sheetName = isExecutionTime ? "Execution Times" : "Memory Usage";
		Sheet sheet = workbook.getSheet(sheetName);
		if (sheet == null) {
			sheet = workbook.createSheet(sheetName);
			initializeHeaderRow(sheet, isExecutionTime);
		}
		return sheet;
	}

	private static void initializeHeaderRow(Sheet sheet, boolean isExecutionTime) {
		Row headerRow = sheet.createRow(0);
		String[] headers = isExecutionTime
				? new String[] { "Graph BFS Execution Time 10000 (s)", "Graph DFS Execution Time 10000 (s)",
						"Graph BFS Execution Time 1000 (s)", "Graph DFS Execution Time 1000 (s)",
						"Tree BFS Execution Time 10000 (s)", "Tree DFS Execution Time 10000 (s)",
						"Tree BFS Execution Time 1000 (s)", "Tree DFS Execution Time 1000 (s)" }
				: new String[] { "Graph BFS Memory Usage 10000 (bytes)", "Graph DFS Memory Usage 10000 (bytes)",
						"Graph BFS Memory Usage 1000 (bytes)", "Graph DFS Memory Usage 1000 (bytes)",
						"Tree BFS Memory Usage 10000 (bytes)", "Tree DFS Memory Usage 10000 (bytes)",
						"Tree BFS Memory Usage 1000 (bytes)", "Tree DFS Memory Usage 1000 (bytes)" };

		for (int i = 0; i < headers.length; i++) {
			headerRow.createCell(i).setCellValue(headers[i]);
		}
	}

	private static void writeDataInColumns(List<Double> bfsData, List<Double> dfsData, int columnOffset, Row row) {
		if (!bfsData.isEmpty()) {
			row.createCell(columnOffset, CellType.NUMERIC).setCellValue(bfsData.get(0));
		}
		if (!dfsData.isEmpty()) {
			row.createCell(columnOffset + 1, CellType.NUMERIC).setCellValue(dfsData.get(0));
		}
	}

	private static int calculateColumnOffset(boolean isTree, int nodeCount) {
		int columnOffset;

		if (isTree) {
			columnOffset = (nodeCount == 10000) ? 4 : 6;
		} else {
			columnOffset = (nodeCount == 10000) ? 0 : 2;
		}

		return columnOffset;
	}

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

	private static boolean isRowEmpty(Row row, int startColumnIndex) {
		for (int colIndex = startColumnIndex; colIndex < startColumnIndex + 2; colIndex++) {
			Cell cell = row.getCell(colIndex);
			if (cell != null && !cell.toString().trim().isEmpty()) {
				return false;
			}
		}
		return true;
	}

	private static void saveWorkbook(Workbook workbook, String filePath) throws IOException {
		try (OutputStream outputStream = Files.newOutputStream(Paths.get(filePath))) {
			workbook.write(outputStream);
		}
	}
}
