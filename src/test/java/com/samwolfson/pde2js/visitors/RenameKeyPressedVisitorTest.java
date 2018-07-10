package com.samwolfson.pde2js.visitors;

import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import org.junit.Assert;
import org.junit.Test;

public class RenameKeyPressedVisitorTest {
    private static final String[] DIRECTIONS = {"UP", "DOWN", "LEFT", "RIGHT"};

    @Test
    public void shouldRenameKeyPressedVariableOnLeft() {
        for (String direction : DIRECTIONS) {
            NameExpr keyName = new NameExpr(direction);

            BinaryExpr condition = new BinaryExpr(keyName, new NameExpr("keyCode"), BinaryExpr.Operator.EQUALS);
            IfStmt ifStmt = new IfStmt(condition, new BlockStmt(), null);

            ifStmt.accept(new RenameKeyPressedVisitor(), null);

            Assert.assertEquals(direction + "_ARROW", keyName.getNameAsString());
        }
    }
}
