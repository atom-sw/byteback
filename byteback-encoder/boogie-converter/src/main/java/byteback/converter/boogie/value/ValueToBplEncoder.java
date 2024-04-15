package byteback.converter.boogie.value;

import byteback.syntax.type.KindType;
import byteback.syntax.type.declaration.method.body.value.ExtendsExpr;
import byteback.syntax.type.declaration.method.body.value.TypeConstant;
import byteback.syntax.type.declaration.method.body.value.analyzer.VimpTypeInterpreter;
import byteback.syntax.type.declaration.method.body.value.ImpliesExpr;
import byteback.syntax.type.declaration.method.body.value.QuantifierExpr;
import byteback.converter.boogie.type.ClassToBplEncoder;
import byteback.converter.common.value.ValueEncoder;
import soot.*;
import soot.jimple.AndExpr;
import soot.jimple.BinopExpr;
import soot.jimple.NegExpr;

import java.io.PrintWriter;
import java.util.Iterator;

public class ValueToBplEncoder extends ValueEncoder {

    public ValueToBplEncoder(PrintWriter writer) {
        super(writer);
    }

    // This should go to the Body transformer
    public void writeTypeAccess(final Type type) {
        if (type instanceof KindType) {
            writer.write("Type");
        }
    }

    public void writeLocalAccess(final Local local) {
        writer.write("`" + local.getName() + "`");
    }

    public void writeQuantifierBindings(final Iterable<Local> locals) {
        final Iterator<Local> localIterator = locals.iterator();

        while (localIterator.hasNext()) {
            final Local local = localIterator.next();
            writeLocalAccess(local);
            writer.write(": ");
            writeTypeAccess(local.getType());

            if (localIterator.hasNext()) {
                writer.write(", ");
            }
        }
    }

    public void writeQuantifier(final QuantifierExpr quantifierExpr) {
        writer.write("(forall ");
        writeQuantifierBindings(quantifierExpr.getBindings());
        writer.write(" :: ");
        writeValue(quantifierExpr.getValue());
        writer.write(")");
    }

    public void writeCall(final String functionName, final Value... arguments) {
        writer.write(functionName);
        writer.write("(");


        for (int i = 0; i < arguments.length; ++i) {
            writeValue(arguments[i]);

            if (i != arguments.length - 1) {
                writer.write(", ");
            }
        }

        writer.write(")");
    }

    public void writeTypeConstant(final TypeConstant typeConstant) {
        writer.write(ClassToBplEncoder.makeClassConstantName(typeConstant.value));
    }

    public void writeBinaryExpr(final String operator, final BinopExpr value) {
        writer.write("(");
        writeValue(value.getOp1());
        writer.write(" " + operator + " ");
        writeValue(value.getOp2());
        writer.write(")");
    }

    public void writeNotExpr(final NegExpr negExpr) {
        writer.write("!");
        writeValue(negExpr.getOp());
    }

    public void writeValue(final Value value) {
        if (value instanceof final Local local) {
            writeLocalAccess(local);
            return;
        } else if (value instanceof final QuantifierExpr quantifierExpr) {
            writeQuantifier(quantifierExpr);
            return;
        } else if (value instanceof final TypeConstant typeConstant) {
            writeTypeConstant(typeConstant);
            return;
        } else if (value instanceof final ExtendsExpr extendsExpr) {
            writeCall("type.extends", extendsExpr.getOp1(), extendsExpr.getOp2());
            return;
        } else if (value instanceof final AndExpr andExpr) {
            if (VimpTypeInterpreter.v().typeOf(andExpr) == BooleanType.v()) {
                writeBinaryExpr("&&", andExpr);
                return;
            }
        } else if (value instanceof final NegExpr negExpr) {
            if (VimpTypeInterpreter.v().typeOf(negExpr) == BooleanType.v()) {
                writeNotExpr(negExpr);
                return;
            }
        } else if (value instanceof final ImpliesExpr impliesExpr) {
            writeBinaryExpr("==>", impliesExpr);
            return;
        }

        throw new IllegalStateException("Unable to encode expression: " + value);
    }

    @Override
    public void transformValue(final Body body, final UnitBox unitBox, final ValueBox valueBox) {
        writeValue(valueBox.getValue());
    }

}
