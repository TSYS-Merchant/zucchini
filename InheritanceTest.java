package com.merchantwarehouse.qa.cukes;

import cucumber.api.java.en.Then;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertEquals;
import org.junit.experimental.categories.Category;

/**
 *
 * @author dominicl
 */
@RunWith(Cucumber.class)
@Category(com.merchantwarehouse.qa.junit.categories.SmokeTest.class)
//@Cucumber.Options(features="DifferentlyNamedFeature.feature")
public class InheritanceTest extends InheritanceTestBase {

    @Then("^the total should be (\\d+)$")
    public void then_the_total_should_be(Integer total) {
        assertEquals("total", total, getTotal());
    }
}
