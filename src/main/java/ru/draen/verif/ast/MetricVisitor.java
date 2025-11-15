package ru.draen.verif.ast;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.GenericVisitorWithDefaults;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MetricVisitor extends GenericVisitorWithDefaults<Void, Void> {
    private final MetricRegistry registry;

    @Override
    public Void defaultAction(Node n, Void arg) {
        n.getChildNodes().forEach(node -> node.accept(this, arg));
        return null;
    }

    @Override
    public Void visit(ClassOrInterfaceDeclaration n, Void arg) {
        return super.visit(n, arg);
    }
}
