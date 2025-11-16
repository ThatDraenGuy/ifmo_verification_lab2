package ru.draen.verif.ast.mood;

import java.util.Deque;
import java.util.LinkedList;

public class VisitorContext {
    private final Deque<String> classStack = new LinkedList<>();
    public void enterClass(String className) {
        classStack.add(className);
    }
    public String currentClass() {
        return classStack.peek();
    }
    public void exitClass() {
        classStack.pop();
    }
}
