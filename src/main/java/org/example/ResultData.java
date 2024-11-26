package org.example;

public class ResultData {
    private final String model;
    private final double averageTime;
    private final double standardDeviation;

    public ResultData(String model, double averageTime, double standardDeviation) {
        this.model = model;
        this.averageTime = averageTime;
        this.standardDeviation = standardDeviation;
    }

    public String getModel() {
        return model;
    }

    public double getAverageTime() {
        return averageTime;
    }

    public double getStandardDeviation() {
        return standardDeviation;
    }
}
