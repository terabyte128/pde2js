package com.samwolfson.pde2js.visitors;

import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class SizeToCreateCanvasVisitor extends VoidVisitorAdapter<Void> {
    @Override
    public void visit(MethodCallExpr n, Void arg) {
        super.visit(n, arg);
        if (n.getNameAsString().equals("size")) {
            n.setName("createCanvas");
        }
    }
}
