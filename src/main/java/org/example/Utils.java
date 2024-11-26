package org.example;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.util.Util;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.Callable;

public class Utils {

    public static void visualizeMatrix(double[][] matrix, String name, int maxRows, int maxCols) {
        int rows = Math.min(matrix.length, maxRows);
        int cols = Math.min(matrix[0].length, maxCols);
        boolean isTruncatedRows = matrix.length > maxRows;
        boolean isTruncatedCols = matrix[0].length > maxCols;

        System.out.println("Matrix " + name + " (" + matrix.length + "x" + matrix[0].length + "):");

        System.out.print("     ");
        for (int j = 0; j < cols; j++) {
            System.out.printf("[%2d]  ", j);
        }
        if (isTruncatedCols) {
            System.out.print("...");
        }
        System.out.println();

        for (int i = 0; i < rows; i++) {
            System.out.printf("[%2d] ", i);

            for (int j = 0; j < cols; j++) {
                System.out.printf("%.4f ", matrix[i][j]);
            }

            if (isTruncatedCols) {
                System.out.print("...");
            }
            System.out.println();
        }

        if (isTruncatedRows) {
            System.out.print("     ...");
            System.out.println();
        }

        if (isTruncatedRows || isTruncatedCols) {
            System.out.println("Note: Matrix display truncated to " +
                    rows + "x" + cols + " (full size: " +
                    matrix.length + "x" + matrix[0].length + ")");
        }
    }

    public static void printCPUInfo() {
        try {
            SystemInfo si = new SystemInfo();
            HardwareAbstractionLayer hal = si.getHardware();
            CentralProcessor processor = hal.getProcessor();

            System.out.println("Processor: " + processor.getProcessorIdentifier().getName());
            System.out.println("Manufacturer: " + processor.getProcessorIdentifier().getVendor());
            System.out.println("Physical Cores: " + processor.getPhysicalProcessorCount());
            System.out.println("Logical CPUs: " + processor.getLogicalProcessorCount());
            System.out.println("CPU Architecture: " + processor.getProcessorIdentifier().getMicroarchitecture());

            System.out.println("\n--- Core Frequencies ---");
            long[] currentFreqs = processor.getCurrentFreq();
            for (int i = 0; i < currentFreqs.length; i++) {
                double frequencyMHz = (double) currentFreqs[i] / 1_000_000;
                System.out.printf("Core %d: %.2f MHz%n", i, frequencyMHz);
            }

            double maxFreqMHz = (double) processor.getMaxFreq() / 1_000_000;
            System.out.printf("Max Frequency: %.2f MHz%n", maxFreqMHz);

            System.out.println("\n--- CPU Load ---");
            long[] prevTicks = processor.getSystemCpuLoadTicks();
            Util.sleep(1000);
            double cpuLoad = processor.getSystemCpuLoadBetweenTicks(prevTicks) * 100;
            System.out.printf("Current CPU Load: %.2f%%%n", cpuLoad);

            System.out.println("\n--- Temperature Information ---");
            double temperature = si.getHardware().getSensors().getCpuTemperature();
            if (temperature > 0) {
                System.out.printf("CPU Temperature: %.1f°C%n", temperature);
            } else {
                System.out.println("CPU temperature information not available");
            }

        } catch (Exception e) {
            System.err.println("Error while collecting CPU information: " + e.getMessage());
        }
    }

    public static double[][] generateMatrix(int rows, int cols, long seed) {
        Random random = new Random(seed);
        double[][] res = new double[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                res[i][j] = random.nextDouble();
            }
        }

        return res;
    }

    public static ResultData runMultipleExecutions(String model, Callable<double[][]> multiplicationMethod, int executions) {
        List<Long> executionTimes = new ArrayList<>();

        for (int i = 0; i < executions; i++) {
            try {
                Instant start = Instant.now();
                multiplicationMethod.call();
                Instant end = Instant.now();
                long duration = java.time.Duration.between(start, end).toMillis();
                executionTimes.add(duration);
                System.out.printf("Execution %d: %d ms%n", i + 1, duration);
            } catch (Exception e) {
                System.err.println("Error in execution " + (i + 1) + " of " + model +
                        ": " + e.getMessage());
            }
        }

        double averageTime = executionTimes.stream()
                                .mapToLong(Long::longValue)
                                .average()
                                .orElse(0.0);

        double standardDeviation = calculateStandardDeviation(executionTimes, averageTime);

        return new ResultData(model, averageTime, standardDeviation);
    }

    public static double calculateStandardDeviation(List<Long> executionTimes,
                                                     double average) {
        double variance = executionTimes.stream()
                            .mapToDouble(time -> {
                                double diff = time - average;
                                return diff * diff;
                            })
                            .average()
                            .orElse(0.0);

        return Math.sqrt(variance);
    }

    public static void writeResultsToCsv(List<ResultData> results, String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write("Multiplication Model,Average Execution Time (ms),Standard Deviation (ms)\n");

            for (ResultData result : results) {
                writer.write(String.format(Locale.US,"%s,%.2f,%.2f%n",
                        result.getModel(),
                        result.getAverageTime(),
                        result.getStandardDeviation()));
            }
        } catch (IOException e) {
            System.err.println("Error writing to CSV file: " + e.getMessage());
        }
    }
}
