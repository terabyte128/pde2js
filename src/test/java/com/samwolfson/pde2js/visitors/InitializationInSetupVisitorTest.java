package com.samwolfson.pde2js.visitors;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.junit.Assert;
import org.junit.Test;

public class InitializationInSetupVisitorTest {
    private static final String TEST_CODE =
            "public class Test {" +
            "   int w = 3;" +
            "   int x = 7, y, z = 2;" +
            "}";

    @Test
    public void shouldMoveInitializationsToSetupMethod() {
        CompilationUnit cu = JavaParser.parse(TEST_CODE);
        MethodDeclaration setupMethod = cu.getClassByName("Test").get().addMethod("initializeFields");
        cu.accept(new InitializationInSetupVisitor(setupMethod), null);

        BlockStmt setupBody = setupMethod.getBody().get();

        // should remove all initializations from class scope
        cu.getClassByName("Test").get().getFields().forEach(field -> {
            field.getVariables().forEach(variable -> {
                Assert.assertFalse(variable.getInitializer().isPresent());
            });
        });

        Assert.assertEquals(4, setupBody.getStatements().size());

        checkAssignment("w", 3, setupBody.getStatement(0));
        checkAssignment("x", 7, setupBody.getStatement(1));
        checkAssignment("y", 0, setupBody.getStatement(2));
        checkAssignment("z", 2, setupBody.getStatement(3));
    }

    private void checkAssignment(String name, int value, Statement statement) {
        Assert.assertEquals(name, statement.asExpressionStmt().getExpression().asAssignExpr().getTarget().asNameExpr().getNameAsString());
        Assert.assertEquals(value, statement.asExpressionStmt().getExpression().asAssignExpr().getValue().asIntegerLiteralExpr().asInt());
    }
}
