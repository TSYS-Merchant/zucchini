/**
 * *******************************************************************************
 *
 * [ MultiLoader.java ]
 *
 * COPYRIGHT (c) 1999 - 2013 by Merchant Warehouse, Boston, MA USA. All rights
 * reserved. This material contains unpublished, copyrighted work including
 * confidential and proprietary information of Merchant Warehouse.
 *
 ********************************************************************************
 */
package com.merchantwarehouse.qa.cukes;

import cucumber.runtime.io.ClasspathResourceLoader;
import cucumber.runtime.io.DelegatingResourceIteratorFactory;
import cucumber.runtime.io.FileResourceLoader;
import cucumber.runtime.io.Resource;
import cucumber.runtime.io.ResourceLoader;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MultiLoader implements ResourceLoader {

    private static final String CLASSPATH_SCHEME = "classpath:";

    private final Class clazz;
    private final ClassLoader classLoader;
    private final ClasspathResourceLoader classpath;
    private final FileResourceLoader fs;

    public MultiLoader(final Class clazz, final ClassLoader classLoader) {
        this.clazz = clazz;
        this.classLoader = classLoader;
        classpath = new ClasspathResourceLoader(classLoader);
        fs = new FileResourceLoader();
    }

    @Override
    public Iterable<Resource> resources(final String path, final String suffix) {
        if (isClasspathPath(path)) {
            String pathStrippedOfClasspath = stripClasspathPrefix(path);
            String classPackage = clazz.getPackage().getName().replace('.', '/');

            if (pathStrippedOfClasspath.equals(classPackage)) {
                // only load the @Resource associated with this.clazz
                try {
                    Enumeration<URL> resources = classLoader.getResources(pathStrippedOfClasspath);
                    while (resources.hasMoreElements()) {
                        Iterator<Resource> iter_ = new DelegatingResourceIteratorFactory().createIterator(resources.nextElement(), pathStrippedOfClasspath, suffix);
                        while (iter_.hasNext()) {
                            Resource r = iter_.next();
                            if (r.getClassName().endsWith(clazz.getName())) {
                                return Arrays.asList(r);
                            }
                        }
                    }
                } catch (IOException ioe) {
                    Logger.getLogger(MultiLoader.class.getName()).log(Level.SEVERE, null, ioe);
                }

                return Collections.EMPTY_LIST;
            } else {
                return classpath.resources(pathStrippedOfClasspath, suffix);
            }
        } else {
            return fs.resources(path, suffix);
        }
    }

    private static String packageName(String gluePath) {
        if (isClasspathPath(gluePath)) {
            gluePath = stripClasspathPrefix(gluePath);
        }
        return gluePath.replace('/', '.').replace('\\', '.');
    }

    private static boolean isClasspathPath(final String path) {
        return path.startsWith(CLASSPATH_SCHEME);
    }

    private static String stripClasspathPrefix(final String path) {
        return path.substring(CLASSPATH_SCHEME.length());
    }

}
