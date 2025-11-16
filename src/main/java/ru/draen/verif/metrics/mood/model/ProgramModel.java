package ru.draen.verif.metrics.mood.model;

import java.util.Collections;
import java.util.List;

public record ProgramModel(
        List<ClassModel> classes
) {
    public ProgramModel {
        classes = Collections.unmodifiableList(classes);
    }
}
