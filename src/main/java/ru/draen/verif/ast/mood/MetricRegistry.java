package ru.draen.verif.ast.mood;

import ru.draen.verif.metrics.mood.model.AttributeModel;
import ru.draen.verif.metrics.mood.model.ClassModel;
import ru.draen.verif.metrics.mood.model.MethodModel;
import ru.draen.verif.metrics.mood.model.ProgramModel;

import java.util.*;

public class MetricRegistry {
    private final Set<String> classes = new HashSet<>();
    private final Map<String, List<MethodModel>> methodsByClass = new HashMap<>();
    private final Map<String, List<AttributeModel>> attrsByClass = new HashMap<>();
    private final Map<String, List<String>> childrenByClass = new HashMap<>();
    private final Map<String, Set<String>> coupledByClass = new HashMap<>();

    private boolean isNonProject(String identifier) {
        return identifier.startsWith("java.");
    }

    public void addClass(String className) {
        classes.add(className);
    }

    public void addInheritance(String parent, String child) {
        if (isNonProject(parent)) return;
        childrenByClass.computeIfAbsent(parent, k -> new ArrayList<>()).add(child);
    }

    public void addCoupling(String from, String to) {
        if (isNonProject(to)) return;
        coupledByClass.computeIfAbsent(from, k -> new HashSet<>()).add(to);
    }

    public void addMethod(String className, MethodModel methodModel) {
        if (isNonProject(methodModel.name())) return;
        methodsByClass.computeIfAbsent(className, k -> new ArrayList<>()).add(methodModel);
    }

    public void addAttr(String className, AttributeModel attrModel) {
        attrsByClass.computeIfAbsent(className, k -> new ArrayList<>()).add(attrModel);
    }

    public ProgramModel construct() {
        var classModels = classes.stream().map(className -> new ClassModel(
                className,
                methodsByClass.getOrDefault(className, List.of()),
                attrsByClass.getOrDefault(className, List.of()),
                childrenByClass.getOrDefault(className, List.of()).size(),
                coupledByClass.getOrDefault(className, Set.of())
        )).toList();
        return new ProgramModel(classModels);
    }
}
