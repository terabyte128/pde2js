package com.samwolfson.pde2js.visitors;

import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/**
 * Replace calls to `String.length()` with `String.length`
 * Also replace `s1.equals(s2)` with `s1 == s2`
 * TODO: right now this replaces ALL .length() calls with .length (regardless of variable type) -- possible to fix?
 * This turns out to be a more complicated problem than it seems because JavaParser does not store type information
 * when traversing the AST. There is a related project, "JavaSymbolSolver," which might help, but is poorly documented.
 */
public class StringLengthAndEqualsVisitor extends VoidVisitorAdapter<Void> {

    @Override
    public void visit(MethodCallExpr n, Void arg) {
        super.visit(n, arg);
        if (n.getScope().isPresent() && n.getNameAsString().equals("length")) {
            n.replace(new FieldAccessExpr(n.getScope().get(), "length"));
        } else if(n.getScope().isPresent() && n.getNameAsString().equals("equals") && n.getArguments().size() == 1) {
            n.replace(new BinaryExpr(n.getScope().get().asNameExpr(), n.getArgument(0).asNameExpr(), BinaryExpr.Operator.EQUALS));
        }
    }
}
