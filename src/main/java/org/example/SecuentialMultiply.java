package org.example;

import java.util.Random;

public class SecuentialMultiply
{
    /**
     * Realiza la multiplicación de matrices de manera secuencial.
     *
     * @param A Matriz A
     * @param B Matriz B
     * @return Matriz resultante C
     * @throws IllegalArgumentException si las dimensiones de las matrices no son compatibles
     */
    public static double[][] multiply(double[][] A, double[][] B) {
        int rowsA = A.length;
        int colsA = A[0].length;
        int colsB = B[0].length;
        if (colsA != B.length) {
            throw new IllegalArgumentException("El número de columnas de A debe coincidir con el número de filas de B.");
        }

        double[][] C = new double[rowsA][colsB];

        for (int i = 0; i < rowsA; i++) {
            for (int j = 0; j < colsB; j++) {
                for (int k = 0; k < colsA; k++) {
                    C[i][j] = C[i][j] + A[i][k] * B[k][j];
                }
            }
        }

        return C;
    }
}
