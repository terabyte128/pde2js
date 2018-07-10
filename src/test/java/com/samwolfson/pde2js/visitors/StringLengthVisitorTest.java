package com.samwolfson.pde2js.visitors;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.junit.Assert;
import org.junit.Test;

public class StringLengthVisitorTest {
    private static final String TEST_CODE = "" +
            "public class Test {" +
            "String s = \"hello, world!\";" +
            "int x;" +
            "void setup() {" +
            "x = s.length();" +
            "} }";

    @Test
    public void shouldRenameMethodCallToFieldAccess() {
        CompilationUnit cu = JavaParser.parse(TEST_CODE);
        cu.accept(new StringLengthVisitor(), null);

        BlockStmt setupBody = cu.getClassByName("Test").get().getMethods().get(0).getBody().get();
        Assert.assertEquals("length",
                setupBody.getStatements().get(0).asExpressionStmt()
                        .getExpression().asAssignExpr().getValue().asFieldAccessExpr().getNameAsString());
    }
}
