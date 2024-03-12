package byteback.analysis.model;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 1999 Raja Vallee-Rai
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

import soot.*;
import soot.options.Options;
import soot.tag.AbstractHost;

/**
 * Soot representation of a Java field. Can be declared to belong to a SootClass.
 */
public class FieldModel extends AbstractHost implements ClassMemberModel {
    private String name;

    private Type type;

    private int modifiers;

    protected boolean isDeclared = false;

    protected ClassModel declaringClass;

    protected volatile String sig;

    protected volatile String subSig;

    /**
     * Constructs a Soot field with the given name, type and modifiers.
     */
    public FieldModel(String name, Type type, int modifiers) {
        this.name = name;
        this.type = type;
        this.modifiers = modifiers;
    }

    /**
     * Constructs a Soot field with the given name, type and no modifiers.
     */
    public FieldModel(String name, Type type) {
        this(name, type, 0);
    }

    public int equivHashCode() {
        return type.hashCode() * 101 + modifiers * 17 + name.hashCode();
    }

    public String getSignature() {
        if (sig == null) {
            synchronized (this) {
                if (sig == null) {
                    sig = getSignature(getDeclaringClass(), getSubSignature());
                }
            }
        }
        return sig;
    }

    public static String getSignature(ClassModel cl, String name, Type type) {
        return getSignature(cl, getSubSignature(name, type));
    }

    public static String getSignature(ClassModel cl, String subSignature) {
      String buffer = '<' + Scene.v().quotedNameOf(cl.getName()) + ": " +
              subSignature + '>';
        return buffer;
    }

    public String getSubSignature() {
        if (subSig == null) {
            synchronized (this) {
                if (subSig == null) {
                    subSig = getSubSignature(getName(), getType());
                }
            }
        }
        return subSig;
    }

    protected static String getSubSignature(String name, Type type) {
      return type.toQuotedString() + ' ' + Scene.v().quotedNameOf(name);
    }

    @Override
    public ClassModel getDeclaringClass() {
        if (!isDeclared) {
            throw new RuntimeException("not declared: " + getName() + " " + getType());
        }

        return declaringClass;
    }

    public synchronized void setDeclaringClass(ClassModel sc) {
        this.declaringClass = sc;
        this.sig = null;
    }

    @Override
    public boolean isDeclared() {
        return isDeclared;
    }

    public void setDeclared(boolean isDeclared) {
        this.isDeclared = isDeclared;
    }

    public String getName() {
        return name;
    }

    public synchronized void setName(String name) {
        if (name != null) {
            this.name = name;
            this.sig = null;
            this.subSig = null;
        }
    }

    @Override
    public Type getType() {
        return type;
    }

    public synchronized void setType(Type t) {
        if (t != null) {
            this.type = t;
            this.sig = null;
            this.subSig = null;
        }
    }

    /**
     * Convenience method returning true if this field is public.
     */
    @Override
    public boolean isPublic() {
        return Modifier.isPublic(this.getModifiers());
    }

    /**
     * Convenience method returning true if this field is protected.
     */
    @Override
    public boolean isProtected() {
        return Modifier.isProtected(this.getModifiers());
    }

    /**
     * Convenience method returning true if this field is private.
     */
    @Override
    public boolean isPrivate() {
        return Modifier.isPrivate(this.getModifiers());
    }

    /**
     * Convenience method returning true if this field is static.
     */
    @Override
    public boolean isStatic() {
        return Modifier.isStatic(this.getModifiers());
    }

    /**
     * Convenience method returning true if this field is final.
     */
    public boolean isFinal() {
        return Modifier.isFinal(this.getModifiers());
    }

    @Override
    public void setModifiers(int modifiers) {
        this.modifiers = modifiers;
    }

    @Override
    public int getModifiers() {
        return modifiers;
    }

    @Override
    public String toString() {
        return getSignature();
    }

    private String getOriginalStyleDeclaration() {
        String qualifiers = (Modifier.toString(modifiers) + ' ' + type.toQuotedString()).trim();
        if (qualifiers.isEmpty()) {
            return Scene.v().quotedNameOf(name);
        } else {
            return qualifiers + ' ' + Scene.v().quotedNameOf(name);
        }

    }

    public String getDeclaration() {
        return getOriginalStyleDeclaration();
    }

    public SootFieldRef makeRef() {
        return Scene.v().makeFieldRef(declaringClass, name, type, isStatic());
    }
}
