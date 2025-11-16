package ru.draen.verif.metrics.mood;

import ru.draen.verif.metrics.MetricResult;
import ru.draen.verif.metrics.mood.model.AttributeModel;
import ru.draen.verif.metrics.mood.model.MethodModel;
import ru.draen.verif.metrics.mood.model.ProgramModel;

public class AIFFactor implements MOODFactor {
    @Override
    public MetricResult calculate(ProgramModel program) {
        long ai = 0;
        long anio = 0;
        for (var clazz : program.classes()) {
            long aii = clazz.attributes().stream()
                    .filter(AttributeModel::inherited)
                    .count();
            long ani = clazz.attributes().stream()
                    .filter(attr -> !attr.inherited())
                    .count();
            ai += aii;
            anio += aii + ani;
        }
        double result = ((double) ai) / anio;
        return new MetricResult("AIF", result);
    }
}
