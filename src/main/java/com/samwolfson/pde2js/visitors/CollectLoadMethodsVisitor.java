package com.samwolfson.pde2js.visitors;

import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Collect all of the method calls that load from a file.
 */
public class CollectLoadMethodsVisitor extends VoidVisitorAdapter<Void> {
    private List<Expression> loadMethodCalls = new ArrayList<>();
    private static final String METHOD_NAME_REGEX = "load(Model|Image|JSON|Strings|Table|XML|Bytes|Font|Shader)";

    public List<Expression> getLoadMethodCalls() {
        return Collections.unmodifiableList(loadMethodCalls);
    }

    @Override
    public void visit(AssignExpr n, Void arg) {
        super.visit(n, arg);

        if (n.getValue().isMethodCallExpr()) {
            MethodCallExpr callExpr = n.getValue().asMethodCallExpr();
            if (callExpr.getNameAsString().matches(METHOD_NAME_REGEX)) {
                loadMethodCalls.add(n);
            }
        }
    }
}
