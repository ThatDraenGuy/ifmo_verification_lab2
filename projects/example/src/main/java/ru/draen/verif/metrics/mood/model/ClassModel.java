package ru.draen.verif.metrics.mood.model;

import java.util.List;
import java.util.Set;

public record ClassModel(
        String id,
        List<MethodModel> methods,
        int inheritorsCount,
        Set<String> coupledIds
) {
}
