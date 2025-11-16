package ru.draen.verif.metrics;

public record MetricResult(
        String name,
        double fraction
) {
    @Override
    public String toString() {
        return name + ": " + fraction;
    }
}
