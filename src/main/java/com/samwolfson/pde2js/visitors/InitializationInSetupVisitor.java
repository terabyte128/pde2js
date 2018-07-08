package com.samwolfson.pde2js.visitors;

import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/**
 * Move all variable initializations from the top of the class into the setup() method.
 */
public class InitializationInSetupVisitor extends VoidVisitorAdapter<Void> {
    private BlockStmt setupMethodBody;

    /**
     *
     * @param setupMethod Processing's setup() method
     */
    public InitializationInSetupVisitor(MethodDeclaration setupMethod) {
        super();
        this.setupMethodBody = setupMethod.getBody()
                .orElseThrow(() -> new IllegalStateException("setup() must have a body"));
    }

    @Override
    public void visit(FieldDeclaration n, Void arg) {
        super.visit(n, arg);

        // loop over the variables backwards, since they will be
        // inserted at the top of the setup() method (index 0)
        for (int i = n.getVariables().size() - 1; i >= 0; i--) {
            VariableDeclarator v = n.getVariable(i);
            if (v.getInitializer().isPresent()) {
                Expression initializer = v.getInitializer().get();
                setupMethodBody.addStatement(0, new AssignExpr(v.getNameAsExpression(), initializer, AssignExpr.Operator.ASSIGN));
                v.removeInitializer();
            }
        }
    }
}
