package org.example;

import java.util.LinkedList;
import java.util.List;

public class Statistics {
    private final List<Double> extraPheromone = new LinkedList<>();
    private final List<Integer> distances = new LinkedList<>();
    private final List<Double> wishes = new LinkedList<>();

    public void addExtraPheromone(double extraPheromone) {
        this.extraPheromone.add(extraPheromone);
    }

    public void addDistances(List<Integer> distances) {
        this.distances.addAll(distances);
    }
    public void addDistance(Integer distance) {
        this.distances.add(distance);
    }

    public void addWish(double wish) {
        this.wishes.add(wish);
    }

    private double getAverageExtraPheromone() {
        return extraPheromone.stream().mapToDouble(e -> e).average().orElse(0);
    }

    private double getAverageWish() {
        return wishes.stream().mapToDouble(w -> w).average().orElse(0);
    }

    public double getAverageDistance() {
        return distances.stream().mapToInt(d -> d).average().orElse(0);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Statistics{");
        sb.append("averageExtraPheromone=").append(getAverageExtraPheromone());
        sb.append(", averageDistance=").append(getAverageDistance());
        sb.append(", averageWish=").append(getAverageWish());
        sb.append('}');
        return sb.toString();
    }
}
