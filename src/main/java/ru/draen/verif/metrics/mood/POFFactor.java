package ru.draen.verif.metrics.mood;

import ru.draen.verif.metrics.MetricResult;
import ru.draen.verif.metrics.mood.model.MethodModel;
import ru.draen.verif.metrics.mood.model.ProgramModel;

public class POFFactor implements MOODFactor {
    @Override
    public MetricResult calculate(ProgramModel program) {
        long mo = 0;
        long mnd = 0;
        for (var clazz : program.classes()) {
            long mdi = clazz.methods().stream()
                    .filter(method -> !method.inherited() && !method.overridden())
                    .count();
            long moi = clazz.methods().stream()
                    .filter(MethodModel::overridden)
                    .count();
            int di = clazz.inheritorsCount();
            mo += moi;
            mnd += mdi * di;
        }
        double result = ((double) mo) / mnd;
        return new MetricResult("POF", result);
    }
}
