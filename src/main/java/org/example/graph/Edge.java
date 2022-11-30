package org.example.graph;

import static java.lang.Math.pow;

public class Edge {
    private double pheromone;
    private final int distance;
    private final double proximity;
    private Edge(double pheromone, double proximity, int distance) {
        this.pheromone = pheromone;
        this.proximity = proximity;
        this.distance = distance;
    }

    public static Edge valueOf(int distance) {
        return new Edge(0.2, 1 / (double) distance, distance);
    }

    public double getPheromone() {
        return pheromone;
    }

    public int getDistance() {
        return distance;
    }

    public double getProximity() {
        return proximity;
    }

    public void evaporatePheromone(double k) {
        this.pheromone *= k;
    }

    public double calculateWish(int alfa, int beta) {
        return pow(pheromone, alfa) * pow(proximity, beta);
    }

    public void addPheromone(double extraPheromone) {
        this.pheromone += extraPheromone;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Edge{");
        sb.append("pheromone=").append(pheromone);
        sb.append(", distance=").append(distance);
        sb.append(", proximity=").append(proximity);
        sb.append('}');
        return sb.toString();
    }
}
