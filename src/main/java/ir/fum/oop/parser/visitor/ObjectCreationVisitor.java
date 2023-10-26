package ir.fum.oop.parser.visitor;

import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import lombok.RequiredArgsConstructor;
import ir.fum.oop.model.ClassInfo;


@RequiredArgsConstructor
public class ObjectCreationVisitor extends VoidVisitorAdapter<Void> {

    private final ClassInfo classInfo;
    @Override
    public void visit(ObjectCreationExpr n, Void arg) {
        super.visit(n, arg);

        classInfo.getInstantiation().add(n.getTypeAsString());
    }
}
