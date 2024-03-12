package soot.util.annotations;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
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

import soot.tag.*;

/**
 * An {@link AbtractAnnotationElementTypeSwitch} that converts an {@link AnnotationElement} to a mapping of element name and the
 * actual result.
 *
 * @author Florian Kuebler
 */
public class AnnotationElementSwitch extends AbtractAnnotationElementTypeSwitch<AnnotationElementSwitch.AnnotationElemResult<?>> {

    /**
     * A helper class to map method name and result.
     *
     * @param <V> the result type.
     * @author Florian Kuebler
     */
    public static class AnnotationElemResult<V> {

        private final String name;
        private final V value;

        public AnnotationElemResult(String name, V value) {
            this.name = name;
            this.value = value;
        }

        public String getKey() {
            return name;
        }

        public V getValue() {
            return value;
        }
    }

    @Override
    public void caseAnnotationAnnotationElem(AnnotationSubElement v) {
        AnnotationInstanceCreator aic = new AnnotationInstanceCreator();

        Object result = aic.create(v.getValue());

        setResult(new AnnotationElemResult<Object>(v.getName(), result));
    }

    @Override
    public void caseAnnotationArrayElem(AnnotationArrayElement v) {
        /*
         * for arrays, apply a new AnnotationElemSwitch to every array element and collect the results. Note that the component
         * type of the result is unknown here, s.t. object has to be used.
         */
        Object[] result = new Object[v.getNumValues()];

        int i = 0;
        for (AnnotationElement elem : v.getValues()) {
            AnnotationElementSwitch sw = new AnnotationElementSwitch();
            elem.apply(sw);
            result[i] = sw.getResult().getValue();
            i++;
        }

        setResult(new AnnotationElemResult<Object[]>(v.getName(), result));
    }

    @Override
    public void caseAnnotationBooleanElem(AnnotationBooleanElement v) {
        setResult(new AnnotationElemResult<Boolean>(v.getName(), v.getValue()));
    }

    @Override
    public void caseAnnotationClassElem(AnnotationClassElement v) {
        try {
            Class<?> clazz = ClassLoaderUtils.loadClass(v.getDesc().replace('/', '.'));
            setResult(new AnnotationElemResult<Class<?>>(v.getName(), clazz));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Could not load class: " + v.getDesc());
        }
    }

    @Override
    public void caseAnnotationDoubleElem(AnnotationDoubleElement v) {
        setResult(new AnnotationElemResult<Double>(v.getName(), v.getValue()));
    }

    @Override
    public void caseAnnotationEnumElem(AnnotationEnumElement v) {
        try {
            Class<?> clazz = ClassLoaderUtils.loadClass(v.getTypeName().replace('/', '.'));

            // find out which enum constant is used.
            Enum<?> result = null;
            for (Object o : clazz.getEnumConstants()) {
                try {
                    Enum<?> t = (Enum<?>) o;
                    if (t.name().equals(v.getConstantName())) {
                        result = t;
                        break;
                    }
                } catch (ClassCastException e) {
                    throw new RuntimeException("Class " + v.getTypeName() + " is no Enum");
                }
            }

            if (result == null) {
                throw new RuntimeException(v.getConstantName() + " is not a EnumConstant of " + v.getTypeName());
            }

            setResult(new AnnotationElemResult<Enum<?>>(v.getName(), result));

        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Could not load class: " + v.getTypeName());
        }
    }

    @Override
    public void caseAnnotationFloatElem(AnnotationFloatElement v) {
        setResult(new AnnotationElemResult<Float>(v.getName(), v.getValue()));
    }

    @Override
    public void caseAnnotationIntElem(AnnotationIntElement v) {
        setResult(new AnnotationElemResult<Integer>(v.getName(), v.getValue()));
    }

    @Override
    public void caseAnnotationLongElem(AnnotationLongElement v) {
        setResult(new AnnotationElemResult<Long>(v.getName(), v.getValue()));
    }

    @Override
    public void caseAnnotationStringElem(AnnotationStringElement v) {
        setResult(new AnnotationElemResult<String>(v.getName(), v.getValue()));
    }

    @Override
    public void defaultCase(Object object) {
        throw new RuntimeException("Unexpected AnnotationElem");
    }
}
