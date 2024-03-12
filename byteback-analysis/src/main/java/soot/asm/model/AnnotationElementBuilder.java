package soot.asm.model;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2014 Raja Vallee-Rai and others
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import soot.tag.*;

import java.util.ArrayList;

/**
 * Annotation element builder.
 *
 * @author Aaloan Miftah
 */
abstract class AnnotationElementBuilder extends AnnotationVisitor {

    protected final ArrayList<AnnotationElement> annotationElements;

    AnnotationElementBuilder(int expected) {
        super(Opcodes.ASM5);
        this.annotationElements = new ArrayList<>(expected);
    }

    AnnotationElementBuilder() {
        this(4);
    }

    public AnnotationElement getAnnotationElement(String name, Object value) {
        final AnnotationElement annotationElement;

        if (value instanceof final Byte byteValue) {
            annotationElement = new AnnotationIntElement(byteValue, 'B', name);
        } else if (value instanceof final Boolean booleanValue) {
            annotationElement = new AnnotationIntElement(booleanValue ? 1 : 0, 'Z', name);
        } else if (value instanceof final Character characterValue) {
            annotationElement = new AnnotationIntElement(characterValue, 'C', name);
        } else if (value instanceof final Short shortValue) {
            annotationElement = new AnnotationIntElement(shortValue, 'S', name);
        } else if (value instanceof final Integer integerValue) {
            annotationElement = new AnnotationIntElement(integerValue, 'I', name);
        } else if (value instanceof final Long longValue) {
            annotationElement = new AnnotationLongElement(longValue, 'J', name);
        } else if (value instanceof final Float floatValue) {
            annotationElement = new AnnotationFloatElement(floatValue, 'F', name);
        } else if (value instanceof final Double doubleValue) {
            annotationElement = new AnnotationDoubleElement(doubleValue, 'D', name);
        } else if (value instanceof final String stringValue) {
            annotationElement = new AnnotationStringElement(stringValue, 's', name);
        } else if (value instanceof final Type typeValue) {
            annotationElement = new AnnotationClassElement(typeValue.getDescriptor(), 'c', name);
        } else if (value.getClass().isArray()) {
            final ArrayList<AnnotationElement> annotationArray = new ArrayList<>();

            if (value instanceof byte[]) {
                for (Object element : (byte[]) value) {
                    annotationArray.add(getAnnotationElement(name, element));
                }
            } else if (value instanceof boolean[]) {
                for (Object element : (boolean[]) value) {
                    annotationArray.add(getAnnotationElement(name, element));
                }
            } else if (value instanceof char[]) {
                for (Object element : (char[]) value) {
                    annotationArray.add(getAnnotationElement(name, element));
                }
            } else if (value instanceof short[]) {
                for (Object element : (short[]) value) {
                    annotationArray.add(getAnnotationElement(name, element));
                }
            } else if (value instanceof int[]) {
                for (Object element : (int[]) value) {
                    annotationArray.add(getAnnotationElement(name, element));
                }
            } else if (value instanceof long[]) {
                for (Object element : (long[]) value) {
                    annotationArray.add(getAnnotationElement(name, element));
                }
            } else if (value instanceof float[]) {
                for (Object element : (float[]) value) {
                    annotationArray.add(getAnnotationElement(name, element));
                }
            } else if (value instanceof double[]) {
                for (Object element : (double[]) value) {
                    annotationArray.add(getAnnotationElement(name, element));
                }
            } else if (value instanceof String[]) {
                for (Object element : (String[]) value) {
                    annotationArray.add(getAnnotationElement(name, element));
                }
            } else if (value instanceof Type[]) {
                for (Object element : (Type[]) value) {
                    annotationArray.add(getAnnotationElement(name, element));
                }
            } else {
                throw new UnsupportedOperationException("Unsupported array value type: " + value.getClass());
            }
            annotationElement = new AnnotationArrayElement(annotationArray, '[', name);
        } else {
            throw new UnsupportedOperationException("Unsupported value type: " + value.getClass());
        }
        return (annotationElement);
    }

    @Override
    public void visit(final String name, final Object value) {
        final AnnotationElement annotationElement = getAnnotationElement(name, value);
        annotationElements.add(annotationElement);
    }

    @Override
    public void visitEnum(final String name, final String desc, final String value) {
        annotationElements.add(new AnnotationEnumElement(desc, value, 'e', name));
    }

    @Override
    public AnnotationVisitor visitArray(final String name) {
        return new AnnotationElementBuilder() {
            @Override
            public void visitEnd() {
                String elementName = name;

                if (elementName == null) {
                    elementName = "default";
                }

                annotationElements.add(new AnnotationArrayElement(this.annotationElements, '[', elementName));
            }
        };
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String name, final String desc) {
        return new AnnotationElementBuilder() {
            @Override
            public void visitEnd() {
                final AnnotationTag tag = new AnnotationTag(desc, annotationElements);
                annotationElements.add(new AnnotationSubElement(tag, '@', name));
            }
        };
    }

    @Override
    public abstract void visitEnd();
}