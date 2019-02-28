package com.samwolfson.pde2js.visitors;

import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.*;

/**
 * Replace casts from anything in CASTING_TYPES with equivalent function calls.
 * JS doesn't have a notion of "casting" since it's dynamically typed, but p5.js provides
 * int(..), float(..), etc., functions just like Processing does.
 */

public class CastingToFunctionCallVisitor extends VoidVisitorAdapter<Void> {

    private static final String[] CASTING_TYPES = { "float", "int", "boolean", "byte", "char" };

    @Override
    public void visit(CastExpr n, Void arg) {
        super.visit(n, arg);

        if (Arrays.asList(CASTING_TYPES).contains(n.getTypeAsString())) {
            n.replace(new MethodCallExpr(n.getTypeAsString(), n.getExpression()));
        }
    }
}
