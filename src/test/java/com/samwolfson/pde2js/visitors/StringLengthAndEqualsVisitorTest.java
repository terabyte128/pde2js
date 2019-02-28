package com.samwolfson.pde2js.visitors;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.junit.Assert;
import org.junit.Test;

public class StringLengthAndEqualsVisitorTest {
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
        cu.accept(new StringLengthAndEqualsVisitor(), null);

        BlockStmt setupBody = cu.getClassByName("Test").get().getMethods().get(0).getBody().get();
        Assert.assertEquals("length",
                setupBody.getStatements().get(0).asExpressionStmt()
                        .getExpression().asAssignExpr().getValue().asFieldAccessExpr().getNameAsString());
    }

    private static final String TEST_CODE_2 = "" +
            "public class Test {" +
            "String s = \"hello, world!\";" +
            "String s2 = \"boop\";" +
            "boolean x;" +
            "void setup() {" +
            "x = s.equals(s2);" +
            "} }";

    @Test
    public void shouldReplaceEqualsWithBinaryEquals() {
        CompilationUnit cu = JavaParser.parse(TEST_CODE_2);
        cu.accept(new StringLengthAndEqualsVisitor(), null);

        BlockStmt setupBody = cu.getClassByName("Test").get().getMethods().get(0).getBody().get();
        Assert.assertTrue(
                setupBody.getStatements().get(0).asExpressionStmt()
                        .getExpression().asAssignExpr().getValue().isBinaryExpr());
    }
}
