package ru.draen.verif.metrics.mood.model;

public record AttributeModel(
        String name,
        boolean visible, // видимый метод (public)
        boolean inherited // унаследованный метод
) {
}
