package utils;

public class SearchMetrics {
	private long totalTime;
	private long computationTime;
	private long communicationTime;
	private long memoryUsage;
	private String algorithmType;
	private boolean isParallel;
	private int nodesProcessed;

	// Constructor
	public SearchMetrics(long totalTime, long computationTime, long communicationTime, long memoryUsage,
			String algorithmType, boolean isParallel, int nodesProcessed) {
		this.totalTime = totalTime;
		this.computationTime = computationTime;
		this.communicationTime = communicationTime;
		this.memoryUsage = memoryUsage;
		this.algorithmType = algorithmType;
		this.isParallel = isParallel;
		this.nodesProcessed = nodesProcessed;
	}

	// Getters
	public long getTotalTime() {
		return totalTime;
	}

	public long getComputationTime() {
		return computationTime;
	}

	public long getCommunicationTime() {
		return communicationTime;
	}

	public long getMemoryUsage() {
		return memoryUsage;
	}

	public String getAlgorithmType() {
		return algorithmType;
	}

	public boolean isParallel() {
		return isParallel;
	}

	public int getNodesProcessed() {
		return nodesProcessed;
	}

	public void printMetrics() {
		System.out.println("\n=== " + algorithmType + (isParallel ? " Parallel" : " Sequential") + " Metrics ===");
		System.out.println("Total time: " + totalTime + " ns (" + (totalTime / 1_000_000.0) + " ms)");
		System.out.println(
				"Computation time: " + computationTime + " ns (" + (computationTime * 100.0 / totalTime) + "%)");
		System.out.println("Communication/sync time: " + communicationTime + " ns ("
				+ (communicationTime * 100.0 / totalTime) + "%)");
		System.out.println("Memory used: " + memoryUsage + " bytes");
		System.out.println("Nodes processed: " + nodesProcessed);
	}
}