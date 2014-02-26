package com.merchantwarehouse.zucchini;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertEquals;

/**
 * This class tests the integration with various junit features (@BeforeClass and @AfterClass). It also has the side
 * effect of testing that the same Gherkin rules and the same Cucumber Step Definitions may appear in more than one file
 * without them conflicting.
 *
 * @author dominicl
 */
@RunWith(Zucchini.class)
@Zucchini.Options(features = "DifferentlyNamedFeature.feature")
public class JunitIntegrationTest {

    // counters of how many times cucumber's before/after were called. these should be 2x each.
    private static int countOfTimesCucumbersBeforeWasCalled = 0;
    private static int countOfTimesCucumbersAfterWasCalled = 0;

    // counters of how many times junit's before/after were called. these should be 2x each.
    private static int countOfTimesJunitsBeforeWasCalled = 0;
    private static int countOfTimesJunitsAfterWasCalled = 0;

    // counters of how many times junit's beforeclass/afterclass were called. these should be 1x each.
    private static int countOfTimesBeforeClassWasCalled = 0;
    private static int countOfTimesAfterClassWasCalled = 0;

    // counters of how many times given/when/then were called. given the above feature file, these should be 2x each.
    // this is important, because there are 2 Gherkin feature files with the same rules, so the stock version of cucumber
    // would run these rules 4x each. actually, it would fail to run at all because both this class and the InheritanceTest
    // and GlueClassTest define identical step definitions. we'll validate the counters in @AfterClass
    private static int countOfTimesGivenWasCalled = 0;
    private static int countOfTimesWhenWasCalled = 0;
    private static int countOfTimesThenWasCalled = 0;

    @cucumber.api.java.Before
    public void cucumberBefore() {
        countOfTimesCucumbersBeforeWasCalled++;
    }

    @cucumber.api.java.After
    public void cucumberAfter() {
        countOfTimesCucumbersAfterWasCalled++;
    }

    @org.junit.Before
    public void junitBefore() {
        countOfTimesJunitsBeforeWasCalled++;
    }

    @org.junit.After
    public void junitAfter() {
        countOfTimesJunitsAfterWasCalled++;
    }

    @BeforeClass
    public static void beforeClass() {
        countOfTimesBeforeClassWasCalled++;
        System.out.format("Called beforeClass() %d times", countOfTimesBeforeClassWasCalled).println();
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

        assertEquals("countOfTimesGivenWasCalled", 2, countOfTimesGivenWasCalled);
        assertEquals("countOfTimesWhenWasCalled", 2, countOfTimesWhenWasCalled);
        assertEquals("countOfTimesThenWasCalled", 2, countOfTimesThenWasCalled);
    }

    @Then("^the total should be (\\d+)$")
    public void then_the_total_should_be(Integer total) {
        countOfTimesThenWasCalled++;
    }

    @Given("^two integers (\\d+) and (\\d+)$")
    public final void given_two_integers(final Integer a, final Integer b) {
        countOfTimesGivenWasCalled++;
    }

    @When("^you add them together$")
    public final void when_you_add_them_together() {
        countOfTimesWhenWasCalled++;
    }
}
