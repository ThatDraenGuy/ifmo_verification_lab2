package ru.draen.verif;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.github.javaparser.utils.SourceRoot;
import ru.draen.verif.ast.mood.MOODMetricVisitor;
import ru.draen.verif.ast.mood.MetricRegistry;
import ru.draen.verif.ast.mood.VisitorContext;
import ru.draen.verif.metrics.mood.MOODMetrics;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws IOException {
        ParserConfiguration configuration = new ParserConfiguration();

        CombinedTypeSolver typeSolver = new CombinedTypeSolver();
        typeSolver.add(new ReflectionTypeSolver()); // Add standard library types
        Path srcDir = Paths.get("projects/example/src");
        typeSolver.add(new JavaParserTypeSolver(srcDir, configuration)); // Add your project's source code


        configuration.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_21);
        configuration.setSymbolResolver(new JavaSymbolSolver(typeSolver));
        SourceRoot sourceRoot = new SourceRoot(srcDir, configuration);
        var results = sourceRoot.tryToParse();

        MetricRegistry registry = new MetricRegistry();
        for (var res : results) {
            MOODMetricVisitor visitor = new MOODMetricVisitor(registry);
            res.getResult().get().accept(visitor, new VisitorContext());
        }
        MOODMetrics metrics = new MOODMetrics();
        var metricResults = metrics.process(registry.construct());
        System.out.println(metricResults);
    }
}