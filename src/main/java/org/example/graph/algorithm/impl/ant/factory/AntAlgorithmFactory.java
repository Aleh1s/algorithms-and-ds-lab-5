package org.example.graph.algorithm.impl.ant.factory;

import org.example.graph.algorithm.RouteAlgorithm;
import org.example.graph.algorithm.factory.RouteAlgorithmFactory;
import org.example.graph.algorithm.impl.ant.AntAlgorithm;
import org.example.graph.algorithm.impl.ant.exception.AntAlgorithmFactoryException;
import org.example.graph.parser.GraphParser;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import static org.example.Constants.*;

public class AntAlgorithmFactory implements RouteAlgorithmFactory {

    private GraphParser graphParser;
    private AntAlgorithmFactory() {}

    public static AntAlgorithmFactory newInstance() {
        return new AntAlgorithmFactory();
    }

    @Override
    public RouteAlgorithm createRouteAlgorithm() throws AntAlgorithmFactoryException {
        Properties props = new Properties();
        AntAlgorithm algorithm;
        try {
            props.load(new FileReader(APP_PROPS_FILE_NAME));
            algorithm = new AntAlgorithm(
                    Integer.parseInt(props.getProperty(BETA)),
                    Integer.parseInt(props.getProperty(ALFA)),
                    Integer.parseInt(props.getProperty(L_MIN)),
                    Integer.parseInt(props.getProperty(NUMBER_OF_ANTS)),
                    Integer.parseInt(props.getProperty(ITERATIONS)),
                    Double.parseDouble(props.getProperty(P)),
                    graphParser);
        } catch (IOException e) {
            throw new AntAlgorithmFactoryException(e);
        }
        return algorithm;
    }

    @Override
    public void setGraphParser(GraphParser graphParser) {
        this.graphParser = graphParser;
    }
}
