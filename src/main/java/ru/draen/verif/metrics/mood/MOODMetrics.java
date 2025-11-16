package ru.draen.verif.metrics.mood;

import ru.draen.verif.metrics.MetricResult;
import ru.draen.verif.metrics.mood.model.ProgramModel;

import java.util.List;

public class MOODMetrics {
    private final List<MOODFactor> factors = List.of(
            new MHFFactor(),
            new AHFFactor(),
            new MIFFactor(),
            new AIFFactor(),
            new POFFactor(),
            new COFFactor()
    );

    public List<MetricResult> process(ProgramModel model) {
        return factors.stream().map(factor -> factor.calculate(model)).toList();
    }
}
