package ir.fum.oop.parser.visitor;

import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import lombok.RequiredArgsConstructor;
import ir.fum.oop.model.ClassInfo;


@RequiredArgsConstructor
public class AssociationVisitor extends VoidVisitorAdapter<Void> {

    private final ClassInfo classInfo;

    @Override
    public void visit(FieldDeclaration n, Void arg) {
        super.visit(n, arg);

        for (VariableDeclarator variable : n.getVariables()) {
            if (variable.getType() instanceof ClassOrInterfaceType) {
                classInfo.getAssociation().add(variable.getTypeAsString());
                classInfo.getAggregation().add(variable.getTypeAsString());
                classInfo.getComposition().add(variable.getTypeAsString());
            }
        }
    }
}