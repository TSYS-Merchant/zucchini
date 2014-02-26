package com.merchantwarehouse.zucchini;

import cucumber.api.java.en.Then;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertEquals;

/**
 * This class tests whether we can inherit cucumber stepdefs from a parent class. It also indirectly tests that if you
 * don't specify a feature file via the @Cucumber.Options annotation, that you bind to InheritanceTest.feature
 *
 * @author dominicl
 */
@RunWith(Zucchini.class)
public class InheritanceTest extends InheritanceTestBase {

    @Then("^the total should be (\\d+)$")
    public void then_the_total_should_be(Integer total) {
        assertEquals("total", total, getTotal());
    }
}
