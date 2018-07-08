package com.samwolfson.pde2js.visitors;

import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/**
 * Replace calls to <code>println()</code> with <code>print()</code>
 */
public class PrintlnToPrintVisitor extends VoidVisitorAdapter<Void> {

    @Override
    public void visit(MethodCallExpr n, Void arg) {
        super.visit(n, arg);

        if (n.getNameAsString().equals("println")) {
            n.setName("print");
        }
    }
}
