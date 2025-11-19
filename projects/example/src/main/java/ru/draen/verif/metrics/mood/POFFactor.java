package ru.draen.verif.metrics.mood;

import ru.draen.verif.metrics.MetricResult;
import ru.draen.verif.metrics.mood.model.ProgramModel;

public class POFFactor implements MOODFactor {
    @Override
    public MetricResult calculate(ProgramModel program) {
        long mo = 0;
        long mnd = 0;
        for (var clazz : program.classes()) {
            long mni = clazz.methods().stream()
                    .filter(method -> !method.inherited())
                    .count();
            long moi = clazz.methods().stream()
                    .filter(method -> method.inherited() && method.overridden())
                    .count();
            int di = clazz.inheritorsCount();
            mo += moi;
            mnd += mni * di;
        }
        double result = ((double) mo) / mnd;
        return new MetricResult(result);
    }
}
