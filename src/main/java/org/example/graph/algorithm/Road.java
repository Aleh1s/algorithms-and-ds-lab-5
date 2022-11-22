package org.example.graph.algorithm;

import com.google.common.graph.EndpointPair;
import org.example.graph.Vertex;

import static java.lang.Math.pow;

public class Road {
    private EndpointPair<Vertex> edge;
    private final double proximity;
    private double pheromone;

    private Road(EndpointPair<Vertex> edge, double proximity) {
        this.edge = edge;
        this.proximity = proximity;
        this.pheromone = 0.2;
    }

    public void pheromoneEvaporation(double k) {
        this.pheromone *= k;
    }

    public static Road from(EndpointPair<Vertex> edge, double proximity) {
        return new Road(edge, proximity);
    }

    public double calculateWish(int alfa, int beta) {
        return pow(pheromone, alfa) + pow(proximity, beta);
    }

    public void addPheromone(double extraPheromone) {
        this.pheromone += extraPheromone;
    }

    public EndpointPair<Vertex> getEdge() {
        return edge;
    }

    public double getProximity() {
        return proximity;
    }

    public double getPheromone() {
        return pheromone;
    }
}
