package ru.draen.verif.metrics.mood.model;

public record MethodModel(
        String name,
        boolean visible, // видимый метод (public)
        boolean inherited, // унаследованный метод
        boolean overridden // переопределённый метод
) {
}
