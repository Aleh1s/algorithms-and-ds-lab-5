package org.example.graph.algorithm.impl.ant;

import com.google.common.graph.EndpointPair;
import com.google.common.graph.MutableValueGraph;
import org.example.graph.Properties;
import org.example.graph.Vertex;
import org.example.graph.algorithm.RouteAlgorithm;
import org.example.graph.algorithm.impl.ant.exception.AntAlgorithmException;
import org.example.graph.parser.GraphParser;
import org.example.graph.parser.strategy.exception.GraphParserStrategyException;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

public class AntAlgorithm extends RouteAlgorithm {

    private final int alfa;
    private final int beta;
    private final int lMin;
    private final int numberOfAnts;
    private MutableValueGraph<Vertex, Integer> graph;
    private Map<EndpointPair<Vertex>, Properties> edgeProperties;

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
    public Stack<Vertex> buildRoute(Vertex start, Vertex end) throws AntAlgorithmException { // todo: start == end
        buildGraph();
        initEdgeProperties();
        for (int i = 0; i < 10; i++) {
            List<Stack<Vertex>> paths = new ArrayList<>(numberOfAnts);
            for (int j = 0; j < numberOfAnts; j++)
                paths.add(findPath(start, end));

            updateEdgeProperties(paths);
        }
        return getPath(start, end);
    }

    private Stack<Vertex> getPath(Vertex start, Vertex end) {
        Stack<Vertex> path = new Stack<>();
        Set<Vertex> visited = new HashSet<>();
        Vertex curr = path.push(start);
        visited.add(curr);
        while (true) {
            Vertex finalCurr = curr;
            Vertex vertex = getAvailable(curr, visited).stream()
                    .max(Comparator.comparing(v -> edgeProperties.get(EndpointPair.unordered(finalCurr, v)).getPheromone()))
                    .orElseThrow(() -> new IllegalStateException("Can not find the best adjacent vertex"));
            curr = path.push(vertex);
            visited.add(curr);

            if (curr.equals(end))
                return path;
        }
    }

    private void updateEdgeProperties(List<Stack<Vertex>> paths) {
        edgeProperties.values()
                .forEach(p -> p.setPheromone(p.getPheromone() * 0.64));
        for (Stack<Vertex> path : paths) {
            double extraPheromone = lMin / length(path);
            Vertex curr = path.pop(), next;
            while (!path.empty()) {
                next = path.pop();
                Properties p = edgeProperties.get(EndpointPair.unordered(curr, next));
                p.setPheromone(p.getPheromone() + extraPheromone);
                curr = next;
            }
        }
    }

    private double length(Stack<Vertex> path) {
        double sum = 0;
        Vertex curr = null;
        for (Vertex v : path) {
            if (isNull(curr)) {
                curr = v;
                continue;
            }

            sum += graph.edgeValue(EndpointPair.unordered(curr, v))
                    .orElseThrow(() -> new IllegalStateException("Edge value does not exists"));
            curr = v;
        }

        return sum;
    }

    private Stack<Vertex> findPath(Vertex start, Vertex end) {
        Stack<Vertex> path = new Stack<>();
        Set<Vertex> visited = new HashSet<>();
        Vertex curr = path.push(start);
        visited.add(curr);
        while (true) {
            Map<Vertex, Double> tp = countTransitionProbabilities(curr, visited);
            curr = path.push(makeStep(tp));
            visited.add(curr);

            if (curr.equals(end))
                return path;
        }
    }

    private Vertex makeStep(Map<Vertex, Double> tp) {
        double random = calculateRandom();
        Vertex last = null;
        for (Map.Entry<Vertex, Double> entry : tp.entrySet()) {
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

    private Map<Vertex, Double> countTransitionProbabilities(Vertex vertex, Set<Vertex> visited) {
        Set<Vertex> available = getAvailable(vertex, visited);
        Set<Properties> properties = getEdgeProperties(vertex, available);
        double sum = findSumOfWish(properties);
        return available.stream()
                .collect(Collectors.toMap(Function.identity(), v -> findProbability(
                        edgeProperties.get(EndpointPair.unordered(vertex, v)), sum)));
    }

    private Set<Vertex> getAvailable(Vertex vertex, Set<Vertex> visited) {
        return graph.adjacentNodes(vertex).stream()
                .filter(v -> !visited.contains(v))
                .collect(Collectors.toSet());
    }

    private Set<Properties> getEdgeProperties(Vertex vertex, Set<Vertex> adjacentVertices) {
        return adjacentVertices.stream()
                .map(v -> edgeProperties.get(EndpointPair.unordered(vertex, v)))
                .collect(Collectors.toSet());
    }

    private double findProbability(Properties properties, double sum) {
        return properties.calculateWish(alfa, beta) / sum;
    }

    private double findSumOfWish(Set<Properties> vertices) {
        return vertices.stream()
                .mapToDouble(prop -> prop.calculateWish(alfa, beta))
                .sum();
    }

    private void initEdgeProperties() {
        this.edgeProperties = graph.edges().stream()
                .collect(Collectors.toMap(
                        Function.identity(), endpointPair -> Properties.from(calculateProximity(getLength(endpointPair)))));
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