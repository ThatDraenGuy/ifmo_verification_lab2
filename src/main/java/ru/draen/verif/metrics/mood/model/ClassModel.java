package ru.draen.verif.metrics.mood.model;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public record ClassModel(
        String id,
        List<MethodModel> methods,
        List<AttributeModel> attributes,
        int inheritorsCount,
        Set<String> coupledIds
) {
    public ClassModel {
        methods = Collections.unmodifiableList(methods);
        attributes = Collections.unmodifiableList(attributes);
        coupledIds = Collections.unmodifiableSet(coupledIds);
    }
}
