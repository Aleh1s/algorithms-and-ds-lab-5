package org.example.graph.algorithm.impl.ant;

import com.google.common.graph.EndpointPair;
import com.google.common.graph.MutableValueGraph;
import org.example.Statistics;
import org.example.graph.Edge;
import org.example.graph.Vertex;
import org.example.graph.algorithm.Entry;
import org.example.graph.algorithm.Path;
import org.example.graph.algorithm.RouteAlgorithm;
import org.example.graph.algorithm.impl.ant.exception.AntAlgorithmException;
import org.example.graph.parser.GraphParser;
import org.example.graph.parser.strategy.exception.GraphParserStrategyException;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;
import static org.example.graph.algorithm.impl.ant.Indicator.FAILURE;
import static org.example.graph.algorithm.impl.ant.Indicator.RESULT;

public class AntAlgorithm extends RouteAlgorithm {
    private Vertex start;
    private Vertex terminal;
    private Statistics statistics;
    private MutableValueGraph<Vertex, Edge> graph;
    private final double p;
    private final int alfa;
    private final int beta;
    private final int lMin;
    private final int iterations;
    private final int numberOfAnts;
    public AntAlgorithm(
            int alfa,
            int beta,
            int lMin,
            int numberOfAnts,
            int iterations,
            double p,
            GraphParser parser) {
        super(parser);
        this.alfa = alfa;
        this.beta = beta;
        this.lMin = lMin;
        this.numberOfAnts = numberOfAnts;
        this.iterations = iterations;
        this.p = p;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Vertex> buildRoute(Vertex start, Vertex terminal) throws AntAlgorithmException {
        this.statistics = new Statistics();
        this.start = Objects.requireNonNull(start);
        this.terminal = Objects.requireNonNull(terminal);

        if (start.equals(terminal))
            return List.of(start);

        buildGraph();
        checkPoints();
        startToMove();

        Stack<Vertex> bestRoute = buildBestRoute();
        System.out.println("Length: " + Path.valueOf((Stack<Vertex>) bestRoute.clone(), graph).getLength());
        System.out.println(statistics);
        return bestRoute;
    }

    @Override
    public MutableValueGraph<Vertex, Edge> getGraph() {
        return graph;
    }

    private void buildGraph() throws AntAlgorithmException {
        try {
            this.graph = graphParser.parse();
        } catch (GraphParserStrategyException e) {
            throw new AntAlgorithmException(e);
        }
    }

    private void checkPoints() throws AntAlgorithmException {
        Set<Vertex> vertices = graph.nodes();

        String message = "Invalid %s point";

        if (!vertices.contains(start))
            throw new AntAlgorithmException(message.formatted("start"));

        if (!vertices.contains(terminal))
            throw new AntAlgorithmException(message.formatted("terminal"));
    }

    private void startToMove() {
        for (int i = 0; i < iterations; i++) updateEdges(findPaths());
    }

    private List<Path> findPaths() {
        List<Path> paths = new LinkedList<>();
        for (int i = 0; i < numberOfAnts; i++) paths.add(findPath());
        statistics.addDistances(paths.stream().map(Path::getLength).toList());
        return paths;
    }

    private Path findPath() {
        Stack<Vertex> path = new Stack<>();
        Indicator i = findPathRecursive(start, new HashSet<>(), path);

        if (i.equals(FAILURE))
            throw new IllegalStateException("Cannot find path");

        return Path.valueOf(path, graph);
    }

    private Indicator findPathRecursive(Vertex curr, HashSet<Vertex> visited, Stack<Vertex> path) {
        visited.add(curr);
        path.push(curr);

        if (curr.equals(terminal))
            return RESULT;

        Set<Vertex> adjacentVertices = getNotVisitedAdjacentVertices(curr, visited);

        if (adjacentVertices.isEmpty())
            return FAILURE;

        Map<Vertex, Double> transitionProbabilities
                = getTransitionProbabilities(curr, adjacentVertices);
        while (transitionProbabilities.size() > 0) {
            Indicator i = findPathRecursive(takeStep(transitionProbabilities), visited, path);

            if (i.equals(RESULT))
                return i;

            adjacentVertices.remove(path.pop());
            transitionProbabilities = getTransitionProbabilities(curr, adjacentVertices);
        }

        return FAILURE;
    }

    private Vertex takeStep(Map<Vertex, Double> transitionProbabilities) {
        double random = calculateRandomDouble();
        Vertex last = null;
        for (Map.Entry<Vertex, Double> e : transitionProbabilities.entrySet()) {
            random -= e.getValue();
            last = e.getKey();
            if (random <= 0)
                break;
        }
        return last;
    }

    private Set<Vertex> getNotVisitedAdjacentVertices(Vertex curr, Set<Vertex> visited) {
        return graph.adjacentNodes(curr).stream()
                .filter(v -> !visited.contains(v))
                .collect(Collectors.toSet());
    }

    private Map<Vertex, Double> getTransitionProbabilities(Vertex curr, Set<Vertex> adjacentVertices) {
        double sumOfWish = getSumOfWish(curr, adjacentVertices);
        return adjacentVertices.stream()
                .collect(Collectors.toMap(
                        identity(), vertex -> calculateProbability(getEdge(curr, vertex), sumOfWish)));
    }

    public double getSumOfWish(Vertex curr, Set<Vertex> adjacentVertices) {
        return adjacentVertices.stream()
                .map(vertex -> getEdge(curr, vertex))
                .mapToDouble(e -> {
                    double wish = e.calculateWish(alfa, beta);
                    statistics.addWish(wish);
                    return wish;
                })
                .sum();
    }

    private Stack<Vertex> buildBestRoute() {
        Stack<Vertex> path = new Stack<>();
        Indicator i = buildBestRouteRecursive(start, new HashSet<>(), path);

        if (i.equals(FAILURE))
            throw new IllegalStateException("Cannot find path");

        return path;
    }

    private Indicator buildBestRouteRecursive(Vertex curr, Set<Vertex> visited, Stack<Vertex> path) {
        visited.add(curr);
        path.push(curr);

        if (curr.equals(terminal))
            return RESULT;

        Set<Vertex> adjacentVertices = getNotVisitedAdjacentVertices(curr, visited);

        if (adjacentVertices.isEmpty())
            return FAILURE;

        PriorityQueue<Entry> pheromoneQueue = getPheromoneQueue(curr, adjacentVertices);
        while (!pheromoneQueue.isEmpty()) {
            Entry entry = pheromoneQueue.poll();
            Indicator i = buildBestRouteRecursive(entry.getVertex(), visited, path);

            if (i.equals(RESULT))
                return i;

            path.pop();
        }

        return FAILURE;
    }

    private PriorityQueue<Entry> getPheromoneQueue(Vertex curr, Set<Vertex> adjacentVertices) {
        PriorityQueue<Entry> queue = new PriorityQueue<>();
        adjacentVertices.stream()
                .map(vertex -> {
                    Edge edge = getEdge(curr, vertex);
                    return Entry.from(vertex, edge.getPheromone(), edge.getDistance());
                }).forEach(queue::add);
        return queue;
    }

    private void updateEdges(List<Path> paths) {
        evaporatePheromone();
        for (Path path : paths) {
            double extraPheromone = path.getExtraPheromone(lMin);
            statistics.addExtraPheromone(extraPheromone);
            for (EndpointPair<Vertex> edge : path.getEdges()) {
                Edge e = getEdge(edge.nodeV(), edge.nodeU());
                e.addPheromone(extraPheromone);
            }
        }
    }

    private void evaporatePheromone() {
        graph.edges().stream()
                .map(e -> getEdge(e.nodeV(), e.nodeU()))
                .forEach(e -> e.evaporatePheromone(p));
    }

    private double calculateRandomDouble() {
        Random random = new Random();
        int randomInt = random.nextInt(1001);
        return randomInt / 1000.0;
    }

    private double calculateProbability(Edge edge, double sum) {
        return edge.calculateWish(alfa, beta) / sum;
    }

    private Edge getEdge(Vertex v1, Vertex v2) {
        return graph.edgeValue(v1, v2).orElseThrow();
    }
}