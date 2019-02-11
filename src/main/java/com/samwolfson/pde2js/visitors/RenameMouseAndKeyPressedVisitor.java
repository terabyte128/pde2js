package com.samwolfson.pde2js.visitors;

import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/**
 * Replace the variable <code>mousePressed</code> with <code>mouseIsPressed</code>
 * and <code>keyPressed</code> with <code>keyIsPressed</code>
 * (In p5, it is named differently from the function).
 */
public class RenameMouseAndKeyPressedVisitor extends VoidVisitorAdapter<Void> {

    @Override
    public void visit(NameExpr n, Void arg) {
        super.visit(n, arg);

        if (n.getNameAsString().equals("mousePressed")) {
            n.setName("mouseIsPressed");
        } else if (n.getNameAsString().equals("keyPressed")) {
            n.setName("keyIsPressed");
        }
    }
}
