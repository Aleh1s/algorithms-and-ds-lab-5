package org.example.graph;

import static java.lang.Math.pow;

public class Properties {
    private final double proximity;
    private double pheromone;

    private Properties(double proximity) {
        this.proximity = proximity;
        this.pheromone = 0.2;
    }

    public double getProximity() {
        return proximity;
    }

    public double getPheromone() {
        return pheromone;
    }

    public void setPheromone(double pheromone) {
        this.pheromone = pheromone;
    }

    public static Properties from(double proximity) {
        return new Properties(proximity);
    }

    public double calculateWish(int alfa, int beta) {
        return pow(pheromone, alfa) + pow(proximity, beta);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Properties{");
        sb.append("proximity=").append(proximity);
        sb.append(", pheromone=").append(pheromone);
        sb.append('}');
        return sb.toString();
    }
}
