package org.example.graph.algorithm.impl.ant;

import com.google.common.graph.EndpointPair;
import com.google.common.graph.MutableValueGraph;
import org.example.graph.Vertex;
import org.example.graph.algorithm.Path;
import org.example.graph.algorithm.Road;
import org.example.graph.algorithm.RouteAlgorithm;
import org.example.graph.algorithm.impl.ant.exception.AntAlgorithmException;
import org.example.graph.parser.GraphParser;
import org.example.graph.parser.strategy.exception.GraphParserStrategyException;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.graph.EndpointPair.unordered;

public class AntAlgorithm extends RouteAlgorithm {

    private final int alfa;
    private final int beta;
    private final int lMin;
    private final int numberOfAnts;
    private MutableValueGraph<Vertex, Integer> graph;
    private Map<EndpointPair<Vertex>, Road> roads;

    public AntAlgorithm(
            int alfa,
            int beta,
            int lMin,
            int numberOfAnts,
            GraphParser parser) {
        super(parser);
        this.alfa = alfa;
        this.beta = beta;
        this.lMin = lMin;
        this.numberOfAnts = numberOfAnts;
    }

    @Override
    public Stack<Vertex> buildRoute(Vertex start, Vertex end) throws AntAlgorithmException {
        if (start.equals(end)) {
            return new Stack<>();
        }
        buildGraph();
        checkPoints(start, end);
        initRoads();
        for (int i = 0; i < 10; i++) {
            List<Path> paths = new ArrayList<>(numberOfAnts);
            for (int j = 0; j < numberOfAnts; j++)
                paths.add(findPath(start, end));
            updateRoads(paths);
        }
        return buildBestRoute(start, end);
    }

    private void checkPoints(Vertex s, Vertex e) throws AntAlgorithmException {
        Set<Vertex> nodes = graph.nodes();

        if (!nodes.contains(s) || !nodes.contains(e))
            throw new AntAlgorithmException("Invalid input points");
    }

    private Stack<Vertex> buildBestRoute(Vertex start, Vertex end) throws AntAlgorithmException {
        Set<Vertex> nodes = graph.nodes();
        Stack<Vertex> bestRoute = new Stack<>();
        Set<Vertex> visited = new HashSet<>();

        Vertex curr = start;
        bestRoute.push(curr);
        visited.add(curr);

        int length = nodes.size();
        while (visited.size() < length) {
            Set<Road> availableRoads = getAvailableRoads(curr, visited);

            Road bestRoad = findBestRoad(availableRoads);
            Vertex next = bestRoad.getEdge().adjacentNode(curr);
            bestRoute.push(next);
            visited.add(next);
            curr = next;

            if (curr.equals(end))
                return bestRoute;
        }

        throw new AntAlgorithmException("Cannot build route");
    }

    private Road findBestRoad(Set<Road> road) {
        return road.stream()
                .max(Comparator.comparing(Road::getPheromone))
                .orElseThrow(() -> new IllegalStateException("Can not find best road"));
    }

    private void updateRoads(List<Path> paths) {
        Map<EndpointPair<Vertex>, Road> roads = this.roads;
        int lMin = this.lMin;

        roads.values().forEach(road -> road.pheromoneEvaporation(0.64));
        for (Path path : paths) {
            double extraPheromone = path.getExtraPheromone(lMin);
            for (Road road : path.getRoads()) road.addPheromone(extraPheromone);
        }
    }

    private Path findPath(Vertex start, Vertex end) throws AntAlgorithmException {
        MutableValueGraph<Vertex, Integer> graph = this.graph;

        Path path = new Path(graph);
        Set<Vertex> visited = new HashSet<>();
        visited.add(start);
        int numberOfNodes = graph.nodes().size();
        Vertex curr = start;
        while (visited.size() < numberOfNodes) {
            Map<Road, Double> tp = transitionProbabilities(curr, visited);
            Road next = next(tp);
            path.addRoad(next);
            EndpointPair<Vertex> edge = next.getEdge();
            curr = edge.adjacentNode(curr);

            if (curr.equals(end))
                return path;
        }

        throw new AntAlgorithmException("There is no such end point");
    }

    private Road next(Map<Road, Double> tp) {
        double random = calculateRandom();
        Road last = null;
        for (Map.Entry<Road, Double> entry : tp.entrySet()) {
            random -= entry.getValue();
            last = entry.getKey();
            if (random <= 0)
                return last;
        }
        return last;
    }

    private double calculateRandom() {
        Random random = new Random();
        int randomInt = random.nextInt(1001);
        return randomInt / 1000.0;
    }

    private Map<Road, Double> transitionProbabilities(Vertex curr, Set<Vertex> visited) {
        Set<Road> availableRoads = getAvailableRoads(curr, visited);
        double sum = findSumOfWish(availableRoads);
        return availableRoads.stream()
                .collect(Collectors.toMap(
                        Function.identity(), road -> findProbability(road, sum)));
    }

    private Set<Road> getAvailableRoads(Vertex curr, Set<Vertex> visited) {
        return graph.adjacentNodes(curr).stream()
                .filter(v -> !visited.contains(v))
                .map(v -> roads.get(unordered(curr, v)))
                .collect(Collectors.toSet());
    }

    private double findProbability(Road road, double sum) {
        return road.calculateWish(alfa, beta) / sum;
    }

    private double findSumOfWish(Set<Road> roads) {
        return roads.stream()
                .mapToDouble(road -> road.calculateWish(alfa, beta))
                .sum();
    }

    private void initRoads() {
        this.roads = graph.edges().stream()
                .collect(Collectors.toMap(
                        Function.identity(), endpointPair -> Road.from(
                                endpointPair, calculateProximity(getLength(endpointPair)))));
    }

    private int getLength(EndpointPair<Vertex> endpointPair) {
        return graph.edgeValue(endpointPair)
                .orElseThrow(() -> new IllegalStateException("There is no value for given endpoint pair"));
    }

    private double calculateProximity(int length) {
        return 1 / (float) length;
    }

    private void buildGraph() throws AntAlgorithmException {
        try {
            this.graph = graphParser.parse();
        } catch (GraphParserStrategyException e) {
            throw new AntAlgorithmException(e);
        }
    }
}