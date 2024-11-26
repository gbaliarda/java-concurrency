package org.example;

import java.util.ArrayList;
import java.util.List;

public class App {
    private static int SIZE = 1024;
    private static long SEED = 6834723;
    private static final int EXECUTIONS = 10;
    private static final String CSV_FILE = "matrix_multiplication_results.csv";
    public static void main(String[] args) {

        System.out.println("============ CPU Information ============");

        Utils.printCPUInfo();

        System.out.println("=======================================");
        System.out.println("============ Matrix Generation ============");
        double[][] A = Utils.generateMatrix(SIZE, SIZE, SEED);
        double[][] B = Utils.generateMatrix(SIZE, SIZE, SEED);

        Utils.visualizeMatrix(A, "A", 5, 5);
        Utils.visualizeMatrix(B, "B", 5, 5);
        System.out.println("=======================================");

        System.out.println("============ Warmup ============");
        Utils.runMultipleExecutions("Sequential",
                () -> SecuentialMultiply.multiply(A, B), EXECUTIONS);
        System.out.println("\nWarmup end");

        System.out.println("=======================================");

        List<ResultData> results = new ArrayList<>();

        System.out.println("============ Secuential Matrix Multiplication ============");
        System.out.println("\nRunning Sequential Multiplication...");
        results.add(Utils.runMultipleExecutions("Sequential",
                () -> SecuentialMultiply.multiply(A, B), EXECUTIONS));

        System.out.println("=======================================");
        System.out.println("============ ExecutorService Matrix Multiplication ============");

        int maxThreads = Runtime.getRuntime().availableProcessors();
        for (int threads = 0; threads <= maxThreads; threads++) {
            final int threadCount = threads;
            System.out.println("\nRunning ExecutorService Multiplication with " + threads + " threads...");
            results.add(Utils.runMultipleExecutions(
                    "ExecutorService_" + threads + "threads",
                    () -> ExecutorServiceMultiply.multiply(A, B, threadCount), EXECUTIONS));
        }

        System.out.println("=======================================");
        System.out.println("============ ForkJoin Matrix Multiplication ============");
        for (int threads = 0; threads <= maxThreads; threads++) {
            final int threadCount = threads;
            System.out.println("\nRunning ForkJoin Multiplication with " + threads + " threads...");
            results.add(Utils.runMultipleExecutions(
                    "ForkJoin_" + threads + "threads",
                    () -> ForkJoinMultiply.multiply(A, B, threadCount), EXECUTIONS));
        }

        System.out.println("=======================================");

        Utils.writeResultsToCsv(results, CSV_FILE);
        System.out.println("\nResults have been written to " + CSV_FILE);

    }


}
