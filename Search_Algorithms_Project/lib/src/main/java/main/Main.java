package main;

import java.util.Scanner;

import executors.GraphExecutor;
import executors.TreeExecutor;

public class Main {
	
	private static final String filePathGraph10000 = "src/main/resources/data_input/graph10000.txt";
	private static final String filePathGraph1000 = "src/main/resources/data_input/graph1000.txt";
	private static final String filePathTree10000 = "src/main/resources/data_input/tree10000.txt";
	private static final String filePathTree1000 = "src/main/resources/data_input/tree1000.txt";

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		int dataStructureChoice;
		int nodeCount = 0;
		int startNodeID = 0;

		do {
			dataStructureChoice = displayDataStructureChoice(scanner);

			if (dataStructureChoice == 1 || dataStructureChoice == 2) {
				System.out.println("\nChoose the number of nodes (1000 or 10000):");
				while (!scanner.hasNextInt() || !((nodeCount = scanner.nextInt()) == 1000 || nodeCount == 10000)) {
					System.out.println("Invalid input. Please choose 1000 or 10000.");
					scanner.nextLine();
				}

				String fileName;
				if (dataStructureChoice == 1) {
					fileName = (nodeCount == 1000) ? filePathGraph1000 : filePathGraph10000;
					displayChoices();
					int choice = scanner.nextInt();
					choiceRun(choice, fileName, startNodeID, nodeCount, scanner, false);
				} else {
					fileName = (nodeCount == 1000) ? filePathTree1000 : filePathTree10000;
					displayChoices();
					int choice = scanner.nextInt();
					choiceRun(choice, fileName, startNodeID, nodeCount, scanner, true);
				}
			} else if (dataStructureChoice == 3) {
				System.out.println("Exiting the program.");
				break;
			}
		} while (true);

		scanner.close();
	}

	private static int displayDataStructureChoice(Scanner scanner) {
		System.out.println("\nChoose the data structure:");
		System.out.println("1. Graph");
		System.out.println("2. Tree");
		System.out.println("3. Exit");
		System.out.print("Your choice (1/2/3): ");
		int choice = scanner.nextInt();
		scanner.nextLine();
		return choice;
	}

	private static void displayChoices() {
		System.out.println("\nSelect the execution method:");
		System.out.println("1. Sequential");
		System.out.println("2. Parallel (available only for Graph)");
		System.out.println("3. Exit");
		System.out.print("Your choice (1/2/3): ");
	}

	private static void choiceRun(int choice, String fileName, int startNodeID, int nodeCount, Scanner scanner,
			boolean isTree) {
		if (isTree) {
			switch (choice) {
			case 1:
				TreeExecutor.runTreeMethods(fileName, scanner, nodeCount);
				break;
			case 2:
				System.out.println("Parallel execution is not implemented for trees.");
				break;
			case 3:
				System.out.println("Exiting the program.");
				break;
			default:
				System.out.println("Invalid option. Please choose again.");
				scanner.nextLine();
				break;
			}
		} else {
			switch (choice) {
			case 1:
				GraphExecutor.runSequentialMethods(fileName, startNodeID, nodeCount);
				break;
			case 2:
				try {
					GraphExecutor.runParallelMethods(fileName, startNodeID);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case 3:
				System.out.println("Exiting the program.");

				break;
			default:
				System.out.println("Invalid option. Please choose again.");
				scanner.nextLine();
				break;
			}
		}
	}
}
