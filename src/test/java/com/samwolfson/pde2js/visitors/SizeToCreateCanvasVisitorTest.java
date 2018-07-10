package com.samwolfson.pde2js.visitors;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.junit.Assert;
import org.junit.Test;

public class SizeToCreateCanvasVisitorTest {
    private static final String TEST_CODE = "" +
            "public class Test {" +
            "void setup() {" +
            "size(500, 500);" +
            "} }";

    @Test
    public void shouldRenameSizeToCreateCanvas() {
        CompilationUnit cu = JavaParser.parse(TEST_CODE);
        cu.accept(new SizeToCreateCanvasVisitor(), null);

        BlockStmt setupBody = cu.getClassByName("Test").get().getMethods().get(0).getBody().get();
        Assert.assertEquals("createCanvas",
                setupBody.getStatements().get(0).asExpressionStmt()
                        .getExpression().asMethodCallExpr().getNameAsString());
    }
}
