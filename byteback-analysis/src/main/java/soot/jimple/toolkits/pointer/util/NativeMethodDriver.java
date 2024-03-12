package soot.jimple.toolkits.pointer.util;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Feng Qian
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import byteback.analysis.model.MethodModel;
import soot.jimple.toolkits.pointer.nativemethods.*;
import soot.jimple.toolkits.pointer.representations.ReferenceVariable;

import java.util.HashMap;

public class NativeMethodDriver {
    private static final Logger logger = LoggerFactory.getLogger(NativeMethodDriver.class);

    public NativeMethodDriver(NativeHelper helper) {
        cnameToSim.put("java.lang.Object", new JavaLangObjectNative(helper));
        cnameToSim.put("java.lang.System", new JavaLangSystemNative(helper));
        cnameToSim.put("java.lang.Runtime", new JavaLangRuntimeNative(helper));
        cnameToSim.put("java.lang.Shutdown", new JavaLangShutdownNative(helper));
        cnameToSim.put("java.lang.String", new JavaLangStringNative(helper));
        cnameToSim.put("java.lang.Float", new JavaLangFloatNative(helper));
        cnameToSim.put("java.lang.Double", new JavaLangDoubleNative(helper));
        cnameToSim.put("java.lang.StrictMath", new JavaLangStrictMathNative(helper));
        cnameToSim.put("java.lang.Throwable", new JavaLangThrowableNative(helper));
        cnameToSim.put("java.lang.Class", new JavaLangClassNative(helper));
        cnameToSim.put("java.lang.Package", new JavaLangPackageNative(helper));
        cnameToSim.put("java.lang.Thread", new JavaLangThreadNative(helper));
        cnameToSim.put("java.lang.ClassLoader", new JavaLangClassLoaderNative(helper));
        cnameToSim.put("java.lang.ClassLoader$NativeLibrary", new JavaLangClassLoaderNativeLibraryNative(helper));
        cnameToSim.put("java.lang.SecurityManager", new JavaLangSecurityManagerNative(helper));

        cnameToSim.put("java.lang.reflect.Field", new JavaLangReflectFieldNative(helper));
        cnameToSim.put("java.lang.reflect.Array", new JavaLangReflectArrayNative(helper));
        cnameToSim.put("java.lang.reflect.Method", new JavaLangReflectMethodNative(helper));
        cnameToSim.put("java.lang.reflect.Constructor", new JavaLangReflectConstructorNative(helper));
        cnameToSim.put("java.lang.reflect.Proxy", new JavaLangReflectProxyNative(helper));

        cnameToSim.put("java.io.FileInputStream", new JavaIoFileInputStreamNative(helper));
        cnameToSim.put("java.io.FileOutputStream", new JavaIoFileOutputStreamNative(helper));
        cnameToSim.put("java.io.ObjectInputStream", new JavaIoObjectInputStreamNative(helper));
        cnameToSim.put("java.io.ObjectOutputStream", new JavaIoObjectOutputStreamNative(helper));
        cnameToSim.put("java.io.ObjectStreamClass", new JavaIoObjectStreamClassNative(helper));
        cnameToSim.put("java.io.FileSystem", new JavaIoFileSystemNative(helper));
        cnameToSim.put("java.io.FileDescriptor", new JavaIoFileDescriptorNative(helper));

        cnameToSim.put("java.util.ResourceBundle", new JavaUtilResourceBundleNative(helper));
        cnameToSim.put("java.util.TimeZone", new JavaUtilTimeZoneNative(helper));

        cnameToSim.put("java.util.jar.JarFile", new JavaUtilJarJarFileNative(helper));

        cnameToSim.put("java.util.zip.CRC32", new JavaUtilZipCRC32Native(helper));
        cnameToSim.put("java.util.zip.Inflater", new JavaUtilZipInflaterNative(helper));
        cnameToSim.put("java.util.zip.ZipFile", new JavaUtilZipZipFileNative(helper));
        cnameToSim.put("java.util.zip.ZipEntry", new JavaUtilZipZipEntryNative(helper));

        cnameToSim.put("java.security.AccessController", new JavaSecurityAccessControllerNative(helper));

        cnameToSim.put("java.net.InetAddress", new JavaNetInetAddressNative(helper));
        cnameToSim.put("java.net.InetAddressImpl", new JavaNetInetAddressImplNative(helper));

        cnameToSim.put("sun.misc.Signal", new SunMiscSignalNative(helper));
        cnameToSim.put("sun.misc.NativeSignalHandler", new SunMiscSignalHandlerNative(helper));
        cnameToSim.put("sun.misc.Unsafe", new SunMiscUnsafeNative(helper));
    }

    protected final HashMap<String, NativeMethodClass> cnameToSim = new HashMap<String, NativeMethodClass>(100);
    private final boolean DEBUG = false;

    /**
     * The entry point of native method simulation.
     *
     * @param method,    must be a native method
     * @param thisVar,   the variable represent @this, it can be null if the method is static
     * @param returnVar, the variable represent @return it is null if the method has no return
     * @param params,    array of parameters.
     */
    public boolean process(MethodModel method, ReferenceVariable thisVar, ReferenceVariable returnVar,
                           ReferenceVariable[] params) {

        String cname = method.getDeclaringClass().getName();
        NativeMethodClass clsSim = cnameToSim.get(cname);

        // logger.debug(""+method.toString());
        if (clsSim == null) {
            // logger.warn("it is unsafe to simulate the method ");
            // logger.debug(" "+method.toString());
            // throw new NativeMethodNotSupportedException(method);
            return false;
        } else {

            try {
                clsSim.simulateMethod(method, thisVar, returnVar, params);
                return true;
            } catch (NativeMethodNotSupportedException e) {
                if (DEBUG) {
                    logger.warn("it is unsafe to simulate the method ");
                    logger.debug("         " + method);
                }
            }
            return false;
        }
    }
}
