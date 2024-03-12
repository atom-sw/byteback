package soot;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2004 Ondrej Lhotak
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

import byteback.analysis.model.ClassModel;
import byteback.analysis.model.FieldModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.options.Options;

import java.util.ArrayDeque;

/**
 * Representation of a reference to a field as it appears in a class file. Note that the field directly referred to may not
 * actually exist; the actual target of the reference is determined according to the resolution procedure in the Java Virtual
 * Machine Specification, 2nd ed, section 5.4.3.2.
 */

public class AbstractFieldRef implements SootFieldRef {
    private static final Logger logger = LoggerFactory.getLogger(AbstractFieldRef.class);

    public AbstractFieldRef(ClassModel declaringClass, String name, Type type, boolean isStatic) {
        this.declaringClass = declaringClass;
        this.name = name;
        this.type = type;
        this.isStatic = isStatic;
        if (declaringClass == null) {
            throw new RuntimeException("Attempt to create SootFieldRef with null class");
        }
        if (name == null) {
            throw new RuntimeException("Attempt to create SootFieldRef with null name");
        }
        if (type == null) {
            throw new RuntimeException("Attempt to create SootFieldRef with null type");
        }
    }

    private final ClassModel declaringClass;
    private final String name;
    private final Type type;
    private final boolean isStatic;

    @Override
    public ClassModel declaringClass() {
        return declaringClass;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Type type() {
        return type;
    }

    @Override
    public boolean isStatic() {
        return isStatic;
    }

    @Override
    public String getSignature() {
        return FieldModel.getSignature(declaringClass, name, type);
    }

    public class FieldResolutionFailedException extends ResolutionFailedException {
        /**
         *
         */
        private static final long serialVersionUID = -4657113720516199499L;

        public FieldResolutionFailedException() {
            super("Class " + declaringClass + " doesn't have field " + name + " : " + type
                    + "; failed to resolve in superclasses and interfaces");
        }

        @Override
        public String toString() {
            StringBuffer ret = new StringBuffer();
            ret.append(super.toString());
            resolve(ret);
            return ret.toString();
        }
    }

    @Override
    public FieldModel resolve() {
        return resolve(null);
    }

    private FieldModel checkStatic(FieldModel ret) {
        if ((Options.v().wrong_staticness() == Options.wrong_staticness_fail
                || Options.v().wrong_staticness() == Options.wrong_staticness_fixstrict) && ret.isStatic() != isStatic()
                && !ret.isPhantom()) {
            throw new ResolutionFailedException("Resolved " + this + " to " + ret + " which has wrong static-ness");
        }
        return ret;
    }

    private FieldModel resolve(StringBuffer trace) {
        ClassModel cl = declaringClass;
        while (true) {
            if (trace != null) {
                trace.append("Looking in " + cl + " which has fields " + cl.getFieldModels() + "\n");
            }

            // Check whether we have the field in the current class
            FieldModel clField = cl.getFieldUnsafe(name, type);
            if (clField != null) {
                return checkStatic(clField);
            }
            // If we have a phantom class, we directly construct a phantom field
            // in it and don't care about superclasses.
            else if (Scene.v().allowsPhantomRefs() && cl.isPhantom()) {
                synchronized (cl) {
                    // Check that no other thread has created the field in the
                    // meantime
                    clField = cl.getFieldUnsafe(name, type);
                    if (clField != null) {
                        return checkStatic(clField);
                    }

                    // Make sure that we don't have a conflicting field
                    FieldModel existingField = cl.getFieldByNameUnsafe(name);
                    if (existingField != null) {
                        return handleFieldTypeMismatch(clField);
                    }

                    // Create the phantom field
                    FieldModel f = Scene.v().makeSootField(name, type, isStatic() ? Modifier.STATIC : 0);
                    f.setPhantom(true);
                    cl.addFieldModel(f);
                    return f;
                }
            } else {
                // Since this class is not phantom, we look at its interfaces
                ArrayDeque<ClassModel> queue = new ArrayDeque<ClassModel>();
                queue.addAll(cl.getInterfaceTypes());
                while (true) {
                    ClassModel iface = queue.poll();
                    if (iface == null) {
                        break;
                    }

                    if (trace != null) {
                        trace.append("Looking in " + iface + " which has fields " + iface.getFieldModels() + "\n");
                    }
                    FieldModel ifaceField = iface.getFieldUnsafe(name, type);
                    if (ifaceField != null) {
                        return checkStatic(ifaceField);
                    }
                    queue.addAll(iface.getInterfaceTypes());
                }

                // If we have not found a suitable field in the current class,
                // try the superclass
                if (cl.hasSuperclass()) {
                    cl = cl.getSuperType();
                } else {
                    break;
                }
            }
        }

        // If we allow phantom refs, we construct phantom fields
        if (Options.v().allow_phantom_refs()) {
            FieldModel sf = Scene.v().makeSootField(name, type, isStatic ? Modifier.STATIC : 0);
            sf.setPhantom(true);
            synchronized (declaringClass) {
                // Be careful: Another thread may have already created this
                // field in the meantime, so better check twice.
                FieldModel clField = declaringClass.getFieldByNameUnsafe(name);
                if (clField != null) {
                    if (clField.getType().equals(type)) {
                        return checkStatic(clField);
                    } else {
                        return handleFieldTypeMismatch(clField);
                    }
                } else {
                    // Add the new phantom field
                    declaringClass.addFieldModel(sf);
                    return sf;
                }
            }
        }

        if (trace == null) {
            FieldResolutionFailedException e = new FieldResolutionFailedException();
            if (Options.v().ignore_resolution_errors()) {
                logger.debug(e.getMessage());
            } else {
                throw e;
            }
        }
        return null;
    }

    protected FieldModel handleFieldTypeMismatch(FieldModel clField) {
        switch (Options.v().field_type_mismatches()) {
            case Options.field_type_mismatches_fail:
                throw new ConflictingFieldRefException(clField, type);
            case Options.field_type_mismatches_ignore:
                return checkStatic(clField);
            case Options.field_type_mismatches_null:
                return null;
        }
        throw new RuntimeException(
                String.format("Unsupported option for handling field type mismatches: %d", Options.v().field_type_mismatches()));
    }

    @Override
    public String toString() {
        return getSignature();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((declaringClass == null) ? 0 : declaringClass.hashCode());
        result = prime * result + (isStatic ? 1231 : 1237);
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }
        AbstractFieldRef other = (AbstractFieldRef) obj;
        if (declaringClass == null) {
            if (other.declaringClass != null) {
                return false;
            }
        } else if (!declaringClass.equals(other.declaringClass)) {
            return false;
        }
        if (isStatic != other.isStatic) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (type == null) {
            return other.type == null;
        } else return type.equals(other.type);
    }

}
