package ru.draen.verif.ast.mood;

import com.github.javaparser.ast.AccessSpecifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.RecordDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.GenericVisitorWithDefaults;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserMethodDeclaration;
import lombok.AllArgsConstructor;
import ru.draen.verif.metrics.mood.model.AttributeModel;
import ru.draen.verif.metrics.mood.model.MethodModel;

import java.util.HashSet;

@AllArgsConstructor
public class MOODMetricVisitor extends GenericVisitorWithDefaults<Void, VisitorContext> {
    private final MetricRegistry registry;

    @Override
    public Void defaultAction(Node n, VisitorContext ctx) {
        n.getChildNodes().forEach(node -> node.accept(this, ctx));
        return null;
    }

    private void registerClassMethods(ResolvedReferenceTypeDeclaration resolvedClass) {
        var allMethods = resolvedClass.getAllMethods(); // inherited + declared (no overwritten)
        var declaredMethods = resolvedClass.getDeclaredMethods(); // declared (has overwritten)

        var inheritedMethods = new HashSet<>(allMethods);
        inheritedMethods.removeIf(method -> declaredMethods.stream()
                .anyMatch(declaredMethod -> declaredMethod.getSignature().equals(method.getSignature())));

        inheritedMethods.forEach(method -> {
            var declaration = method.declaringType().getDeclaredMethods().stream()
                    .filter(methodDecl -> methodDecl.getSignature().equals(method.getSignature()))
                    .findFirst().orElseThrow();
            boolean isVisible = declaration.accessSpecifier().equals(AccessSpecifier.PUBLIC);
            boolean isInherited = true;
            boolean isOverridden = false;
            registry.addMethod(resolvedClass.getQualifiedName(), new MethodModel(
                    method.getQualifiedSignature(),
                    isVisible,
                    isInherited,
                    isOverridden
            ));
        });
        declaredMethods.forEach(declaration -> {
            boolean isOverridden;
            if (declaration instanceof JavaParserMethodDeclaration javaDecl) {
                var actualDecl = (MethodDeclaration) javaDecl.toAst().get();
                isOverridden = actualDecl.getAnnotationByClass(Override.class).isPresent();
            } else {
                isOverridden = false;
            }
            boolean isVisible = declaration.accessSpecifier().equals(AccessSpecifier.PUBLIC);
            boolean isInherited = false;
            registry.addMethod(resolvedClass.getQualifiedName(), new MethodModel(
                    declaration.getQualifiedSignature(),
                    isVisible,
                    isInherited,
                    isOverridden
            ));
        });
    }

    private void registerClassAttrs(ResolvedReferenceTypeDeclaration resolvedClass) {
        var allFields = resolvedClass.getAllFields(); // inherited + declared
        var declaredFields = resolvedClass.getDeclaredFields(); // declared

        var inheritedFields = new HashSet<>(allFields);
        inheritedFields.removeIf(field ->
                field.declaringType().getQualifiedName().equals(resolvedClass.getQualifiedName()));

        inheritedFields.forEach(field -> {
            boolean isVisible = field.accessSpecifier().equals(AccessSpecifier.PUBLIC);
            boolean isInherited = true;
            registry.addAttr(resolvedClass.getQualifiedName(), new AttributeModel(
                    field.getName(),
                    isVisible,
                    isInherited
            ));
        });
        declaredFields.forEach(field -> {
            boolean isVisible = field.accessSpecifier().equals(AccessSpecifier.PUBLIC);
            boolean isInherited = false;
            registry.addAttr(resolvedClass.getQualifiedName(), new AttributeModel(
                    field.getName(),
                    isVisible,
                    isInherited
            ));
        });
    }

    private Void handleClassDecl(Node n, ResolvedReferenceTypeDeclaration resolvedClass, VisitorContext ctx) {
        registry.addClass(resolvedClass.getQualifiedName());
        ctx.enterClass(resolvedClass.getQualifiedName());

        var ancestors = resolvedClass.getAncestors(true);
        ancestors.forEach(ancestor -> {
            registry.addInheritance(ancestor.getQualifiedName(), resolvedClass.getQualifiedName());
        });

        registerClassMethods(resolvedClass);
        registerClassAttrs(resolvedClass);

        n.getChildNodes().forEach(node -> node.accept(this, ctx));
        ctx.exitClass();
        return null;
    }
    @Override
    public Void visit(ClassOrInterfaceDeclaration n, VisitorContext ctx) {
        if (n.isInterface()) {
            return null;
        }
        return handleClassDecl(n, n.resolve(), ctx);
    }

    @Override
    public Void visit(RecordDeclaration n, VisitorContext ctx) {
        return handleClassDecl(n, n.resolve(), ctx);
    }

    @Override
    public Void visit(MethodCallExpr n, VisitorContext ctx) {
        String methodSource = n.resolve().declaringType().getQualifiedName();
        if (!methodSource.equals(ctx.currentClass())) {
            registry.addCoupling(ctx.currentClass(), methodSource);
        }

        return null;
    }

    @Override
    public Void visit(ClassOrInterfaceType n, VisitorContext ctx) {
        if (n.resolve() instanceof ResolvedReferenceType actualType
                && !actualType.getQualifiedName().equals(ctx.currentClass())) {
            registry.addCoupling(ctx.currentClass(), actualType.getQualifiedName());
        }

        return null;
    }
}
