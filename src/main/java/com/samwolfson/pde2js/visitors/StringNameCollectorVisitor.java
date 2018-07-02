package com.samwolfson.pde2js.visitors;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Look through all the variable declarations and find the ones that declare Strings.
 * Collect their names for use when replacing `length()` with `length`.
 */
public class StringNameCollectorVisitor extends VoidVisitorAdapter<Void> {
    private List<String> stringNames = new ArrayList<>();
    private List<String> stringArrayNames = new ArrayList<>();

    public List<String> getStringNames() {
        return Collections.unmodifiableList(stringNames);
    }

    public List<String> getStringArrayNames() {
        return Collections.unmodifiableList(stringArrayNames);
    }

    @Override
    public void visit(VariableDeclarationExpr n, Void arg) {
        collectStrings(n.getVariables());
    }

    @Override
    public void visit(FieldDeclaration n, Void arg) {
        collectStrings(n.getVariables());
    }

    private void collectStrings(NodeList<VariableDeclarator> variables) {
        variables.forEach(v -> {
            if (v.getType().asString().equals("String")) {
                stringNames.add(v.getNameAsString());
            } else if (v.getType().asString().equals("String[]")) {
                stringArrayNames.add(v.getNameAsString());
            }
        });
    }
}
