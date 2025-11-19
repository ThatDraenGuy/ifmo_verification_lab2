package ru.draen.verif.metrics.mood;

import ru.draen.verif.metrics.MetricResult;
import ru.draen.verif.metrics.mood.model.ProgramModel;

public class MIFFactor implements MOODFactor {
    @Override
    public MetricResult calculate(ProgramModel program) {
        long mi = 0;
        long mnio = 0;
        for (var clazz : program.classes()) {
            long mii = clazz.methods().stream()
                    .filter(method -> method.inherited() && !method.overridden())
                    .count();
            long mni = clazz.methods().stream()
                    .filter(method -> !method.inherited())
                    .count();
            long moi = clazz.methods().stream()
                    .filter(method -> method.inherited() && method.overridden())
                    .count();
            mi += mii;
            mnio += mii + mni + moi;
        }
        double result = ((double) mi) / mnio;
        return new MetricResult(result);
    }
}
