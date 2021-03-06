package com.ibm.wala.j2ee;

/*******************************************************************************
 * Copyright (c) 2002 - 2006 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IClassLoader;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.impl.DefaultEntrypoint;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.types.Descriptor;
import com.ibm.wala.types.MethodReference;
import com.ibm.wala.types.TypeName;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.util.collections.HashMapFactory;
import com.ibm.wala.util.collections.HashSetFactory;
import com.ibm.wala.util.strings.Atom;
import com.ibm.wala.util.strings.ImmutableByteArray;
import com.ibm.wala.util.strings.UTF8Convert;

/**
 * This class provides an iterator of entrypoints that are implementations of servlet methods.
 */
public class ServletEntrypoints implements Iterable<Entrypoint>, EJBConstants {

  static final boolean DEBUG = false;

  public final static Atom doFilterName = Atom.findOrCreateUnicodeAtom("doFilter");

  private final static Descriptor doFilterDesc = Descriptor
      .findOrCreateUTF8("(Ljavax/servlet/ServletRequest;Ljavax/servlet/ServletResponse;Ljavax/servlet/FilterChain;)V");

  private final static Atom destroyName = Atom.findOrCreateUnicodeAtom("destroy");

  private final static Descriptor destroyDesc = Descriptor.findOrCreateUTF8("()V");

  private final static Atom getServletConfigName = Atom.findOrCreateUnicodeAtom("getServletConfig");

  private final static Descriptor getServletConfigDesc = Descriptor.findOrCreateUTF8("()Ljavax/servlet/ServletConfig;");

  private final static Atom getServletInfoName = Atom.findOrCreateUnicodeAtom("getServletInfo");

  private final static Descriptor getServletInfoDesc = Descriptor.findOrCreateUTF8("()Ljava/lang/String;");

  private final static Atom initName = Atom.findOrCreateUnicodeAtom("init");

  private final static Descriptor initDesc = Descriptor.findOrCreateUTF8("(Ljavax/servlet/ServletConfig;)V");

  public final static Atom serviceName = Atom.findOrCreateUnicodeAtom("service");

  private final static byte[] serviceDescAtom = UTF8Convert
      .toUTF8("(Ljavax/servlet/ServletRequest;Ljavax/servlet/ServletResponse;)V");

  private final static Descriptor serviceDesc = Descriptor.findOrCreate(new ImmutableByteArray(serviceDescAtom));

  public final static Atom finalizeName = Atom.findOrCreateUnicodeAtom("finalize");

  private final static Descriptor finalizeDesc = Descriptor.findOrCreateUTF8("()V");

  private final static Atom[] servletMethodNames = { destroyName, getServletConfigName, getServletInfoName, initName, serviceName,
      finalizeName };

  private final static Descriptor[] servletMethodDescs = { destroyDesc, getServletConfigDesc, getServletInfoDesc, initDesc,
      serviceDesc, finalizeDesc };

  private final static Atom[] servletFilterMethodNames = { doFilterName };

  private final static Descriptor[] servletFilterMethodDescs = { doFilterDesc };

  private final static TypeName servletName = TypeName.string2TypeName("Ljavax/servlet/Servlet");

  public final static TypeReference Servlet = TypeReference.findOrCreate(ClassLoaderReference.Extension, servletName);

  private final static TypeName servletFilterName = TypeName.string2TypeName("Ljavax/servlet/Filter");

  public final static TypeReference ServletFilter = TypeReference.findOrCreate(ClassLoaderReference.Extension, servletFilterName);

  private final static TypeName httpServletName = TypeName.string2TypeName("Ljavax/servlet/http/HttpServlet");

  public final static TypeReference HttpServlet = TypeReference.findOrCreate(ClassLoaderReference.Extension, httpServletName);

  private final static TypeName httpJspBaseName = TypeName.string2TypeName("Lcom/ibm/ws/webcontainer/jsp/runtime/HttpJspBase");

  public final static TypeReference HttpJspBase = TypeReference.findOrCreate(ClassLoaderReference.Extension, httpJspBaseName);

  final static TypeName servletRequest = TypeName.string2TypeName("Ljavax/servlet/ServletRequest");

  public final static TypeReference ServletRequest = TypeReference.findOrCreate(ClassLoaderReference.Extension, servletRequest);

  public final static TypeName servletResponse = TypeName.string2TypeName("Ljavax/servlet/ServletResponse");

  public final static TypeReference ServletResponse = TypeReference.findOrCreate(ClassLoaderReference.Extension, servletResponse);

  final static TypeName httpServletRequest = TypeName.string2TypeName("Ljavax/servlet/http/HttpServletRequest");

  public final static TypeReference HttpServletRequest = TypeReference.findOrCreate(ClassLoaderReference.Extension, servletRequest);

  public final static TypeName httpServletResponse = TypeName.string2TypeName("Ljavax/servlet/http/HttpServletResponse");

  public final static TypeReference HttpServletResponse = TypeReference.findOrCreate(ClassLoaderReference.Extension,
      servletResponse);

  private final static TypeName servletContext = TypeName.string2TypeName("Ljavax/servlet/ServletContext");

  public final static TypeReference ServletContext = TypeReference.findOrCreate(ClassLoaderReference.Extension, servletContext);

  private final static TypeName servletConfig = TypeName.string2TypeName("Ljavax/servlet/ServletConfig");

  public final static TypeReference ServletConfig = TypeReference.findOrCreate(ClassLoaderReference.Extension, servletConfig);

  private final static TypeName walaHttpServletRequest = TypeName
      .string2TypeName("Lcom/ibm/wala/model/javax/servlet/http/HttpServletRequest");

  public final static TypeReference WalaHttpServletRequest = TypeReference.findOrCreate(ClassLoaderReference.Extension,
      walaHttpServletRequest);

  private final static TypeName walaHttpServletResponse = TypeName
      .string2TypeName("Lcom/ibm/wala/model/javax/servlet/http/HttpServletResponse");

  public final static TypeReference WalaHttpServletResponse = TypeReference.findOrCreate(ClassLoaderReference.Extension,
      walaHttpServletResponse);

  private final static TypeName walaServletContextModel = TypeName
      .string2TypeName("Lcom/ibm/wala/model/javax/servlet/ServletContext");

  public final static TypeReference WalaServletContextModel = TypeReference.findOrCreate(ClassLoaderReference.Extension,
      walaServletContextModel);

  private final static TypeName walaServletConfigModel = TypeName
      .string2TypeName("Lcom/ibm/wala/model/javax/servlet/ServletConfig");

  public final static TypeReference WalaServletConfigModel = TypeReference.findOrCreate(ClassLoaderReference.Extension,
      walaServletConfigModel);

  private final static TypeName actionServlet = TypeName.string2TypeName("Lorg/apache/struts/action/ActionServlet");

  public final static MethodReference servletInit = MethodReference.findOrCreate(Servlet, initName, initDesc);

  private Set<Entrypoint> entrypoints = HashSetFactory.make();

  /**
   * Set of servlet (or ServletFilter) implementations found.
   */
  private Set<IClass> servlets = HashSetFactory.make();

  /**
   * Mapping of TypeName -> TypeReference; this map controls selection of concrete types for parameters to some servlet methods.
   */
  private final static HashMap<TypeName, TypeReference> concreteParameterMap = HashMapFactory.make(5);
  static {
    concreteParameterMap.put(httpServletRequest, WalaHttpServletRequest);
    concreteParameterMap.put(httpServletResponse, WalaHttpServletResponse);
    concreteParameterMap.put(servletRequest, WalaHttpServletRequest);
    concreteParameterMap.put(servletResponse, WalaHttpServletResponse);
    concreteParameterMap.put(servletContext, WalaServletContextModel);
    concreteParameterMap.put(servletConfig, WalaServletConfigModel);
  }

  private final AnalysisScope scope;

  private final IClassHierarchy cha;

  private boolean isInitialized;

  /**
   * @param scope scope of analysis
   * @param cha loaded class hierarchy
   */
  public ServletEntrypoints(AnalysisScope scope, IClassHierarchy cha) {
    this.scope = scope;
    this.cha = cha;
  }

  private void init() {
    if (isInitialized) {
      return;
    }
    isInitialized = true;
    TypeReference actionServletType = TypeReference.findOrCreate(scope.getApplicationLoader(), actionServlet);
    IClass actionServletClass = cha.lookupClass(actionServletType);

    IClass servlet = cha.lookupClass(Servlet);
    IClass servletFilter = cha.lookupClass(ServletFilter);
    assert servlet != null;
    assert servletFilter != null;
    for (Iterator<IClass> it = getCandidateEntryClasses(cha); it.hasNext();) {
      IClass klass = (IClass) it.next();
      if (klass != null) {
        if (DEBUG) {
          System.err.println((getClass() + " consider " + klass));
        }
        if (cha.lookupClass(klass.getReference()) == null) {
          continue;
        }
        // ignore struts ActionServlets
        if (cha.lookupClass(actionServletType) != null) {
          if (cha.isSubclassOf(klass, actionServletClass)) {
            continue;
          }
        }
        if (cha.implementsInterface(klass, servlet)) {
          servlets.add(klass);
          final TypeReference type = klass.getReference();

          for (int i = 0; i < servletMethodNames.length; i++) {
            Atom name = servletMethodNames[i];
            Descriptor desc = servletMethodDescs[i];
            MethodReference M = MethodReference.findOrCreate(type, name, desc);
            IMethod m = cha.resolveMethod(M);
            if (cha.resolveMethod(M) != null) {
              entrypoints.add(new DefaultEntrypoint(m, cha) {

                /**
                 * Assume all ServletRequest and ServletResponse are HTTP flavor.
                 */
                public TypeReference[] getParameterTypes(int i) {
                  if (i == 0) {
                    // "this" pointer
                    return new TypeReference[] { type };
                  } else {
                    TypeReference[] tArray = super.getParameterTypes(i);
                    assert tArray.length == 1;
                    TypeReference T = tArray[0];
                    TypeName n = T.getName();
                    TypeReference Tp = getConcreteServletParameterType(n);
                    if (Tp != null) {
                      T = Tp;
                    }
                    return new TypeReference[] { T };
                  }
                }
              });
            }
          }
        }
        if (cha.implementsInterface(klass, servletFilter)) {
          servlets.add(klass);
          final TypeReference type = klass.getReference();

          for (int i = 0; i < servletFilterMethodNames.length; i++) {
            Atom name = servletFilterMethodNames[i];
            Descriptor desc = servletFilterMethodDescs[i];
            MethodReference M = MethodReference.findOrCreate(type, name, desc);
            IMethod m = cha.resolveMethod(M);
            if (cha.resolveMethod(M) != null) {
              entrypoints.add(new DefaultEntrypoint(m, cha) {

                /**
                 * Assume all ServletRequest and ServletResponse are HTTP flavor.
                 */
                public TypeReference[] getParameterTypes(int i) {
                  if (i == 0) {
                    // "this" pointer
                    return new TypeReference[] { type };
                  } else {
                    TypeReference[] tArray = super.getParameterTypes(i);
                    assert tArray.length == 1;
                    TypeReference T = tArray[0];
                    TypeName n = T.getName();
                    TypeReference Tp = concreteParameterMap.get(n);
                    if (Tp != null) {
                      T = Tp;
                    }
                    return new TypeReference[] { T };
                  }
                }
              });
            }
          }
        }
      }
    }
  }

  /**
   * return the set of classes that should be examined when searching for servlet entrypoints.
   */
  protected Iterator<IClass> getCandidateEntryClasses(IClassHierarchy cha) {
    IClassLoader appLoader = cha.getLoader(ClassLoaderReference.Application);
    return appLoader.iterateAllClasses();
  }

  public Iterator<Entrypoint> iterator() {
    init();
    return entrypoints.iterator();
  }

  public String toString() {
    init();
    StringBuffer result = new StringBuffer();
    result.append("Servlets:");
    Iterator<IClass> it = servlets.iterator();
    if (it.hasNext()) {
      while (it.hasNext()) {
        result.append("\n   ");
        result.append(it.next());
      }
    } else {
      result.append("   none");
    }
    return result.toString();
  }

  /**
   * return the concrete type to use for a parameter to a servlet with declared type n.
   */
  public static TypeReference getConcreteServletParameterType(TypeName n) {
    TypeReference Tp = concreteParameterMap.get(n);
    return Tp;
  }

  public static boolean isStandardServlet(IClass klass) {
    IClassHierarchy cha = klass.getClassHierarchy();
    TypeReference actionServletType = TypeReference.findOrCreate(ClassLoaderReference.Application, actionServlet);
    IClass actionServletClass = cha.lookupClass(actionServletType);

    IClass servlet = cha.lookupClass(Servlet);
    IClass servletFilter = cha.lookupClass(ServletFilter);
    if (servlet == null) {
      throw new IllegalStateException("javax.servlet.Servlet not loaded.");
    }
    if (servletFilter == null) {
      throw new IllegalStateException("javax.servlet.ServletFilter not loaded.");
    }
    if (cha.implementsInterface(klass, servlet) || cha.implementsInterface(klass, servletFilter)) {
      // ignore struts ActionServlets
      if (actionServletClass != null) {
        if (cha.isSubclassOf(klass, actionServletClass)) {
          return false;
        }
      }
      return true;
    }
    return false;
  }
}
