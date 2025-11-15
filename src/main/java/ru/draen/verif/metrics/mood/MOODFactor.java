package ru.draen.verif.metrics.mood;

import ru.draen.verif.metrics.MetricResult;
import ru.draen.verif.metrics.mood.model.ProgramModel;

public interface MOODFactor {
    MetricResult calculate(ProgramModel program);
}
