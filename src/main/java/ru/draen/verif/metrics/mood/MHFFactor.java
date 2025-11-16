package ru.draen.verif.metrics.mood;

import ru.draen.verif.metrics.MetricResult;
import ru.draen.verif.metrics.mood.model.ProgramModel;

public class MHFFactor implements MOODFactor {
    @Override
    public MetricResult calculate(ProgramModel program) {
        long mh = 0;
        long mhv = 0;
        for (var clazz : program.classes()) {
            long mhi = clazz.methods().stream()
                    .filter(method -> !method.visible() && !method.inherited())
                    .count();
            long mvi = clazz.methods().stream()
                    .filter(method -> method.visible() && !method.inherited())
                    .count();
            mh += mhi;
            mhv += mhi + mvi;
        }
        double result = ((double) mh) / mhv;
        return new MetricResult("MHF", result);
    }
}
