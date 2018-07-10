package com.samwolfson.pde2js.visitors;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.junit.Assert;
import org.junit.Test;

public class PrintlnToPrintVisitorTest {
    private static final String TEST_CODE =
            "public class Test {" +
                    "void setup() {" +
                    "println(\"hello, world!\");" +
                    "}" +
                    "}";

    @Test
    public void shouldChangePrintlnToPrint() {
        CompilationUnit cu = JavaParser.parse(TEST_CODE);
        cu.accept(new PrintlnToPrintVisitor(), null);

        MethodDeclaration setupMethod = cu.getClassByName("Test").get().getMethods().get(0);

        Assert.assertEquals("print",
                setupMethod.getBody().get().getStatements().get(0)
                        .asExpressionStmt().getExpression().asMethodCallExpr().getNameAsString());
    }
}
