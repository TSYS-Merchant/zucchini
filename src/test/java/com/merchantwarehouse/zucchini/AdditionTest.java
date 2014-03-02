package com.merchantwarehouse.zucchini;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import cucumber.api.java.lu.a;
import static org.junit.Assert.assertEquals;
import org.junit.runner.RunWith;

/**
 * This class tests that zucchini will tightly bind a Java class to a Gherkin feature file with the same name.
 *
 * @author dominicl
 */
@RunWith(Zucchini.class)
public class AdditionTest {

    // instance data
    private int a;
    private int b;
    private int total;

    @Given("^two integers (\\d+) and (\\d+)$")
    public final void given_two_integers(final Integer a, final Integer b) {
        this.a = a;
        this.b = b;
    }

    @When("^you add them together$")
    public final void when_you_add_them_together() {
        total = a + b;
    }

    @Then("^the total should be (\\d+)$")
    public void then_the_total_should_be(Integer total) {
        assertEquals("total", total.intValue(), this.total);
    }
}
