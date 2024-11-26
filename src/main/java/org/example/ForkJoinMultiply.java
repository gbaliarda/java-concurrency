package org.example;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class ForkJoinMultiply {
    private static final int THRESHOLD = 64;

    /**
     * Realiza la multiplicación de matrices usando Fork/Join.
     *
     * @param A Matriz A
     * @param B Matriz B
     * @return Matriz resultante C
     * @throws IllegalArgumentException si las dimensiones de las matrices no son compatibles
     */
    public static double[][] multiply(double[][] A, double[][] B, int maxThreads) {
        int rowsA = A.length;
        int colsA = A[0].length;
        int colsB = B[0].length;
        if (colsA != B.length) {
            throw new IllegalArgumentException("El número de columnas de A debe coincidir con el número de filas de B.");
        }

        double[][] C = new double[rowsA][colsB];

        ForkJoinPool pool = new ForkJoinPool(maxThreads);
        MatrixMultiplyTask task = new MatrixMultiplyTask(A, B, C, 0, rowsA);
        pool.invoke(task);

        return C;
    }

    static class MatrixMultiplyTask extends RecursiveAction {
        private final double[][] A, B, C;
        private final int startRow, endRow;

        MatrixMultiplyTask(double[][] A, double[][] B, double[][] C, int startRow, int endRow) {
            this.A = A;
            this.B = B;
            this.C = C;
            this.startRow = startRow;
            this.endRow = endRow;
        }

        @Override
        protected void compute() {
            int colsB = B[0].length;
            int colsA = A[0].length;
            if (endRow - startRow <= THRESHOLD) {

                for (int i = startRow; i < endRow; i++) {
                    for (int j = 0; j < colsB; j++) {
                        C[i][j] = 0;
                        for (int k = 0; k < colsA; k++) {
                            C[i][j] += A[i][k] * B[k][j];
                        }
                    }
                }
            } else {
                int midRow = (startRow + endRow) / 2;
                MatrixMultiplyTask leftTask = new MatrixMultiplyTask(A, B, C, startRow, midRow);
                MatrixMultiplyTask rightTask = new MatrixMultiplyTask(A, B, C, midRow, endRow);

                invokeAll(leftTask, rightTask);
            }
        }
    }
}
