package com.samwolfson.pde2js.visitors;

import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/**
 * Replace calls to `String.length()` with `String.length`
 * TODO: right now this replaces ALL .length() calls with .length (regardless of variable type) -- possible to fix?
 * This turns out to be a more complicated problem than it seems because JavaParser does not store type information
 * when traversing the AST. There is a related project, "JavaSymbolSolver," which might help, but is poorly documented.
 */
public class StringLengthVisitor extends VoidVisitorAdapter<Void> {

    @Override
    public void visit(MethodCallExpr n, Void arg) {
        super.visit(n, arg);
        if (n.getScope().isPresent() && n.getNameAsString().equals("length")) {
            n.replace(new FieldAccessExpr(n.getScope().get(), "length"));
        }
    }
}
