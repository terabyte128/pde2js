package com.samwolfson.pde2js.visitors;

import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.Arrays;
import java.util.Collections;

/**
 * Renames key variables:
 * UP -> UP_ARROW
 * LEFT -> LEFT_ARROW
 * RIGHT -> RIGHT_ARROW
 * DOWN -> DOWN_ARROW
 *
 * and removes checks for 'KEY == CODED'
 */
public class RenameKeyPressedVisitor extends VoidVisitorAdapter<Void> {

    private String[] keyNames = { "UP", "LEFT", "RIGHT", "DOWN" };

    @Override
    public void visit(NameExpr n, Void arg) {
        super.visit(n, arg);
        if (Arrays.asList(keyNames).contains(n.getNameAsString())) {
            n.setName(n.getNameAsString() + "_ARROW");
        }
    }

    /**
     * Remove key == CODED checks.
     * TODO: currently this replaces those checks with "if (true)" -- is there a better way?
     * @param n
     * @param arg
     */
    @Override
    public void visit(BinaryExpr n, Void arg) {
        super.visit(n, arg);
        if (n.getOperator().equals(BinaryExpr.Operator.EQUALS)) {
            if (n.getLeft().isNameExpr() && n.getRight().isNameExpr()) {

                if (
                        (n.getLeft().asNameExpr().getNameAsString().equals("key")
                        && n.getRight().asNameExpr().getNameAsString().equals("CODED"))

                        || (n.getLeft().asNameExpr().getNameAsString().equals("CODED")
                        && n.getRight().asNameExpr().getNameAsString().equals("key"))) {

                    n.replace(new BooleanLiteralExpr(true));
                }
            }
        }

    }
}
