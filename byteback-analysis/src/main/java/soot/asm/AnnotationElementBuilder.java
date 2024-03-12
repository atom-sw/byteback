package soot.asm;

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

import java.util.ArrayList;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import soot.tagkit.AnnotationAnnotationElem;
import soot.tagkit.AnnotationArrayElem;
import soot.tagkit.AnnotationClassElem;
import soot.tagkit.AnnotationDoubleElem;
import soot.tagkit.AnnotationElem;
import soot.tagkit.AnnotationEnumElement;
import soot.tagkit.AnnotationFloatElem;
import soot.tagkit.AnnotationIntElem;
import soot.tagkit.AnnotationLongElem;
import soot.tagkit.AnnotationStringElem;
import soot.tagkit.AnnotationTag;

/**
 * Annotation element builder.
 * 
 * @author Aaloan Miftah
 */
abstract class AnnotationElementBuilder extends AnnotationVisitor {

  protected final ArrayList<AnnotationElem> annotationElements;

  AnnotationElementBuilder(int expected) {
    super(Opcodes.ASM5);
    this.annotationElements = new ArrayList<>(expected);
  }

  AnnotationElementBuilder() {
    this(4);
  }

  public AnnotationElem getAnnotationElement(String name, Object value) {
    AnnotationElem elem;
    if (value instanceof final Byte byteValue) {
      elem = new AnnotationIntElem(byteValue, 'B', name);
    } else if (value instanceof final Boolean booleanValue) {
      elem = new AnnotationIntElem(booleanValue ? 1 : 0, 'Z', name);
    } else if (value instanceof final Character characterValue) {
      elem = new AnnotationIntElem(characterValue, 'C', name);
    } else if (value instanceof final Short shortValue) {
      elem = new AnnotationIntElem(shortValue, 'S', name);
    } else if (value instanceof final Integer integerValue) {
      elem = new AnnotationIntElem(integerValue, 'I', name);
    } else if (value instanceof final Long longValue) {
      elem = new AnnotationLongElem(longValue, 'J', name);
    } else if (value instanceof final Float floatValue) {
      elem = new AnnotationFloatElem(floatValue, 'F', name);
    } else if (value instanceof final Double doubleValue) {
      elem = new AnnotationDoubleElem(doubleValue, 'D', name);
    } else if (value instanceof final String stringValue) {
      elem = new AnnotationStringElem(stringValue, 's', name);
    } else if (value instanceof final Type typeValue) {
      elem = new AnnotationClassElem(typeValue.getDescriptor(), 'c', name);
    } else if (value.getClass().isArray()) {
      ArrayList<AnnotationElem> annotationArray = new ArrayList<AnnotationElem>();
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
      elem = new AnnotationArrayElem(annotationArray, '[', name);
    } else {
      throw new UnsupportedOperationException("Unsupported value type: " + value.getClass());
    }
    return (elem);
  }

  @Override
  public void visit(String name, Object value) {
    AnnotationElem elem = getAnnotationElement(name, value);
    annotationElements.add(elem);
  }

  @Override
  public void visitEnum(String name, String desc, String value) {
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

        annotationElements.add(new AnnotationArrayElem(this.annotationElements, '[', elementName));
      }
    };
  }

  @Override
  public AnnotationVisitor visitAnnotation(final String name, final String desc) {
    return new AnnotationElementBuilder() {
      @Override
      public void visitEnd() {
        final AnnotationTag tag = new AnnotationTag(desc, annotationElements);
        annotationElements.add(new AnnotationAnnotationElem(tag, '@', name));
      }
    };
  }

  @Override
  public abstract void visitEnd();
}