package soot.jimple.toolkits.reflection;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2010 Eric Bodden
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
import byteback.analysis.model.MethodModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.*;
import soot.tag.Host;
import soot.tag.LineNumberTag;
import soot.tag.SourceLnPosTag;

import java.io.*;
import java.util.*;

public class ReflectionTraceInfo {
    private static final Logger logger = LoggerFactory.getLogger(ReflectionTraceInfo.class);

    public enum Kind {
        ClassForName, ClassNewInstance, ConstructorNewInstance, MethodInvoke, FieldSet, FieldGet
    }

    protected final Map<MethodModel, Set<String>> classForNameReceivers;
    protected final Map<MethodModel, Set<String>> classNewInstanceReceivers;
    protected final Map<MethodModel, Set<String>> constructorNewInstanceReceivers;
    protected final Map<MethodModel, Set<String>> methodInvokeReceivers;
    protected final Map<MethodModel, Set<String>> fieldSetReceivers;
    protected final Map<MethodModel, Set<String>> fieldGetReceivers;

    public ReflectionTraceInfo(String logFile) {
        this.classForNameReceivers = new LinkedHashMap<MethodModel, Set<String>>();
        this.classNewInstanceReceivers = new LinkedHashMap<MethodModel, Set<String>>();
        this.constructorNewInstanceReceivers = new LinkedHashMap<MethodModel, Set<String>>();
        this.methodInvokeReceivers = new LinkedHashMap<MethodModel, Set<String>>();
        this.fieldSetReceivers = new LinkedHashMap<MethodModel, Set<String>>();
        this.fieldGetReceivers = new LinkedHashMap<MethodModel, Set<String>>();

        if (logFile == null) {
            throw new InternalError("Trace based refection model enabled but no trace file given!?");
        } else {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(logFile)))) {
                final Scene sc = Scene.v();
                final Set<String> ignoredKinds = new HashSet<String>();
                for (String line; (line = reader.readLine()) != null; ) {
                    if (line.isEmpty()) {
                        continue;
                    }
                    final String[] portions = line.split(";", -1);
                    final String kind = portions[0];
                    final String target = portions[1];
                    final String source = portions[2];
                    final int lineNumber = portions[3].length() == 0 ? -1 : Integer.parseInt(portions[3]);

                    for (MethodModel sourceMethod : inferSource(source, lineNumber)) {
                        switch (kind) {
                            case "Class.forName": {
                                Set<String> receiverNames = classForNameReceivers.get(sourceMethod);
                                if (receiverNames == null) {
                                    classForNameReceivers.put(sourceMethod, receiverNames = new LinkedHashSet<String>());
                                }
                                receiverNames.add(target);
                                break;
                            }
                            case "Class.newInstance": {
                                Set<String> receiverNames = classNewInstanceReceivers.get(sourceMethod);
                                if (receiverNames == null) {
                                    classNewInstanceReceivers.put(sourceMethod, receiverNames = new LinkedHashSet<String>());
                                }
                                receiverNames.add(target);
                                break;
                            }
                            case "Method.invoke": {
                                if (!sc.containsMethod(target)) {
                                    throw new RuntimeException("Unknown method for signature: " + target);
                                }
                                Set<String> receiverNames = methodInvokeReceivers.get(sourceMethod);
                                if (receiverNames == null) {
                                    methodInvokeReceivers.put(sourceMethod, receiverNames = new LinkedHashSet<String>());
                                }
                                receiverNames.add(target);
                                break;
                            }
                            case "Constructor.newInstance": {
                                if (!sc.containsMethod(target)) {
                                    throw new RuntimeException("Unknown method for signature: " + target);
                                }
                                Set<String> receiverNames = constructorNewInstanceReceivers.get(sourceMethod);
                                if (receiverNames == null) {
                                    constructorNewInstanceReceivers.put(sourceMethod, receiverNames = new LinkedHashSet<String>());
                                }
                                receiverNames.add(target);
                                break;
                            }
                            case "Field.set*": {
                                if (!sc.containsField(target)) {
                                    throw new RuntimeException("Unknown method for signature: " + target);
                                }
                                Set<String> receiverNames = fieldSetReceivers.get(sourceMethod);
                                if (receiverNames == null) {
                                    fieldSetReceivers.put(sourceMethod, receiverNames = new LinkedHashSet<String>());
                                }
                                receiverNames.add(target);
                                break;
                            }
                            case "Field.get*": {
                                if (!sc.containsField(target)) {
                                    throw new RuntimeException("Unknown method for signature: " + target);
                                }
                                Set<String> receiverNames = fieldGetReceivers.get(sourceMethod);
                                if (receiverNames == null) {
                                    fieldGetReceivers.put(sourceMethod, receiverNames = new LinkedHashSet<String>());
                                }
                                receiverNames.add(target);
                                break;
                            }
                            default:
                                ignoredKinds.add(kind);
                                break;
                        }
                    }
                }
                if (!ignoredKinds.isEmpty()) {
                    logger.debug("Encountered reflective calls entries of the following kinds that\ncannot currently be handled:");
                    for (String kind : ignoredKinds) {
                        logger.debug(kind);
                    }
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException("Trace file not found.", e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Set<MethodModel> inferSource(String source, int lineNumber) {
        int dotIndex = source.lastIndexOf('.');
        String className = source.substring(0, dotIndex);
        String methodName = source.substring(dotIndex + 1);
        final Scene scene = Scene.v();
        if (!scene.containsClass(className)) {
            scene.addBasicClass(className, ClassModel.BODIES);
            scene.loadBasicClasses();
            if (!scene.containsClass(className)) {
                throw new RuntimeException("Trace file refers to unknown class: " + className);
            }
        }

        Set<MethodModel> methodsWithRightName = new LinkedHashSet<MethodModel>();
        for (MethodModel m : scene.getSootClass(className).getMethodModels()) {
            if (m.isConcrete() && m.getName().equals(methodName)) {
                methodsWithRightName.add(m);
            }
        }

        if (methodsWithRightName.isEmpty()) {
            throw new RuntimeException("Trace file refers to unknown method with name " + methodName + " in Class " + className);
        } else if (methodsWithRightName.size() == 1) {
            return Collections.singleton(methodsWithRightName.iterator().next());
        } else {
            // more than one method with that name
            for (MethodModel methodModel : methodsWithRightName) {
                if (coversLineNumber(lineNumber, methodModel)) {
                    return Collections.singleton(methodModel);
                }
                if (methodModel.isConcrete()) {
                    if (!methodModel.hasActiveBody()) {
                        methodModel.retrieveActiveBody();
                    }
                    Body body = methodModel.getActiveBody();
                    if (coversLineNumber(lineNumber, body)) {
                        return Collections.singleton(methodModel);
                    }
                    for (Unit u : body.getUnits()) {
                        if (coversLineNumber(lineNumber, u)) {
                            return Collections.singleton(methodModel);
                        }
                    }
                }
            }

            // if we get here then we found no method with the right line number information;
            // be conservative and return all method that we found
            return methodsWithRightName;
        }
    }

    private boolean coversLineNumber(int lineNumber, Host host) {
        {
            SourceLnPosTag tag = (SourceLnPosTag) host.getTag(SourceLnPosTag.NAME);
            if (tag != null) {
                if (tag.startLn() <= lineNumber && tag.endLn() >= lineNumber) {
                    return true;
                }
            }
        }
        {
            LineNumberTag tag = (LineNumberTag) host.getTag(LineNumberTag.NAME);
            if (tag != null) {
                return tag.getLineNumber() == lineNumber;
            }
        }
        return false;
    }

    public Set<String> classForNameClassNames(MethodModel container) {
        Set<String> ret = classForNameReceivers.get(container);
        return (ret != null) ? ret : Collections.emptySet();
    }

    public Set<ClassModel> classForNameClasses(MethodModel container) {
        Set<ClassModel> result = new LinkedHashSet<ClassModel>();
        for (String className : classForNameClassNames(container)) {
            result.add(Scene.v().getSootClass(className));
        }
        return result;
    }

    public Set<String> classNewInstanceClassNames(MethodModel container) {
        Set<String> ret = classNewInstanceReceivers.get(container);
        return (ret != null) ? ret : Collections.emptySet();
    }

    public Set<ClassModel> classNewInstanceClasses(MethodModel container) {
        Set<ClassModel> result = new LinkedHashSet<ClassModel>();
        for (String className : classNewInstanceClassNames(container)) {
            result.add(Scene.v().getSootClass(className));
        }
        return result;
    }

    public Set<String> constructorNewInstanceSignatures(MethodModel container) {
        Set<String> ret = constructorNewInstanceReceivers.get(container);
        return (ret != null) ? ret : Collections.emptySet();
    }

    public Set<MethodModel> constructorNewInstanceConstructors(MethodModel container) {
        Set<MethodModel> result = new LinkedHashSet<MethodModel>();
        for (String signature : constructorNewInstanceSignatures(container)) {
            result.add(Scene.v().getMethod(signature));
        }
        return result;
    }

    public Set<String> methodInvokeSignatures(MethodModel container) {
        Set<String> ret = methodInvokeReceivers.get(container);
        return (ret != null) ? ret : Collections.emptySet();
    }

    public Set<MethodModel> methodInvokeMethods(MethodModel container) {
        Set<MethodModel> result = new LinkedHashSet<MethodModel>();
        for (String signature : methodInvokeSignatures(container)) {
            result.add(Scene.v().getMethod(signature));
        }
        return result;
    }

    public Set<MethodModel> methodsContainingReflectiveCalls() {
        Set<MethodModel> res = new LinkedHashSet<MethodModel>();
        res.addAll(classForNameReceivers.keySet());
        res.addAll(classNewInstanceReceivers.keySet());
        res.addAll(constructorNewInstanceReceivers.keySet());
        res.addAll(methodInvokeReceivers.keySet());
        return res;
    }

    public Set<String> fieldSetSignatures(MethodModel container) {
        Set<String> ret = fieldSetReceivers.get(container);
        return (ret != null) ? ret : Collections.emptySet();
    }

    public Set<String> fieldGetSignatures(MethodModel container) {
        Set<String> ret = fieldGetReceivers.get(container);
        return (ret != null) ? ret : Collections.emptySet();
    }
}
