To Configure:
Create a testng.xml file.  Here's an example.  The URL for the html page used to test the generated page object must
be specified. If not, the browser will open but WebDriver will not have a url to go fetch the test page.
And to run a suite testng requires the test classes to be specified.

<!DOCTYPE suite SYSTEM "http://testng.org/testng-1.0.dtd" >
<suite name="Suite 1" verbose="3" parallel="tests" thread-count="10">
    <test name="TagOnlyNoAttributesTest">
    <parameter name="url" value="http://localhost:8080/testhtml/htmltests/"/>
    <classes>
       <class name="com.pagerunner.tests.TagOnlyNoAttributesTest" />
    </classes>
    </test>
</suite>


To run:
From IntelliJ:
You must tell TestNg where to find the testng.xml file.
- First, be sure you have the TestNg plugin.  Then, open the
TestNg Run Config dialog from Main Menu -> Run -> EditConfigurations.  Be sure you have selected a TestNg configuration.
If you don't have one, you can create one but right-clicking a TestNg test class and trying to run it, the run config
will ge automatically created.
- Select the Suite radio button.
- Then, click the '...' button for the Suite text input box and using the file dialog select the testng.xml file.


Must specify a testng.xml file to configure the tests.
