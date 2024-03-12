package soot.jimple.toolkits.infoflow;

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

import byteback.analysis.model.ClassModel;
import byteback.analysis.model.FieldModel;
import byteback.analysis.model.MethodModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.*;
import soot.jimple.*;
import soot.jimple.toolkits.callgraph.Edge;
import soot.jimple.toolkits.callgraph.ReachableMethods;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.graph.ExceptionalUnitGraphFactory;
import soot.toolkits.graph.MutableDirectedGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.Pair;

import java.util.*;

// ClassLocalObjectsAnalysis written by Richard L. Halpert, 2007-02-23
// Finds objects that are local to the given scope.
// NOTE THAT THIS ANALYSIS'S RESULTS DO NOT APPLY TO SUBCLASSES OF THE GIVEN CLASS

public class ClassLocalObjectsAnalysis {
    private static final Logger logger = LoggerFactory.getLogger(ClassLocalObjectsAnalysis.class);
    boolean printdfgs;

    LocalObjectsAnalysis loa;
    InfoFlowAnalysis dfa;
    InfoFlowAnalysis primitiveDfa;
    UseFinder uf;
    ClassModel classModel;

    Map<MethodModel, SmartMethodLocalObjectsAnalysis> methodToMethodLocalObjectsAnalysis;
    Map<MethodModel, CallLocalityContext> methodToContext;

    List<MethodModel> allMethods;
    // methods that are called at least once from outside of this class (ie need to be public, protected, or package-private)
    List<MethodModel> externalMethods;
    // methods that are only ever called by other methods in this class (ie could be marked private)
    List<MethodModel> internalMethods;
    // methods that should be used as starting points when determining if a value in a method called from this class is local
    // or shared
    // for thread-local objects, this would contain just the run method. For structure-local, it should contain all external
    // methods
    List<MethodModel> entryMethods;

    List<FieldModel> allFields;
    List<FieldModel> externalFields;
    List<FieldModel> internalFields;

    ArrayList<FieldModel> localFields;
    ArrayList<FieldModel> sharedFields;
    ArrayList<FieldModel> localInnerFields;
    ArrayList<FieldModel> sharedInnerFields;

    public ClassLocalObjectsAnalysis(LocalObjectsAnalysis loa, InfoFlowAnalysis dfa, UseFinder uf, ClassModel classModel) {
        this(loa, dfa, null, uf, classModel, null);
    }

    public ClassLocalObjectsAnalysis(LocalObjectsAnalysis loa, InfoFlowAnalysis dfa, InfoFlowAnalysis primitiveDfa,
                                     UseFinder uf, ClassModel classModel, List<MethodModel> entryMethods) {
        printdfgs = dfa.printDebug();
        this.loa = loa;
        this.dfa = dfa;
        this.primitiveDfa = primitiveDfa;
        this.uf = uf;
        this.classModel = classModel;

        this.methodToMethodLocalObjectsAnalysis = new HashMap<MethodModel, SmartMethodLocalObjectsAnalysis>();
        this.methodToContext = null;

        this.allMethods = null;
        this.externalMethods = null;
        this.internalMethods = null;
        this.entryMethods = entryMethods;

        this.allFields = null;
        this.externalFields = null;
        this.internalFields = null;

        this.localFields = null;
        this.sharedFields = null;
        this.localInnerFields = null;
        this.sharedInnerFields = null;

        if (true) // verbose)
        {
            logger.debug("[local-objects] Analyzing local objects for " + classModel);
            logger.debug("[local-objects]   preparing class             " + new Date());
        }
        prepare();

        if (true) // verbose)
        {
            logger.debug("[local-objects]   analyzing class             " + new Date());
        }
        doAnalysis();

        if (true) // verbose)
        {
            logger.debug("[local-objects]   propagating over call graph " + new Date());
        }
        propagate();

        if (true) {
            logger.debug("[local-objects]   finished at                 " + new Date());
            logger.debug("[local-objects]   (#analyzed/#encountered): " + SmartMethodInfoFlowAnalysis.counter + "/"
                    + ClassInfoFlowAnalysis.methodCount);
        }
    }

    private void prepare() {
        // Get list of all methods
        allMethods = getAllReachableMethods(classModel);

        // Get list of external methods
        externalMethods = uf.getExtMethods(classModel);
        ClassModel superclass = classModel;
        if (superclass.hasSuperclass()) {
            superclass = superclass.getSuperType();
        }
        while (superclass.hasSuperclass()) {
            if (superclass.isApplicationClass()) {
                externalMethods.addAll(uf.getExtMethods(superclass));
            }
            superclass = superclass.getSuperType();
        }

        // Get list of internal methods
        internalMethods = new ArrayList<MethodModel>();
        for (MethodModel method : allMethods) {
            if (!externalMethods.contains(method)) {
                internalMethods.add(method);
            }
        }

        // Get list of all fields
        allFields = getAllFields(classModel);

        // Get list of external fields
        externalFields = uf.getExtFields(classModel);
        superclass = classModel;
        if (superclass.hasSuperclass()) {
            superclass = superclass.getSuperType();
        }
        while (superclass.hasSuperclass()) {
            if (superclass.isApplicationClass()) {
                externalFields.addAll(uf.getExtFields(superclass));
            }
            superclass = superclass.getSuperType();
        }

        // Get list of internal fields
        internalFields = new ArrayList<FieldModel>();
        for (FieldModel field : allFields) {
            if (!externalFields.contains(field)) {
                internalFields.add(field);
            }
        }

    }

    // Returns a list of reachable methods in class sc and its superclasses
    public static List<MethodModel> getAllReachableMethods(ClassModel sc) {
        ReachableMethods rm = Scene.v().getReachableMethods();

        // Get list of reachable methods declared in this class
        List<MethodModel> allMethods = new ArrayList<MethodModel>();
        Iterator methodsIt = sc.methodIterator();
        while (methodsIt.hasNext()) {
            MethodModel method = (MethodModel) methodsIt.next();
            if (rm.contains(method)) {
                allMethods.add(method);
            }
        }

        // Add reachable methods declared in superclasses
        ClassModel superclass = sc;
        if (superclass.hasSuperclass()) {
            superclass = superclass.getSuperType();
        }
        while (superclass.hasSuperclass()) // we don't want to process Object
        {
            Iterator scMethodsIt = superclass.methodIterator();
            while (scMethodsIt.hasNext()) {
                MethodModel scMethod = (MethodModel) scMethodsIt.next();
                if (rm.contains(scMethod)) {
                    allMethods.add(scMethod);
                }
            }
            superclass = superclass.getSuperType();
        }
        return allMethods;
    }

    // Returns a list of fields in class sc and its superclasses
    public static List<FieldModel> getAllFields(ClassModel sc) {
        // Get list of reachable methods declared in this class
        // Also get list of fields declared in this class
        List<FieldModel> allFields = new ArrayList<FieldModel>();
        for (FieldModel field : sc.getFieldModels()) {
            allFields.add(field);
        }

        // Add reachable methods and fields declared in superclasses
        ClassModel superclass = sc;
        if (superclass.hasSuperclass()) {
            superclass = superclass.getSuperType();
        }
        while (superclass.hasSuperclass()) // we don't want to process Object
        {
            for (FieldModel scField : superclass.getFieldModels()) {
                allFields.add(scField);
            }
            superclass = superclass.getSuperType();
        }
        return allFields;
    }

    private void doAnalysis() {
        // Combine the DFA results for each of this class's methods, using safe
        // approximations for which parameters, fields, and globals are shared
        // or local.

        // Separate fields into shared and local. Initially fields are known to be
        // shared if they have any external accesses, or if they're static.
        // Methods are iterated over, moving fields to shared if shared data flows to them.
        // This is repeated until no fields move for a complete iteration.

        // Populate localFields and sharedFields with fields of this class
        localFields = new ArrayList<FieldModel>();
        sharedFields = new ArrayList<FieldModel>();
        Iterator<FieldModel> fieldsIt = allFields.iterator();
        while (fieldsIt.hasNext()) {
            FieldModel field = fieldsIt.next();
            if (fieldIsInitiallyLocal(field)) {
                localFields.add(field);
            } else {
                sharedFields.add(field);
            }
        }

        // Add inner fields to localFields and sharedFields, if present
        localInnerFields = new ArrayList<FieldModel>();
        sharedInnerFields = new ArrayList<FieldModel>();
        Iterator<MethodModel> methodsIt = allMethods.iterator();
        while (methodsIt.hasNext()) {
            MethodModel method = methodsIt.next();

            // Get data flow summary
            MutableDirectedGraph dataFlowSummary;
            if (primitiveDfa != null) {
                dataFlowSummary = primitiveDfa.getMethodInfoFlowSummary(method);

                if (printdfgs && method.getDeclaringClass().isApplicationClass()) {
                    logger.debug("Attempting to print graphs (will succeed only if ./dfg/ is a valid path)");
                    DirectedGraph primitiveGraph = primitiveDfa.getMethodInfoFlowAnalysis(method).getMethodAbbreviatedInfoFlowGraph();
                    InfoFlowAnalysis.printGraphToDotFile(
                            "dfg/" + method.getDeclaringClass().getShortName() + "_" + method.getName() + "_primitive", primitiveGraph,
                            method.getName() + "_primitive", false);

                    DirectedGraph nonPrimitiveGraph = dfa.getMethodInfoFlowAnalysis(method).getMethodAbbreviatedInfoFlowGraph();
                    InfoFlowAnalysis.printGraphToDotFile("dfg/" + method.getDeclaringClass().getShortName() + "_" + method.getName(),
                            nonPrimitiveGraph, method.getName(), false);
                }
            } else {
                dataFlowSummary = dfa.getMethodInfoFlowSummary(method);

                if (printdfgs && method.getDeclaringClass().isApplicationClass()) {
                    logger.debug("Attempting to print graph (will succeed only if ./dfg/ is a valid path)");
                    DirectedGraph nonPrimitiveGraph = dfa.getMethodInfoFlowAnalysis(method).getMethodAbbreviatedInfoFlowGraph();
                    InfoFlowAnalysis.printGraphToDotFile("dfg/" + method.getDeclaringClass().getShortName() + "_" + method.getName(),
                            nonPrimitiveGraph, method.getName(), false);
                }
            }

            // Iterate through nodes
            Iterator<Object> nodesIt = dataFlowSummary.getNodes().iterator();
            while (nodesIt.hasNext()) {
                EquivalentValue node = (EquivalentValue) nodesIt.next();
                if (node.getValue() instanceof InstanceFieldRef ifr) {
                  if (!localFields.contains(ifr.getField()) && !sharedFields.contains(ifr.getField())
                            && !localInnerFields.contains(ifr.getField())) // &&
                    // !sharedInnerFields.contains(ifr.getField()))
                    {
                        // this field is read or written, but is not in the lists of fields!
                        localInnerFields.add(ifr.getField());
                    }
                }
            }
        }

        // Propagate (aka iterate iterate iterate iterate! hope it's not too slow)
        boolean changed = true;
        while (changed) {
            changed = false;
            // logger.debug("Starting iteration:");
            methodsIt = allMethods.iterator();
            while (methodsIt.hasNext()) {
                MethodModel method = methodsIt.next();
                // we can't learn anything from non-concrete methods, and statics can't write non-static fields
                if (method.isStatic() || !method.isConcrete()) {
                    continue;
                }

                ListIterator<FieldModel> localFieldsIt = ((List<FieldModel>) localFields).listIterator();
                while (localFieldsIt.hasNext()) {
                    FieldModel localField = localFieldsIt.next();
                    List sourcesAndSinks = new ArrayList();

                    MutableDirectedGraph dataFlowSummary;
                    if (primitiveDfa != null) {
                        dataFlowSummary = primitiveDfa.getMethodInfoFlowSummary(method);
                    } else {
                        dataFlowSummary = dfa.getMethodInfoFlowSummary(method);
                    }

                    EquivalentValue node = InfoFlowAnalysis.getNodeForFieldRef(method, localField);
                    if (dataFlowSummary.containsNode(node)) {
                        sourcesAndSinks.addAll(dataFlowSummary.getSuccsOf(node));
                        sourcesAndSinks.addAll(dataFlowSummary.getPredsOf(node));
                    }

                    Iterator sourcesAndSinksIt = sourcesAndSinks.iterator();
                    if (localField.getDeclaringClass().isApplicationClass() && sourcesAndSinksIt.hasNext()) {
                        // if(!printedMethodHeading)
                        // {
                        // logger.debug(" Method: " + method.toString());
                        // printedMethodHeading = true;
                        // }
                        // logger.debug(" Field: " + localField.toString());
                    }
                    while (sourcesAndSinksIt.hasNext()) {
                        EquivalentValue sourceOrSink = (EquivalentValue) sourcesAndSinksIt.next();
                        Ref sourceOrSinkRef = (Ref) sourceOrSink.getValue();
                        boolean fieldBecomesShared = false;
                        if (sourceOrSinkRef instanceof ParameterRef) // or return ref
                        {
                            fieldBecomesShared = !parameterIsLocal(method, sourceOrSink, true);
                        } else if (sourceOrSinkRef instanceof ThisRef) // or return ref
                        {
                            fieldBecomesShared = !thisIsLocal(method, sourceOrSink);
                        } else if (sourceOrSinkRef instanceof InstanceFieldRef) {
                            fieldBecomesShared = sharedFields.contains(((FieldRef) sourceOrSinkRef).getField())
                                    || sharedInnerFields.contains(((FieldRef) sourceOrSinkRef).getField());
                        } else if (sourceOrSinkRef instanceof StaticFieldRef) {
                            fieldBecomesShared = true;
                        } else {
                            throw new RuntimeException("Unknown type of Ref in Data Flow Graph:");
                        }

                        if (fieldBecomesShared) {
                            // if(localField.getDeclaringClass().isApplicationClass())
                            // logger.debug(" Source/Sink: " + sourceOrSinkRef.toString() + " is SHARED");
                            localFieldsIt.remove();
                            sharedFields.add(localField);
                            changed = true;
                            break; // other sources don't matter now... it only takes one to taint the field
                        } else {
                            // if(localField.getDeclaringClass().isApplicationClass())
                            // logger.debug(" Source: " + sourceRef.toString() + " is local");
                        }
                    }
                }

                ListIterator<FieldModel> localInnerFieldsIt = ((List<FieldModel>) localInnerFields).listIterator();
                // boolean printedMethodHeading = false;
                while (!changed && localInnerFieldsIt.hasNext()) {
                    FieldModel localInnerField = localInnerFieldsIt.next();
                    List sourcesAndSinks = new ArrayList();

                    MutableDirectedGraph dataFlowSummary;
                    if (primitiveDfa != null) {
                        dataFlowSummary = primitiveDfa.getMethodInfoFlowSummary(method);
                    } else {
                        dataFlowSummary = dfa.getMethodInfoFlowSummary(method);
                    }

                    EquivalentValue node = InfoFlowAnalysis.getNodeForFieldRef(method, localInnerField);
                    if (dataFlowSummary.containsNode(node)) {
                        sourcesAndSinks.addAll(dataFlowSummary.getSuccsOf(node));
                        sourcesAndSinks.addAll(dataFlowSummary.getPredsOf(node));
                    }

                    Iterator sourcesAndSinksIt = sourcesAndSinks.iterator();
                    if (localInnerField.getDeclaringClass().isApplicationClass() && sourcesAndSinksIt.hasNext()) {
                        // if(!printedMethodHeading)
                        // {
                        // logger.debug(" Method: " + method.toString());
                        // printedMethodHeading = true;
                        // }
                        // logger.debug(" Field: " + localField.toString());
                    }
                    while (sourcesAndSinksIt.hasNext()) {
                        EquivalentValue sourceOrSink = (EquivalentValue) sourcesAndSinksIt.next();
                        Ref sourceOrSinkRef = (Ref) sourceOrSink.getValue();
                        boolean fieldBecomesShared = false;
                        if (sourceOrSinkRef instanceof ParameterRef) // or return ref
                        {
                            fieldBecomesShared = !parameterIsLocal(method, sourceOrSink, true);
                        } else if (sourceOrSinkRef instanceof ThisRef) // or return ref
                        {
                            fieldBecomesShared = !thisIsLocal(method, sourceOrSink);
                        } else if (sourceOrSinkRef instanceof InstanceFieldRef) {
                            fieldBecomesShared = sharedFields.contains(((FieldRef) sourceOrSinkRef).getField())
                                    || sharedInnerFields.contains(((FieldRef) sourceOrSinkRef).getField());
                        } else if (sourceOrSinkRef instanceof StaticFieldRef) {
                            fieldBecomesShared = true;
                        } else {
                            throw new RuntimeException("Unknown type of Ref in Data Flow Graph:");
                        }

                        if (fieldBecomesShared) {
                            // if(localField.getDeclaringClass().isApplicationClass())
                            // logger.debug(" Source/Sink: " + sourceOrSinkRef.toString() + " is SHARED");
                            localInnerFieldsIt.remove();
                            sharedInnerFields.add(localInnerField);
                            changed = true;
                            break; // other sources don't matter now... it only takes one to taint the field
                        } else {
                            // if(localField.getDeclaringClass().isApplicationClass())
                            // logger.debug(" Source: " + sourceRef.toString() + " is local");
                        }
                    }
                }
            }
        }

        // Print debug output
        if (dfa.printDebug()) {
            logger.debug("        Found local/shared fields for " + classModel.toString());
            logger.debug("          Local fields: ");
            Iterator<FieldModel> localsToPrintIt = localFields.iterator();
            while (localsToPrintIt.hasNext()) {
                FieldModel localToPrint = localsToPrintIt.next();
                if (localToPrint.getDeclaringClass().isApplicationClass()) {
                    logger.debug("                  " + localToPrint);
                }
            }
            logger.debug("          Shared fields: ");
            Iterator<FieldModel> sharedsToPrintIt = sharedFields.iterator();
            while (sharedsToPrintIt.hasNext()) {
                FieldModel sharedToPrint = sharedsToPrintIt.next();
                if (sharedToPrint.getDeclaringClass().isApplicationClass()) {
                    logger.debug("                  " + sharedToPrint);
                }
            }
            logger.debug("          Local inner fields: ");
            localsToPrintIt = localInnerFields.iterator();
            while (localsToPrintIt.hasNext()) {
                FieldModel localToPrint = localsToPrintIt.next();
                if (localToPrint.getDeclaringClass().isApplicationClass()) {
                    logger.debug("                  " + localToPrint);
                }
            }
            logger.debug("          Shared inner fields: ");
            sharedsToPrintIt = sharedInnerFields.iterator();
            while (sharedsToPrintIt.hasNext()) {
                FieldModel sharedToPrint = sharedsToPrintIt.next();
                if (sharedToPrint.getDeclaringClass().isApplicationClass()) {
                    logger.debug("                  " + sharedToPrint);
                }
            }
        }
    }

    private void propagate() {
        // Initialize worklist
        ArrayList<MethodModel> worklist = new ArrayList<MethodModel>();
        worklist.addAll(entryMethods);

        // Initialize set of contexts
        methodToContext = new HashMap<MethodModel, CallLocalityContext>(); // TODO: add the ability to share a map with another
        // CLOA to save memory (be
        // careful of context-sensitive call graph)
        for (MethodModel method : worklist) {
            methodToContext.put(method, getContextFor(method));
        }

        // Propagate
        Date start = new Date();
        if (dfa.printDebug()) {
            logger.debug("CLOA: Starting Propagation at " + start);
        }
        while (worklist.size() > 0) {
            ArrayList<MethodModel> newWorklist = new ArrayList<MethodModel>();
            for (MethodModel containingMethod : worklist) {
                CallLocalityContext containingContext = methodToContext.get(containingMethod);

                if (dfa.printDebug()) {
                    logger.debug("      " + containingMethod.getName() + " " + containingContext.toShortString());
                }

                // Calculate the context for each invoke stmt in the containingMethod
                Map<Stmt, CallLocalityContext> invokeToContext = new HashMap<Stmt, CallLocalityContext>();
                for (Iterator edgesIt = Scene.v().getCallGraph().edgesOutOf(containingMethod); edgesIt.hasNext(); ) {
                    Edge e = (Edge) edgesIt.next();
                    if (!e.src().getDeclaringClass().isApplicationClass() || e.srcStmt() == null) {
                        continue;
                    }
                    CallLocalityContext invokeContext;
                    if (!invokeToContext.containsKey(e.srcStmt())) {
                        invokeContext = getContextFor(e, containingMethod, containingContext);
                        invokeToContext.put(e.srcStmt(), invokeContext);
                    } else {
                        invokeContext = invokeToContext.get(e.srcStmt());
                    }

                    if (!methodToContext.containsKey(e.tgt())) {
                        methodToContext.put(e.tgt(), invokeContext);
                        newWorklist.add(e.tgt());
                    } else {
                        // logger.debug(" Merging Contexts for " + e.tgt());
                        boolean causedChange = methodToContext.get(e.tgt()).merge(invokeContext); // The contexts being merged could be
                        // from different DFAs. If
                        // so, primitive version might be
                        // bigger.
                        if (causedChange) {
                            newWorklist.add(e.tgt());
                        }
                    }
                }
            }
            worklist = newWorklist;
        }
        long longTime = ((new Date()).getTime() - start.getTime()) / 100;
        float time = (longTime) / 10.0f;
        if (dfa.printDebug()) {
            logger.debug("CLOA: Ending Propagation after " + time + "s");
        }
    }

    public CallLocalityContext getMergedContext(MethodModel method) {
        if (methodToContext.containsKey(method)) {
            return methodToContext.get(method);
        }

        return null;
    }

    private CallLocalityContext getContextFor(Edge e, MethodModel containingMethod, CallLocalityContext containingContext) {
        // get new called method and calling context
        InvokeExpr ie;
        if (e.srcStmt().containsInvokeExpr()) {
            ie = e.srcStmt().getInvokeExpr();
        } else {
            ie = null;
        }

        MethodModel callingMethod = e.tgt();
        CallLocalityContext callingContext = new CallLocalityContext(dfa.getMethodInfoFlowSummary(callingMethod).getNodes());
        // just
        // keeps
        // a
        // map
        // from
        // NODE
        // to
        // SHARED/LOCAL

        // We will use the containing context that we have to determine if base/args are local
        if (callingMethod.isConcrete()) {
            Body b = containingMethod.retrieveActiveBody();

            // check base
            if (ie != null && ie instanceof InstanceInvokeExpr iie) {
              if (!containingMethod.isStatic() && iie.getBase().equivTo(b.getThisLocal())) {
                    // calling another method on same object... basically copy the previous context
                    Iterator<Object> localRefsIt = containingContext.getLocalRefs().iterator();
                    while (localRefsIt.hasNext()) {
                        EquivalentValue rEqVal = (EquivalentValue) localRefsIt.next();
                        Ref r = (Ref) rEqVal.getValue();
                        if (r instanceof InstanceFieldRef) {
                            EquivalentValue newRefEqVal
                                    = InfoFlowAnalysis.getNodeForFieldRef(callingMethod, ((FieldRef) r).getFieldRef().resolve());
                            if (callingContext.containsField(newRefEqVal)) {
                                callingContext.setFieldLocal(newRefEqVal); // must make a new eqval for the method getting called
                            }
                        } else if (r instanceof ThisRef) {
                            callingContext.setThisLocal();
                        }
                    }
                } else if (SmartMethodLocalObjectsAnalysis.isObjectLocal(dfa, containingMethod, containingContext, iie.getBase())) {
                    // calling a method on a local object
                    callingContext.setAllFieldsLocal();
                    callingContext.setThisLocal();
                } else {
                    // calling a method on a shared object
                    callingContext.setAllFieldsShared();
                    callingContext.setThisShared();
                }
            } else {
                callingContext.setAllFieldsShared();
                callingContext.setThisShared();
            }

            // check args
            if (ie == null) {
                callingContext.setAllParamsShared();
            } else {
                for (int param = 0; param < ie.getArgCount(); param++) {
                    if (SmartMethodLocalObjectsAnalysis.isObjectLocal(dfa, containingMethod, containingContext, ie.getArg(param))) {
                        callingContext.setParamLocal(param);
                    } else {
                        callingContext.setParamShared(param);
                    }
                }
            }
        } else {
            // The only conservative solution for a bodyless method is to assume everything is shared
            callingContext.setAllFieldsShared();
            callingContext.setThisShared();
            callingContext.setAllParamsShared();
        }
        return callingContext;
    }

    public CallLocalityContext getContextFor(MethodModel sm) {
        return getContextFor(sm, false);
    }

    private CallLocalityContext getContextFor(MethodModel sm, boolean includePrimitiveDataFlowIfAvailable) {
        CallLocalityContext context;
        if (includePrimitiveDataFlowIfAvailable) {
            context = new CallLocalityContext(primitiveDfa.getMethodInfoFlowSummary(sm).getNodes());
        } else {
            context = new CallLocalityContext(dfa.getMethodInfoFlowSummary(sm).getNodes());
        }

        // Set context for every parameter that is shared
        for (int i = 0; i < sm.getParameterCount(); i++) // no need to worry about return value...
        {
            EquivalentValue paramEqVal = InfoFlowAnalysis.getNodeForParameterRef(sm, i);
            if (parameterIsLocal(sm, paramEqVal, includePrimitiveDataFlowIfAvailable)) {
                context.setParamLocal(i);
            } else {
                context.setParamShared(i);
            }
        }

        for (FieldModel sf : getLocalFields()) {
            EquivalentValue fieldRefEqVal = InfoFlowAnalysis.getNodeForFieldRef(sm, sf);
            context.setFieldLocal(fieldRefEqVal);
        }

        for (FieldModel sf : getSharedFields()) {
            EquivalentValue fieldRefEqVal = InfoFlowAnalysis.getNodeForFieldRef(sm, sf);
            context.setFieldShared(fieldRefEqVal);
        }
        return context;
    }

    public boolean isObjectLocal(Value localOrRef, MethodModel sm) {
        return isObjectLocal(localOrRef, sm, false);
    }

    private boolean isObjectLocal(Value localOrRef, MethodModel sm, boolean includePrimitiveDataFlowIfAvailable) {
        if (localOrRef instanceof StaticFieldRef) {
            return false;
        }

        if (dfa.printDebug()) {
            logger.debug("      CLOA testing if " + localOrRef + " is local in " + sm);
        }

        SmartMethodLocalObjectsAnalysis smloa = getMethodLocalObjectsAnalysis(sm, includePrimitiveDataFlowIfAvailable);
        if (localOrRef instanceof InstanceFieldRef ifr) {
          if (ifr.getBase().equivTo(smloa.getThisLocal())) {
                return isFieldLocal(ifr.getFieldRef().resolve());
            } else {
                // if referred object is local, then find out if field is local in that object
                if (isObjectLocal(ifr.getBase(), sm, includePrimitiveDataFlowIfAvailable)) {
                    boolean retval = loa.isFieldLocalToParent(ifr.getFieldRef().resolve());
                    if (dfa.printDebug()) {
                        logger.debug("      " + (retval ? "local" : "shared"));
                    }
                    return retval;
                } else {
                    if (dfa.printDebug()) {
                        logger.debug("      shared");
                    }
                    return false;
                }
            }
        }
        // TODO Prepare a CallLocalityContext!
        CallLocalityContext context = getContextFor(sm);

        boolean retval = smloa.isObjectLocal(localOrRef, context);
        if (dfa.printDebug()) {
            logger.debug("      " + (retval ? "local" : "shared"));
        }
        return retval;
    }

    public SmartMethodLocalObjectsAnalysis getMethodLocalObjectsAnalysis(MethodModel sm) {
        return getMethodLocalObjectsAnalysis(sm, false);
    }

    private SmartMethodLocalObjectsAnalysis getMethodLocalObjectsAnalysis(MethodModel sm,
                                                                          boolean includePrimitiveDataFlowIfAvailable) {
        if (includePrimitiveDataFlowIfAvailable && primitiveDfa != null) {
            Body b = sm.retrieveActiveBody();
            UnitGraph g = ExceptionalUnitGraphFactory.createExceptionalUnitGraph(b);
            return new SmartMethodLocalObjectsAnalysis(g, primitiveDfa);
        } else if (!methodToMethodLocalObjectsAnalysis.containsKey(sm)) {
            // Analyze this method
            Body b = sm.retrieveActiveBody();
            UnitGraph g = ExceptionalUnitGraphFactory.createExceptionalUnitGraph(b);
            SmartMethodLocalObjectsAnalysis smloa = new SmartMethodLocalObjectsAnalysis(g, dfa);
            methodToMethodLocalObjectsAnalysis.put(sm, smloa);
        }
        return methodToMethodLocalObjectsAnalysis.get(sm);
    }

    private boolean fieldIsInitiallyLocal(FieldModel field) {
        if (field.isStatic()) {
            // Static fields are always shared
            return false;
        } else if (field.isPrivate()) {
            // Private fields may be local
            return true;
        } else {
            return !externalFields.contains(field);
        }
    }

    protected List<FieldModel> getSharedFields() {
        return (List<FieldModel>) sharedFields.clone();
    }

    protected List<FieldModel> getLocalFields() {
        return (List<FieldModel>) localFields.clone();
    }

    public List<FieldModel> getInnerSharedFields() {
        return sharedInnerFields;
    }

    protected boolean isFieldLocal(FieldModel field) {
        return localFields.contains(field);
    }

    protected boolean isFieldLocal(EquivalentValue fieldRef) {
        return localFields.contains(((SootFieldRef) fieldRef.getValue()).resolve());
    }

    public boolean parameterIsLocal(MethodModel method, EquivalentValue parameterRef) {
        return parameterIsLocal(method, parameterRef, false);
    }

    protected boolean parameterIsLocal(MethodModel method, EquivalentValue parameterRef,
                                       boolean includePrimitiveDataFlowIfAvailable) {
        if (dfa.printDebug() && method.getDeclaringClass().isApplicationClass()) {
            logger.debug("        Checking PARAM " + parameterRef + " for " + method);
        }

        // Check if param is primitive or ref type
        ParameterRef param = (ParameterRef) parameterRef.getValue();
        if (!(param.getType() instanceof RefLikeType) && (!dfa.includesPrimitiveInfoFlow() || method.getName().equals("<init>")))
        // TODO
        // fix
        {
            if (dfa.printDebug() && method.getDeclaringClass().isApplicationClass()) {
                logger.debug("          PARAM is local (primitive)");
            }
            return true; // primitive params are always considered local
        }

        // Check if method is externally called
        List extClassCalls = uf.getExtCalls(classModel);
        Iterator extClassCallsIt = extClassCalls.iterator();
        while (extClassCallsIt.hasNext()) {
            Pair extCall = (Pair) extClassCallsIt.next();
            Stmt s = (Stmt) extCall.getO2();
            if (s.getInvokeExpr().getMethodRef().resolve() == method) {
                if (dfa.printDebug() && method.getDeclaringClass().isApplicationClass()) {
                    logger.debug("          PARAM is shared (external access)");
                }
                return false; // If so, assume it's params are shared
            }
        }

        // For each internal call, check if arg is local or shared
        List intClassCalls = uf.getIntCalls(classModel);
        Iterator intClassCallsIt = intClassCalls.iterator(); // returns all internal accesses
        while (intClassCallsIt.hasNext()) {
            Pair intCall = (Pair) intClassCallsIt.next();
            MethodModel containingMethod = (MethodModel) intCall.getO1();
            Stmt s = (Stmt) intCall.getO2();
            InvokeExpr ie = s.getInvokeExpr();
            if (ie.getMethodRef().resolve() == method) {
                if (((ParameterRef) parameterRef.getValue()).getIndex() >= 0) {
                    if (!isObjectLocal(ie.getArg(((ParameterRef) parameterRef.getValue()).getIndex()), containingMethod,
                            includePrimitiveDataFlowIfAvailable)) // WORST
                    // CASE
                    // SCENARIO
                    // HERE
                    // IS
                    // INFINITE
                    // RECURSION!
                    {
                        if (dfa.printDebug() && method.getDeclaringClass().isApplicationClass()) {
                            logger.debug("          PARAM is shared (internal propagation)");
                        }
                        return false; // if arg is shared for any internal call, then param is shared
                    }
                } else {
                    if (s instanceof DefinitionStmt) {
                        Value obj = ((DefinitionStmt) s).getLeftOp();
                        if (!isObjectLocal(obj, containingMethod, includePrimitiveDataFlowIfAvailable)) // WORST CASE SCENARIO HERE IS
                        // INFINITE RECURSION!
                        {
                            if (dfa.printDebug() && method.getDeclaringClass().isApplicationClass()) {
                                logger.debug("          PARAM is shared (internal propagation)");
                            }
                            return false; // if arg is shared for any internal call, then param is shared
                        }
                    }
                }
            }
        }
        if (dfa.printDebug() && method.getDeclaringClass().isApplicationClass()) {
            logger.debug("          PARAM is local SO FAR (internal propagation)");
        }
        return true; // if argument is always local, then parameter is local
    }

    // TODO: SOUND/UNSOUND???
    protected boolean thisIsLocal(MethodModel method, EquivalentValue thisRef) {
        return true;
    }
}
