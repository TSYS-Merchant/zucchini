/**
 * *****************************************************************************
 *
 * [ Cucumber.java ]
 *
 * COPYRIGHT (c) 1999 - 2013 by Merchant Warehouse, Boston, MA USA. All rights reserved. This material contains
 * unpublished, copyrighted work including confidential and proprietary information of Merchant Warehouse.
 *
 *******************************************************************************
 */
package com.merchantwarehouse.zucchini;

import cucumber.api.CucumberOptions;
import cucumber.api.SnippetType;
import cucumber.runtime.ClassFinder;
import cucumber.runtime.Env;
import cucumber.runtime.Runtime;
import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.RuntimeOptionsFactory;
import cucumber.runtime.io.ResourceLoaderClassFinder;
import cucumber.runtime.junit.FeatureRunner;
import cucumber.runtime.junit.JUnitReporter;
import cucumber.runtime.model.CucumberFeature;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;

/**
 * Classes annotated with {@code @RunWith(Cucumber.class)} will run a Cucumber Feature.
 *
 * <p>
 * This class is meant as a drop-in replacement for the official Cucumber Junit class. This class differs from its
 * upstream cousin in the following respects:</p>
 *
 * <ul>
 * <li>It allows for co-mingling of JUnit and Cucumber features. Junit's @org.junit.BeforeClass, @org.junit.AfterClass,
 * @org.junit.Before, and @org.junit.After annotations will work just fine.
 * <li>No more useless empty Java files that only set up a Cucumber JUnit runner
 * <li>It prevents feature steps from "bleeding over" into other files
 * <li>Multiple Java files in the same package can implement the same steps
 * <li>You can keep your step definitions in a parent class. Inheritance works.
 * <li>You can tie your step definitions to any arbitrary feature file.
 * </ul>
 *
 * <p>
 * All other features should work as expected.</p>
 *
 * <p>
 * This class relies heavily on conventions. If no feature paths are specified, Zucchini will look for a
 * {@code .feature} file on the classpath, using the same name as the annotated class ({@code .java} substituted by
 * {@code .feature}). Make sure that your feature file and your annotated Java file have the same name, as this class
 * will tie them together.
 * </p>
 * Additional hints can be given to Zucchini by annotating the class with {@link Options}.
 *
 * @see Options
 */
public class Zucchini extends ParentRunner<FeatureRunner> {

    /**
     * Our JUnit Reporter.
     */
    private final JUnitReporter jUnitReporter;

    /**
     * The Gherkin features we want to run.
     */
    private final List<FeatureRunner> children = new ArrayList<>();

    /**
     * The Cucumber runtime.
     */
    private final Runtime runtime;

    /**
     * Constructor called by JUnit.
     *
     * @param clazz the class with the @RunWith annotation.
     * @throws InitializationError if we failed to initialize Zucchini Zucchini
     */
    public Zucchini(final Class clazz) throws InitializationError {
        super(clazz);
        ClassLoader classLoader = clazz.getClassLoader();

        RuntimeOptions runtimeOptions = createRuntimeOptions(clazz);
        removeDupesFromGlue(runtimeOptions);

        bindClassToFeatureFile(runtimeOptions, clazz);

        MultiLoader resourceLoader = new MultiLoader(clazz, classLoader);
        ClassFinder classFinder = new ResourceLoaderClassFinder(resourceLoader, classLoader);
        runtime = new Runtime(resourceLoader, classLoader, Arrays.asList(new JavaBackend(classFinder, clazz)),
                runtimeOptions);

        jUnitReporter = new JUnitReporter(runtimeOptions.reporter(classLoader), runtimeOptions.formatter(classLoader),
                runtimeOptions.isStrict());
        addChildren(runtimeOptions.cucumberFeatures(resourceLoader));
    }

    @Override
    public final List<FeatureRunner> getChildren() {
        return children;
    }

    @Override
    protected final Description describeChild(final FeatureRunner child) {
        return child.getDescription();
    }

    @Override
    protected final void runChild(final FeatureRunner child,
            final RunNotifier notifier) {
        child.run(notifier);
    }

    @Override
    public final void run(final RunNotifier notifier) {
        super.run(notifier);
        jUnitReporter.done();
        jUnitReporter.close();
        runtime.printSummary(System.out);
    }

    /**
     * Adds a Gherkin feature to the list of features we wish to run.
     *
     * @param cucumberFeatures the list of Gherkin features you wish to run
     * @throws InitializationError blah blah blah
     */
    private void addChildren(final List<CucumberFeature> cucumberFeatures)
            throws InitializationError {
        for (CucumberFeature cucumberFeature : cucumberFeatures) {
            children.add(new FeatureRunner(cucumberFeature, runtime, jUnitReporter));
        }
    }

    /**
     * Removes all duplicate classpaths from our "glue". This is a hack that allows us to have a step definitions class
     * that extends a parent class
     *
     * @param runtimeOptions the Cucumber RuntimeOptions we're modifying
     */
    private void removeDupesFromGlue(final RuntimeOptions runtimeOptions) {
        List<String> allGlue = new ArrayList<>();
        for (String glue : runtimeOptions.getGlue()) {
            if (!allGlue.contains(glue)) {
                allGlue.add(glue);
            }
        }

        runtimeOptions.getGlue().clear();
        runtimeOptions.getGlue().addAll(allGlue);
    }

    private static final String FEATURE_FILE_SUFFIX = ".feature";

    /**
     * Binds the Java Class and Gherkin Feature File together.
     *
     * This enforces a convention of Scenario.class and Scenario.feature being linked together.
     *
     * @param runtimeOptions the Cucumber RuntimeOptions we're modifying
     * @param clazz the class that we've annotated with JUnit @RunWith
     */
    private void bindClassToFeatureFile(final RuntimeOptions runtimeOptions,
            final Class clazz) throws InitializationError {
        List<String> featurePaths = new ArrayList<>();

        for (String featureFileName : runtimeOptions.getFeaturePaths()) {
            // if someone explicitly specified feature file(s) to run, run them
            if (featureFileName.endsWith(FEATURE_FILE_SUFFIX)) {
                featurePaths.add(featureFileName);
            }
        }

        if (featurePaths.isEmpty()) {
            // no features were explicitly specified. bind the class to a feature file with the same name
            String featureFileName = clazz.getSimpleName() + FEATURE_FILE_SUFFIX;
            featurePaths.add(featureFileName);
        }

        runtimeOptions.getFeaturePaths().clear();

        for (String featureFileName : featurePaths) {
            URL featureFileURL = clazz.getResource(featureFileName);
            if (featureFileURL == null) {
                // this should be an unchecked exception because there's nothing the
                // caller can do to safely recover from this error
                throw new InitializationError("Could not find Gherkin feature file: " + featureFileName);
            } else {
                runtimeOptions.getFeaturePaths().add(featureFileURL.getFile());
            }
        }
    }

    /**
     * Creates new Cucumber RuntimeOptions based on this class's annotations.
     *
     * Enforces MW's policy that we always emit HTML and JSON reports, in addition to the JUnit reports. This is for
     * debugging purposes and for integration into our Jenkins CI environment.
     *
     * @param clazz the class you've annotated
     * @return a new RuntimeOptions
     * @throws InitializationError if we cannot initialize Cucumber
     */
    private RuntimeOptions createRuntimeOptions(final Class clazz)
            throws InitializationError {
        try {
            RuntimeOptionsFactory runtimeOptionsFactory;

            runtimeOptionsFactory = new RuntimeOptionsFactory(clazz,
                    new Class[]{CucumberOptions.class, Options.class,
                        cucumber.api.junit.Cucumber.Options.class});

            // this is definitely a brittle hack... internally, Cucumber options
            // are maintained as a list of command line arguments. The
            // RuntimeOptionsFactory has a method that converts from an
            // Options.class or CucumberOptions.class into a list of command
            // line arguments. this method is, unfortunately, private. for us to
            // do the same (without reimplementing cucumber's
            // RuntimeOptionsFactory), we merely "peek up its skirt", grab a
            // handle to the buildArgsFromOptions() method, and append our
            // default formatters to the list.
            // WARNING: this voids the warranty...
            Method m = runtimeOptionsFactory.getClass().getDeclaredMethod("buildArgsFromOptions");
            m.setAccessible(true);
            List<String> args = (List<String>) m.invoke(runtimeOptionsFactory);

            // all merchant warehouse cucumber tests will want (at least) HTML
            // and JSON reports
            applyDefaultFormatting(clazz, args);

            return new RuntimeOptions(new Env("cucumber-jvm"), args.toArray(new String[]{}));
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException ex) {
            Logger.getLogger(Zucchini.class.getName()).log(Level.SEVERE, null, ex);
            throw new InitializationError(ex);
        }
    }

    private static final String CUCUMBER_FORMAT_COMMAND_LINE_ARG = "--format";

    /**
     * all merchant warehouse cucumber tests will want (at least) HTML and JSON reports
     *
     * <p>
     * enforce a convention that the data ends up in a folder named "build/cucumber-reports/$classname". this obviates
     * having to write a lot of boilerplate @Cucumber.Options(format=...) as part of every Stepdefs class</p>
     *
     * @param clazz the class with the JUnit @RunWith annotation
     * @param args the list of command line arguments that configures Cucumber
     */
    private void applyDefaultFormatting(final Class clazz,
            final List<String> args) {
        int formatIndex = args.indexOf(CUCUMBER_FORMAT_COMMAND_LINE_ARG);
        int lastFormatIndex = args.lastIndexOf(CUCUMBER_FORMAT_COMMAND_LINE_ARG);

        // there is a single --format argument
        if (formatIndex > -1 && formatIndex == lastFormatIndex) {
            // check to see if Cucumber was "helpful" and appended a "null" formatter for us...
            if ("null".equalsIgnoreCase(args.get(formatIndex + 1))) {
                // if so, remove it
                args.remove(formatIndex + 1);
                args.remove(formatIndex);
            }
        }

        if (!args.contains(CUCUMBER_FORMAT_COMMAND_LINE_ARG)) {
            // apply the default formatting only if no formatting options were explicitly requested
            String className = clazz.getName();

            args.add(CUCUMBER_FORMAT_COMMAND_LINE_ARG);
            args.add(String.format("html:build/cucumber-reports/%1$s/html", className));

            args.add(CUCUMBER_FORMAT_COMMAND_LINE_ARG);
            args.add(String.format("json:build/cucumber-reports/%1$s/cucumber.json", className));
        }
    }

    /**
     * This annotation can be used to give additional hints to the {@link Zucchini} runner about what to run. It
     * provides similar options to the Cucumber command line used by {@link cucumber.api.cli.Main}
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE})
    public static @interface Options {

        /**
         * @return true if this is a dry run
         */
        boolean dryRun() default false;

        /**
         * @return true if strict mode is enabled (fail if there are undefined or pending steps)
         */
        boolean strict() default true;

        /**
         * @return the paths to the feature(s)
         */
        String[] features() default {};

        /**
         * @return where to look for glue code (stepdefs and hooks)
         */
        String[] glue() default {};

        /**
         * @return what tags in the features should be executed
         */
        String[] tags() default {};

        /**
         * @return what formatter(s) to use
         */
        String[] format() default {};

        /**
         * @return whether or not to use monochrome output
         */
        boolean monochrome() default false;

        /**
         * Specify a patternfilter for features or scenarios
         *
         * @return a list of patterns
         */
        String[] name() default {};

        String dotcucumber() default "";

        /**
         * @return what format should the snippets use. underscore, camelcase
         */
        SnippetType snippets() default SnippetType.UNDERSCORE;
    }
}
