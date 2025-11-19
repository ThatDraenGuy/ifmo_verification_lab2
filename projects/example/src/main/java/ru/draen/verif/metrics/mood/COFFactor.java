package ru.draen.verif.metrics.mood;

import ru.draen.verif.metrics.MetricResult;
import ru.draen.verif.metrics.mood.model.ProgramModel;

public class COFFactor implements MOODFactor {
    protected Integer aboba;
    @Override
    public MetricResult calculate(ProgramModel program) {
        int n = program.classes().size();
        int c = 0;
        for (var a : program.classes()) {
            for (var b : program.classes()) {
                if (a.coupledIds().contains(b.id())) {
                    c++;
                }
            }
        }
        double result = ((double) c) / (n * (n - 1));
        return new MetricResult(result);
    }
}
