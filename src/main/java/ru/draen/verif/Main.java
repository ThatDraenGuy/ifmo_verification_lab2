package ru.draen.verif;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.github.javaparser.utils.SourceRoot;
import lombok.SneakyThrows;
import ru.draen.verif.ast.mood.MOODMetricVisitor;
import ru.draen.verif.ast.mood.MetricRegistry;
import ru.draen.verif.ast.mood.VisitorContext;
import ru.draen.verif.metrics.MetricResult;
import ru.draen.verif.metrics.mood.MOODMetrics;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length > 0) {
            var projPath = Paths.get(args[0]);
            processProject(projPath);
        } else {
            var projDir = Paths.get("projects");
            try (var paths = Files.list(projDir)) {
                paths.filter(Files::isDirectory)
                        .filter(path -> !path.equals(projDir))
                        .forEach(Main::processProject);
            }
        }

    }

    @SneakyThrows
    private static void processProject(Path projPath) {
        ParserConfiguration configuration = new ParserConfiguration();

        CombinedTypeSolver typeSolver = new CombinedTypeSolver();
        typeSolver.add(new ReflectionTypeSolver());
        Path srcDir = projPath.resolve("src").resolve("main").resolve("java");
        typeSolver.add(new JavaParserTypeSolver(srcDir, configuration));
        Path libsDir = projPath.resolve("libs");
        try (var libs = Files.list(libsDir)) {
            for (var lib : libs.toList()) {
                typeSolver.add(new JarTypeSolver(lib));
            }
        }

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
        var table = displayAsTable(metricResults);
        System.out.println(projPath.getFileName().toString());
        System.out.println(table);
        try (var writer = new FileWriter(new File("results", projPath.getFileName().toString() + ".txt"));) {
            writer.append(table);
            writer.flush();
        }
    }

    private static String displayAsTable(List<MetricResult> metrics) {
        List<Integer> widths = new ArrayList<>();
        List<String> values = new ArrayList<>();
        for (var metric : metrics) {
            String value = String.format("%.2f%%", metric.fraction() * 100);
            values.add(value);
            widths.add(Math.max(value.length(), metric.name().length()));
        }

        StringBuilder sb = new StringBuilder();
        for (var width : widths) {
            sb.append("+").append("-".repeat(width + 2));
        }
        sb.append("+'\n");

        for (int i = 0; i < metrics.size(); i++) {
            var metric = metrics.get(i);
            var width = widths.get(i);
            sb.append("| ").append(String.format("%1$-" + (width + 1) + "s", metric.name()));
        }
        sb.append("|\n");

        for (var width : widths) {
            sb.append("+").append("-".repeat(width + 2));
        }
        sb.append("+\n");

        for (int i = 0; i < metrics.size(); i++) {
            var value = values.get(i);
            var width = widths.get(i);
            sb.append("| ").append(String.format("%1$-" + (width + 1) + "s", value));
        }
        sb.append("|\n");

        for (var width : widths) {
            sb.append("+").append("-".repeat(width + 2));
        }
        sb.append("+\n");

        return sb.toString();
    }
}