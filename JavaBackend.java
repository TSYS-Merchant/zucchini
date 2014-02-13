package com.merchantwarehouse.qa.cukes;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.runtime.Backend;
import cucumber.runtime.ClassFinder;
import cucumber.runtime.CucumberException;
import cucumber.runtime.DuplicateStepDefinitionException;
import cucumber.runtime.Glue;
import cucumber.runtime.Reflections;
import cucumber.runtime.UnreportedStepExecutor;
import cucumber.runtime.Utils;
import cucumber.runtime.java.ObjectFactory;
import cucumber.runtime.snippets.FunctionNameSanitizer;
import cucumber.runtime.snippets.SnippetGenerator;
import gherkin.formatter.model.Step;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.regex.Pattern;

public class JavaBackend implements Backend {

    private SnippetGenerator snippetGenerator = new SnippetGenerator(new JavaSnippet());
    private final ObjectFactory objectFactory;

    private final MethodScanner methodScanner;
    private Glue glue;

    /**
     * The constructor called by reflection by default.
     *
     * @param classFinder
     */
    public JavaBackend(ClassFinder classFinder, Class parentClazz) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        methodScanner = new MethodScanner(classFinder);
        objectFactory = new DefaultJavaObjectFactory();
        
        objectFactory.addClass(parentClazz);
    }

    @Override
    public void loadGlue(Glue glue, List<String> gluePaths) {
        this.glue = glue;
        methodScanner.scan(this, gluePaths);
    }

    /**
     * Convenience method for frameworks that wish to load glue from methods explicitly (possibly found with a different
     * mechanism than Cucumber's built-in classpath scanning).
     *
     * @param glue where stepdefs and hooks will be added.
     * @param method a candidate method.
     * @param glueCodeClass the class implementing the method. Must not be a subclass of the class implementing the
     * method.
     */
    public void loadGlue(Glue glue, Method method, Class<?> glueCodeClass) {
        this.glue = glue;
        methodScanner.scan(this, method, glueCodeClass);
    }

    @Override
    public void setUnreportedStepExecutor(UnreportedStepExecutor executor) {
        //Not used here yet
    }

    @Override
    public void buildWorld() {
        objectFactory.start();
    }

    @Override
    public void disposeWorld() {
        objectFactory.stop();
    }

    @Override
    public String getSnippet(Step step, FunctionNameSanitizer fns) {
        return snippetGenerator.getSnippet(step, fns);
    }

    void addStepDefinition(Annotation annotation, Class clazz, Method method) {
        try {
            glue.addStepDefinition(new JavaStepDefinition(clazz, method, pattern(annotation), timeoutMillis(annotation), objectFactory));
        } catch (DuplicateStepDefinitionException e) {
            throw e;
        } catch (Throwable e) {
            throw new CucumberException(e);
        }
    }

    private Pattern pattern(Annotation annotation) throws Throwable {
        Method regexpMethod = annotation.getClass().getMethod("value");
        String regexpString = (String) Utils.invoke(annotation, regexpMethod, 0);
        return Pattern.compile(regexpString);
    }

    private long timeoutMillis(Annotation annotation) throws Throwable {
        Method regexpMethod = annotation.getClass().getMethod("timeout");
        return (Long) Utils.invoke(annotation, regexpMethod, 0);
    }

    void addHook(Annotation annotation, Class clazz, Method method) {
        if (annotation.annotationType().equals(Before.class)) {
            String[] tagExpressions = ((Before) annotation).value();
            long timeout = ((Before) annotation).timeout();
            glue.addBeforeHook(new JavaHookDefinition(clazz, method, tagExpressions, ((Before) annotation).order(), timeout, objectFactory));
        } else {
            String[] tagExpressions = ((After) annotation).value();
            long timeout = ((After) annotation).timeout();
            glue.addAfterHook(new JavaHookDefinition(clazz, method, tagExpressions, ((After) annotation).order(), timeout, objectFactory));
        }
    }

    private static String getMultipleObjectFactoryLogMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("More than one Cucumber ObjectFactory was found in the classpath\n\n");
        sb.append("You probably may have included, for instance, cucumber-spring AND cucumber-guice as part of\n");
        sb.append("your dependencies. When this happens, Cucumber falls back to instantiating the\n");
        sb.append("DefaultJavaObjectFactory implementation which doesn't provide IoC.\n");
        sb.append("In order to enjoy IoC features, please remove the unnecessary dependencies from your classpath.\n");
        return sb.toString();
    }
}
