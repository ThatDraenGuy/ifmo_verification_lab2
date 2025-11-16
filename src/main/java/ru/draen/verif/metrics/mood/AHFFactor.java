package ru.draen.verif.metrics.mood;

import ru.draen.verif.metrics.MetricResult;
import ru.draen.verif.metrics.mood.model.ProgramModel;

public class AHFFactor implements MOODFactor {
    @Override
    public MetricResult calculate(ProgramModel program) {
        long ah = 0;
        long ahv = 0;
        for (var clazz : program.classes()) {
            long ahi = clazz.attributes().stream()
                    .filter(attr -> !attr.visible() && !attr.inherited())
                    .count();
            long avi = clazz.attributes().stream()
                    .filter(attr -> attr.visible() && !attr.inherited())
                    .count();
            ah += ahi;
            ahv += ahi + avi;
        }
        double result = ((double) ah) / ahv;
        return new MetricResult("AHF", result);
    }
}
