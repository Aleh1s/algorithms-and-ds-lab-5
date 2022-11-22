package org.example.graph.algorithm.impl.ant;

import com.google.common.graph.EndpointPair;
import com.google.common.graph.MutableValueGraph;
import org.example.graph.Vertex;
import org.example.graph.algorithm.Entry;
import org.example.graph.algorithm.Path;
import org.example.graph.algorithm.Road;
import org.example.graph.algorithm.RouteAlgorithm;
import org.example.graph.algorithm.exporter.GraphExporter;
import org.example.graph.algorithm.impl.ant.exception.AntAlgorithmException;
import org.example.graph.parser.GraphParser;
import org.example.graph.parser.strategy.exception.GraphParserStrategyException;

import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.graph.EndpointPair.unordered;
import static java.util.function.Function.identity;
import static org.example.graph.algorithm.impl.ant.Indicator.*;
import static org.example.graph.algorithm.impl.ant.Indicator.FAILURE;

public class AntAlgorithm extends RouteAlgorithm {

    private final int alfa;
    private final int beta;
    private final int lMin;
    private final int numberOfAnts;
    private final int iterations;
    private final double p;
    private MutableValueGraph<Vertex, Integer> graph;
    private Map<EndpointPair<Vertex>, Road> roads;

    public AntAlgorithm(
            int alfa,
            int beta,
            int lMin,
            int numberOfAnts,
            int iterations,
            double p,
            GraphParser parser,
            GraphExporter exporter) {
        super(parser, exporter);
        this.alfa = alfa;
        this.beta = beta;
        this.lMin = lMin;
        this.numberOfAnts = numberOfAnts;
        this.iterations = iterations;
        this.p = p;
    }

    @Override
    public Stack<Vertex> buildRoute(Vertex start, Vertex end) throws AntAlgorithmException {
        if (start.equals(end))
            new Stack<>();

        buildGraph();
        checkPoints(start, end);
        initRoads();

        for (int i = 0; i < iterations; i++) {
            List<Path> paths = new ArrayList<>(numberOfAnts);
            for (int j = 0; j < numberOfAnts; j++)
                paths.add(findPath(start, end));
            updateRoads(paths);
        }

        Stack<Vertex> path = buildBestRoute(start, end);
        exportGraph(path);
        return path;
    }

    private Path findPath(Vertex start, Vertex end) {
        Stack<Vertex> path = new Stack<>();
        Indicator i = findPathRecursive(start, end, new HashSet<>(), path);

        if (i.equals(FAILURE))
            throw new IllegalStateException("Cannot find path");

        return transform(path);
    }

    private Path transform(Stack<Vertex> path) {
        Path p = new Path(graph);
        Vertex curr = path.pop(), next;
        while (!path.empty()) {
            next = path.pop();
            p.addRoad(roads.get(unordered(curr, next)));
            curr = next;
        }
        return p;
    }

    private Indicator findPathRecursive(Vertex curr, Vertex end, HashSet<Vertex> visited, Stack<Vertex> path) {
        visited.add(curr);
        path.push(curr);

        if (curr.equals(end))
            return RESULT;

        Set<Vertex> successors = getSuccessors(curr, visited);

        if (successors.isEmpty())
            return FAILURE;

        Map<Vertex, Double> p = probabilities(curr, successors);
        while (p.size() > 0) {
            Indicator i = findPathRecursive(nextVertex(p), end, visited, path);

            if (i.equals(RESULT))
                return i;

            successors.remove(path.pop());
            p = probabilities(curr, successors);
        }

        return FAILURE;
    }

    private Vertex nextVertex(Map<Vertex, Double> probabilities) {
        double random = calculateRandom();
        Vertex last = null;
        for (Map.Entry<Vertex, Double> e : probabilities.entrySet()) {
            random -= e.getValue();
            last = e.getKey();
            if (random <= 0)
                break;
        }
        return last;
    }

    private Set<Vertex> getSuccessors(Vertex curr, Set<Vertex> visited) {
        MutableValueGraph<Vertex, Integer> graph = this.graph;
        return graph.adjacentNodes(curr).stream()
                .filter(v -> !visited.contains(v))
                .collect(Collectors.toSet());
    }

    private Map<Vertex, Double> probabilities(Vertex curr, Set<Vertex> successors) {
        double sum = successors.stream()
                .map(s -> roads.get(unordered(curr, s)))
                .mapToDouble(r -> r.calculateWish(alfa, beta))
                .sum();

        return successors.stream()
                .collect(Collectors.toMap(
                        identity(), s -> calculateProbability(roads.get(unordered(curr, s)), sum)));
    }


    private void exportGraph(Stack<Vertex> path) {
        graphExporter.setGraph(graph);
        graphExporter.setEdges(roads);
        graphExporter.setPath(new HashSet<>(path));
        graphExporter.export();
    }

    private void checkPoints(Vertex s, Vertex e) throws AntAlgorithmException {
        Set<Vertex> nodes = graph.nodes();

        if (!nodes.contains(s) || !nodes.contains(e))
            throw new AntAlgorithmException("Invalid input points");
    }

    private Stack<Vertex> buildBestRoute(Vertex start, Vertex end) {
        Stack<Vertex> path = new Stack<>();
        Indicator i = buildBestRouteRecursive(start, end, new HashSet<>(), path);

        if (i.equals(FAILURE))
            throw new IllegalStateException("Cannot find path");

        System.out.println("Length -> " + transform((Stack<Vertex>) path.clone()).getLength());

        return path;
    }

    private Indicator buildBestRouteRecursive(Vertex curr, Vertex end, Set<Vertex> visited, Stack<Vertex> path) {
        visited.add(curr);
        path.push(curr);

        if (curr.equals(end))
            return RESULT;

        Set<Vertex> successors = getSuccessors(curr, visited);

        if (successors.isEmpty())
            return FAILURE;

        PriorityQueue<Entry> entries = pheromoneQueue(curr, successors);
        while (!entries.isEmpty()) {
            Indicator i = buildBestRouteRecursive(entries.poll().getVertex(), end, visited, path);

            if (i.equals(RESULT))
                return i;
        }

        return FAILURE;
    }

    private PriorityQueue<Entry> pheromoneQueue(Vertex curr, Set<Vertex> successors) {
        PriorityQueue<Entry> queue = new PriorityQueue<>(Comparator.comparing(Entry::getPheromone).reversed());
        successors.stream()
                .map(v -> Entry.from(v, roads.get(unordered(curr, v)).getPheromone()))
                .forEach(queue::add);
        return queue;
    }

    private void updateRoads(List<Path> paths) {
        Map<EndpointPair<Vertex>, Road> roads = this.roads;
        int lMin = this.lMin;

        roads.values().forEach(road -> road.pheromoneEvaporation(p));
        for (Path path : paths) {
            double extraPheromone = path.getExtraPheromone(lMin);
            for (Road road : path.getRoads()) road.addPheromone(extraPheromone);
        }
    }

    private double calculateRandom() {
        Random random = new Random();
        int randomInt = random.nextInt(1001);
        return randomInt / 1000.0;
    }

    private double calculateProbability(Road road, double sum) {
        return road.calculateWish(alfa, beta) / sum;
    }

    private void initRoads() {
        this.roads = graph.edges().stream()
                .collect(Collectors.toMap(
                        identity(), endpointPair -> Road.from(
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