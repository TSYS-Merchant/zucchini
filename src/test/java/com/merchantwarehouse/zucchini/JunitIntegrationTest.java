package com.merchantwarehouse.zucchini;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertEquals;

/**
 * This class tests the integration with various junit features (@BeforeClass and @AfterClass). It also has the side
 * effect of testing that the same Gherkin rules and the same Cucumber Step Definitions may appear in more than one file
 * without them conflicting. It also demonstrates that object inheritance works (i.e. you can inherit step definitions)
 * and that you can bind your test to any arbitrary Gherkin feature file(s).
 *
 * @author dominicl
 */
@RunWith(Zucchini.class)
@Zucchini.Options(features = "DifferentlyNamedFeature.feature")
public class JunitIntegrationTest extends InheritanceTest {

    // counters of how many times cucumber's before/after were called. these should be 2x each.
    private static int countOfTimesCucumbersBeforeWasCalled = 0;
    private static int countOfTimesCucumbersAfterWasCalled = 0;

    // counters of how many times junit's before/after were called. these should be 2x each.
    private static int countOfTimesJunitsBeforeWasCalled = 0;
    private static int countOfTimesJunitsAfterWasCalled = 0;

    // counters of how many times junit's beforeclass/afterclass were called. these should be 1x each.
    private static int countOfTimesBeforeClassWasCalled = 0;
    private static int countOfTimesAfterClassWasCalled = 0;

    @cucumber.api.java.Before
    public final void cucumberBefore() {
        countOfTimesCucumbersBeforeWasCalled++;
    }

    @cucumber.api.java.After
    public final void cucumberAfter() {
        countOfTimesCucumbersAfterWasCalled++;
    }

    @org.junit.Before
    public final void junitBefore() {
        countOfTimesJunitsBeforeWasCalled++;
    }

    @org.junit.After
    public final void junitAfter() {
        countOfTimesJunitsAfterWasCalled++;
    }

    @BeforeClass
    public static void beforeClass() {
        countOfTimesBeforeClassWasCalled++;
    }

    @AfterClass
    public static void afterClass() {
        countOfTimesAfterClassWasCalled++;
        System.out.format("Called afterClass() %d times", countOfTimesAfterClassWasCalled).println();

        assertEquals("countOfTimesBeforeClassWasCalled", 1, countOfTimesBeforeClassWasCalled);
        assertEquals("countOfTimesAfterClassWasCalled", 1, countOfTimesAfterClassWasCalled);

        assertEquals("countOfTimesCucumbersBeforeWasCalled", 2, countOfTimesCucumbersBeforeWasCalled);
        assertEquals("countOfTimesCucumbersAfterWasCalled", 2, countOfTimesCucumbersAfterWasCalled);

        assertEquals("countOfTimesJunitsBeforeWasCalled", 2, countOfTimesJunitsBeforeWasCalled);
        assertEquals("countOfTimesJunitsAfterWasCalled", 2, countOfTimesJunitsAfterWasCalled);

        /**
         * counters of how many times given/when/then were called. given the above feature file, these should be 2x
         * each. this is important, because there are 2 Gherkin feature files with the same rules, so the stock version
         * of cucumber would run these rules 4x each. actually, it would fail to run at all because both this class and
         * the InheritanceTest and GlueClassTest define identical step definitions.
         */
        assertEquals("countOfTimesGivenWasCalled", 2, countOfTimesGivenWasCalled);
        assertEquals("countOfTimesWhenWasCalled", 2, countOfTimesWhenWasCalled);
        assertEquals("countOfTimesThenWasCalled", 2, countOfTimesThenWasCalled);
    }

    // the "given", "when", and "then" are all defined in the parent classes
}
