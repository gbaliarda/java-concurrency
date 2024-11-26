package org.example;

import java.util.Random;
import java.util.concurrent.*;

public class ExecutorServiceMultiply {

    /**
     * Realiza la multiplicación de matrices usando ExecutorService.
     *
     * @param A Matriz A
     * @param B Matriz B
     * @param threads Cantidad de hilos a utilizar
     * @return Matriz resultante C
     * @throws IllegalArgumentException si las dimensiones de las matrices no son compatibles
     */
    public static double[][] multiply(double[][] A, double[][] B, int threads) {
        int rowsA = A.length;
        int colsA = A[0].length;
        int colsB = B[0].length;
        if (colsA != B.length) {
            throw new IllegalArgumentException("El número de columnas de A debe coincidir con el número de filas de B.");
        }

        double[][] C = new double[rowsA][colsB];

        ExecutorService executor = Executors.newFixedThreadPool(threads);

        for (int i = 0; i < rowsA; i++) {
            executor.execute(new MultiplyRowTask(A, B, C, i, colsA, colsB));
        }

        try {
            executor.shutdown();
            while (!executor.isTerminated()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return C;
    }

    static class MultiplyRowTask implements Runnable {
        private final double[][] A, B, C;
        private final int row;
        private final int colsA, colsB;

        MultiplyRowTask(double[][] A, double[][] B, double[][] C, int row, int colsA, int colsB) {
            this.A = A;
            this.B = B;
            this.C = C;
            this.row = row;
            this.colsA = colsA;
            this.colsB = colsB;
        }

        @Override
        public void run() {
            for (int j = 0; j < colsB; j++) {
                C[row][j] = 0;
                for (int k = 0; k < colsA; k++) {
                    C[row][j] += A[row][k] * B[k][j];
                }
            }
        }
    }

    public static void main(String[] args) {
        int SIZE = 1024;
        long SEED = 6834723;
        double[][] A = new double[SIZE][SIZE];
        double[][] B = new double[SIZE][SIZE];

        Random random = new Random(SEED);

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                A[i][j] = random.nextDouble();
                B[i][j] = random.nextDouble();
            }
        }

        double[][] C = ExecutorServiceMultiply.multiply(A, B, Runtime.getRuntime().availableProcessors());

        System.out.printf("Fin: %f%n", C[0][0]);
    }
}
