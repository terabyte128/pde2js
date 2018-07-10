package com.samwolfson.pde2js.visitors;

import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class CollectLoadMethodsVisitorTest {

    private static final String[] LOAD_TYPES = { "Model", "Image", "JSON", "Strings", "Table", "XML", "Bytes", "Font", "Shader" };

    @Test
    public void shouldReportLoadFunctions() {
        for (String loadType : LOAD_TYPES) {
            BlockStmt blockStmt = new BlockStmt();
            blockStmt.addStatement(new AssignExpr(new NameExpr("x"), new MethodCallExpr("load" + loadType), AssignExpr.Operator.ASSIGN));
            List<Expression> expressions = loadCalls(blockStmt);

            Assert.assertEquals(1, expressions.size());
            Assert.assertTrue(expressions.get(0).isAssignExpr());
            Assert.assertEquals("load" + loadType, expressions.get(0).toAssignExpr().get().getValue().asMethodCallExpr().getNameAsString());
        }
    }

    private List<Expression> loadCalls(Statement s) {
        CollectLoadMethodsVisitor visitor = new CollectLoadMethodsVisitor();
        s.accept(visitor, null);

        return visitor.getLoadMethodCalls();
    }
}
