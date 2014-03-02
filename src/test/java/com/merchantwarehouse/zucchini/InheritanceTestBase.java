package com.merchantwarehouse.zucchini;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;

/**
 *
 * @author dominicl
 */
public class InheritanceTestBase {

    // instance data
    private int a;
    private int b;
    private int total;

    /**
     * Counter of how many times "given" was called.
     */
    protected static int countOfTimesGivenWasCalled = 0;
    
    /**
     * Counter of how many times "when" was called.
     */
    protected static int countOfTimesWhenWasCalled = 0;

    @Given("^two integers (\\d+) and (\\d+)$")
    public final void given_two_integers(final Integer a, final Integer b) {
        countOfTimesGivenWasCalled++;

        this.a = a;
        this.b = b;
    }

    @When("^you add them together$")
    public final void when_you_add_them_together() {
        countOfTimesWhenWasCalled++;

        total = a + b;
    }

    public final Integer getTotal() {
        return total;
    }
}
