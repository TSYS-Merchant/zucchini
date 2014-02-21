#Intro
Zucchini is a set of extensions to the [cucumber-jvm](https://github.com/cucumber/cucumber-jvm) Behavior-driven Development framework. These extensions make it significantly easier to use the popular [Behavior-driven Development](http://en.wikipedia.org/wiki/Behavior-driven_development) framework.

[Cucumber](http://cukes.info/) lets software development teams describe how software should behave in plain text. The text is written in a [business-readable domain-specific language](http://www.martinfowler.com/bliki/BusinessReadableDSL.html) and serves as documentation, automated tests and development-aid - all rolled into one format.

Merchant Warehouse’s zucchini framework offers tighter integration with the [Java programming language](http://www.java.com/en/) and the [JUnit testing framework](http://junit.org/), providing developers and testers alike with a much more natural programming experience.

Some of zucchini’s notable features include:
* Better integration with JUnit. Junit’s @BeforeClass and @AfterClass annotations work.
* Tight coupling between [Gherkin](https://github.com/cucumber/cucumber/wiki/Gherkin) feature files and your JUnit test classes. Bind your JUnit test class to one or more feature files as you please. No longer do you have to worry about Cucumber’s dreaded “duplicate step definitions found” error.
* Object inheritance works. You can define your step definitions in a base class and inherit them in child classes.
* No need for empty JUnit test runner classes. Declare your @Cucumber annotation right on your step definition files.
* Get HTML and JSON outputs by default, for easy integration into your [CI systems](http://en.wikipedia.org/wiki/Continuous_integration), such as [Jenkins](http://jenkins-ci.org/).

With Merchant Warehouse’s zucchini, behavior driven development in Java is easy.

#How it works

Zucchini is API-compatible with cucumber-jvm. This means that if your testing team is already using Cucumber, making the switch to zucchini should be as easy as pie. All you have to do is:
- Pull in zucchini AND cucumber 1.1.x into your Maven POM.
- In your JUnit test files, import zucchini instead of cucumber.
- Profit.

# Documentation

Because zucchini is API-compatible with Cucumber, please refer to [Cucumber’s documentation](http://cukes.info/platforms.html). Please see our Examples section below for how zucchini deviates ever so slightly from Cucumber.

#Examples

https://github.com/merchantwarehouse/automation-framework/tree/master/automationFramework/src/test/java/com/merchantwarehouse/qa/cukes

https://github.com/merchantwarehouse/automation-framework/tree/master/automationFramework/src/test/resources/com/merchantwarehouse/qa/cukes

#Example output

Like Cucumber proper, zucchini integrates nicely into a variety of visualization tools, such as Jenkins. There are a number of tools to visualize Cucumber's results, including:

* https://github.com/masterthought/jenkins-cucumber-jvm-reports-plugin
* https://wiki.jenkins-ci.org/display/JENKINS/Cucumber+Test+Result+Plugin

## Cucumber feature overview
![cucumber feature overview, using jenkins' cucumber jvm reports plugin ](https://raw.github.com/merchantwarehouse/zucchini/master/.README/CucumberFeatureOverview.png)

## Cucumber feature results
![cucumber feature results, using jenkins' cucumber jvm reports plugin](https://raw.github.com/merchantwarehouse/zucchini/master/.README/CucumberFeatureResult.png)
 
 
# Downloading / Installation
Currently, Merchant Warehouse’s zucchini is only available as a source download. If you’d like to provide a Maven package, that'd be very welcome.

#Contributing
We love contributions! Please send [pull requests](https://help.github.com/articles/using-pull-requests) our way. All that we ask is that you please include unit tests with all of your pull requests.

#Getting help
We also love bug reports & feature requests. You can file bugs and feature requests in our Github Issue Tracker. Please consider including the following information when you file a ticket:
* What version you're using
* What command or code you ran
* What output you saw
* How the problem can be reproduced. A small Visual Studio project zipped up or code snippet that demonstrates or reproduces the issue is always appreciated.

You can also always find help on the zucchini Google Group.
