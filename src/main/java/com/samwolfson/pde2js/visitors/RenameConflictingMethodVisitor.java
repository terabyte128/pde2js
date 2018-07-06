package com.samwolfson.pde2js.visitors;

import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.samwolfson.pde2js.ProcessingToP5Converter;

import java.util.regex.Pattern;

/**
 * When the pde file was parsed, method names that conflicted with data types were
 * renamed to prevent the parser from complaining. This visitor gives them back their
 * original names.
 */
public class RenameConflictingMethodVisitor extends VoidVisitorAdapter<Void> {
    private static Pattern renamedMethodRegex = Pattern.compile(ProcessingToP5Converter.DATA_TYPE_CONFLICT_PREFIX
            + ProcessingToP5Converter.CONFLICTING_DATA_TYPE_REGEX);

    @Override
    public void visit(MethodCallExpr n, Void arg) {
        super.visit(n, arg);
        n.setName( renamedMethodRegex.matcher(n.getNameAsString()).replaceAll("$1") );
    }
}
