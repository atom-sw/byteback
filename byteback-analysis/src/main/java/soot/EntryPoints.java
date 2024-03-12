package soot;

import byteback.analysis.model.ClassModel;
import byteback.analysis.model.MethodModel;
import soot.util.NumberedString;
import soot.util.StringNumberer;

import java.util.*;

/**
 * Returns the various potential entry points of a Java program.
 *
 * @author Ondrej Lhotak
 */
public class EntryPoints {

    final NumberedString sigMain;
    final NumberedString sigFinalize;
    final NumberedString sigExit;
    final NumberedString sigClinit;
    final NumberedString sigInit;
    final NumberedString sigStart;
    final NumberedString sigRun;
    final NumberedString sigObjRun;
    final NumberedString sigForName;

    public EntryPoints(Singletons.Global g) {
        final StringNumberer subSigNumberer = Scene.v().getSubSigNumberer();

        sigMain = subSigNumberer.findOrAdd(JavaMethods.SIG_MAIN);
        sigFinalize = subSigNumberer.findOrAdd(JavaMethods.SIG_FINALIZE);
        sigExit = subSigNumberer.findOrAdd(JavaMethods.SIG_EXIT);
        sigClinit = subSigNumberer.findOrAdd(JavaMethods.SIG_CLINIT);
        sigInit = subSigNumberer.findOrAdd(JavaMethods.SIG_INIT);
        sigStart = subSigNumberer.findOrAdd(JavaMethods.SIG_START);
        sigRun = subSigNumberer.findOrAdd(JavaMethods.SIG_RUN);
        sigObjRun = subSigNumberer.findOrAdd(JavaMethods.SIG_OBJ_RUN);
        sigForName = subSigNumberer.findOrAdd(JavaMethods.SIG_FOR_NAME);
    }

    public static EntryPoints v() {
        return G.v().soot_EntryPoints();
    }

    protected void addMethod(List<MethodModel> set, ClassModel cls, NumberedString methodSubSig) {
        MethodModel sm = cls.getMethodUnsafe(methodSubSig);
        if (sm != null) {
            set.add(sm);
        }
    }

    protected void addMethod(List<MethodModel> set, String methodSig) {
        final Scene sc = Scene.v();
        if (sc.containsMethod(methodSig)) {
            set.add(sc.getMethod(methodSig));
        }
    }

    /**
     * Returns only the application entry points, not including entry points invoked implicitly by the VM.
     */
    public List<MethodModel> application() {
        List<MethodModel> ret = new ArrayList<MethodModel>();
        final Scene sc = Scene.v();
        if (sc.hasMainClass()) {
            ClassModel mainClass = sc.getMainClass();
            addMethod(ret, mainClass, sigMain);
            for (MethodModel clinit : clinitsOf(mainClass)) {
                ret.add(clinit);
            }
        }
        return ret;
    }

    /**
     * Returns only the entry points invoked implicitly by the VM.
     */
    public List<MethodModel> implicit() {
        List<MethodModel> ret = new ArrayList<MethodModel>();

        addMethod(ret, JavaMethods.INITIALIZE_SYSTEM_CLASS);
        addMethod(ret, JavaMethods.THREAD_GROUP_INIT);
        // addMethod( ret, "<java.lang.ThreadGroup: void
        // remove(java.lang.Thread)>");
        addMethod(ret, JavaMethods.THREAD_EXIT);
        addMethod(ret, JavaMethods.THREADGROUP_UNCAUGHT_EXCEPTION);
        // addMethod( ret, "<java.lang.System: void
        // loadLibrary(java.lang.String)>");
        addMethod(ret, JavaMethods.CLASSLOADER_INIT);
        addMethod(ret, JavaMethods.CLASSLOADER_LOAD_CLASS_INTERNAL);
        addMethod(ret, JavaMethods.CLASSLOADER_CHECK_PACKAGE_ACC);
        addMethod(ret, JavaMethods.CLASSLOADER_ADD_CLASS);
        addMethod(ret, JavaMethods.CLASSLOADER_FIND_NATIVE);
        addMethod(ret, JavaMethods.PRIV_ACTION_EXC_INIT);
        // addMethod( ret, "<java.lang.ref.Finalizer: void
        // register(java.lang.Object)>");
        addMethod(ret, JavaMethods.RUN_FINALIZE);
        addMethod(ret, JavaMethods.THREAD_INIT_RUNNABLE);
        addMethod(ret, JavaMethods.THREAD_INIT_STRING);
        return ret;
    }

    /**
     * Returns all the entry points.
     */
    public List<MethodModel> all() {
        List<MethodModel> ret = new ArrayList<MethodModel>();
        ret.addAll(application());
        ret.addAll(implicit());
        return ret;
    }

    /**
     * Returns a list of all static initializers.
     */
    public List<MethodModel> clinits() {
        List<MethodModel> ret = new ArrayList<MethodModel>();
        for (ClassModel cl : Scene.v().getClasses()) {
            addMethod(ret, cl, sigClinit);
        }
        return ret;
    }

    /**
     * Returns a list of all constructors taking no arguments.
     */
    public List<MethodModel> inits() {
        List<MethodModel> ret = new ArrayList<MethodModel>();
        for (ClassModel cl : Scene.v().getClasses()) {
            addMethod(ret, cl, sigInit);
        }
        return ret;
    }

    /**
     * Returns a list of all constructors.
     */
    public List<MethodModel> allInits() {
        List<MethodModel> ret = new ArrayList<MethodModel>();
        for (ClassModel cl : Scene.v().getClasses()) {
            for (MethodModel m : cl.getMethodModels()) {
                if ("<init>".equals(m.getName())) {
                    ret.add(m);
                }
            }
        }
        return ret;
    }

    /**
     * Returns a list of all concrete methods of all application classes.
     */
    public List<MethodModel> methodsOfApplicationClasses() {
        List<MethodModel> ret = new ArrayList<MethodModel>();
        for (ClassModel cl : Scene.v().getApplicationClasses()) {
            for (MethodModel m : cl.getMethodModels()) {
                if (m.isConcrete()) {
                    ret.add(m);
                }
            }
        }
        return ret;
    }

    /**
     * Returns a list of all concrete main(String[]) methods of all application classes.
     */
    public List<MethodModel> mainsOfApplicationClasses() {
        List<MethodModel> ret = new ArrayList<MethodModel>();
        for (ClassModel cl : Scene.v().getApplicationClasses()) {
            MethodModel m = cl.getMethodUnsafe("void main(java.lang.String[])");
            if (m != null && m.isConcrete()) {
                ret.add(m);
            }
        }
        return ret;
    }

    /**
     * Returns a list of all clinits of class cl and its superclasses.
     */
    public Iterable<MethodModel> clinitsOf(ClassModel cl) {
        // Do not create an actual list, since this method gets called quite often
        // Instead, callers usually just want to iterate over the result.
        MethodModel init = cl.getMethodUnsafe(sigClinit);
        ClassModel superClass = cl.getSuperclassUnsafe();
        // check super classes until finds a constructor or no super class there anymore.
        while (init == null && superClass != null) {
            init = superClass.getMethodUnsafe(sigClinit);
            superClass = superClass.getSuperclassUnsafe();
        }
        if (init == null) {
            return Collections.emptyList();
        }
        MethodModel initStart = init;
        return new Iterable<MethodModel>() {

            @Override
            public Iterator<MethodModel> iterator() {
                return new Iterator<MethodModel>() {
                    MethodModel current = initStart;

                    @Override
                    public MethodModel next() {
                        if (!hasNext()) {
                            throw new NoSuchElementException();
                        }
                        MethodModel n = current;

                        // Pre-fetch the next element
                        current = null;
                        ClassModel currentClass = n.getDeclaringClass();
                        while (true) {
                            ClassModel superClass = currentClass.getSuperclassUnsafe();
                            if (superClass == null) {
                                break;
                            }

                            MethodModel m = superClass.getMethodUnsafe(sigClinit);
                            if (m != null) {
                                current = m;
                                break;
                            }

                            currentClass = superClass;
                        }

                        return n;
                    }

                    @Override
                    public boolean hasNext() {
                        return current != null;
                    }
                };
            }
        };
    }
}
