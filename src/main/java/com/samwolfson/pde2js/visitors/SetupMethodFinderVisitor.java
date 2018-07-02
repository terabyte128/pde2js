package com.samwolfson.pde2js.visitors;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.Optional;

/**
 * Discover the setup() method in a class passed to the visitor.
 */
public class SetupMethodFinderVisitor extends VoidVisitorAdapter<Void> {
    private MethodDeclaration setupMethod;

    public Optional<MethodDeclaration> getSetupMethod() {
        return Optional.ofNullable(setupMethod);
    }

    @Override
    public void visit(MethodDeclaration n, Void arg) {
        if (n.getNameAsString().equals("setup")) {
            this.setupMethod = n;
        }
    }
}
