

Two Modules: Code and Test
--------------------------




Command-line Params
-------------------
- I typically run with the -url and -dest options.  I set these in the IDE's run params input box.  For example

    -url http://<my remote page> -dest <Path to My Projects Folder>/PageObjectRunner/src/main/java/com/selgentests/pageobjects

  This defaults to generating the page object code although there are other options, see below.

  For example: Here's one I commonly ran when testing the app:

  -url http://www.cnn.com -dest /Users/pgrandje/IdeaProjects/POHelper/PageObjectRunner/src/main/java/com/selgentests/pageobjects

- Currently I'm not running this from the command-line yet, although if you're comfortable setting up a typical java runtime
environment you should only need to be sure your CLASSPATH is set correctly to run from the command-line.
only from the IDE.

- Other options
-generate code | hints | codefromhints
-codeShell or -codeShellTemplate -- for setting the filepath of the code template file.  This is the file that defines
 the other shell, such as the class name, for the page object.
-tagSwitch or tagSwitchTemplate -- specifies the filepath for the tags to be used for code generation and the code template
 snippets for code generation.
-loc or -locator -- specifies the strategy to use for writing WebElement locators.
-defMem or -defaultMemberName -- specifies the string to use by default for WebElement members when no useful string
 from the corresponding HTML tag can be used.
-h or -help -- displays command-line help.




Code Template Files
--------------------
- Configuration files are in the RunFolder/resources and is not the root of the project,
it's a sub-folder and is set in the IDE's run configuration settings.  Also the logs get written to the RunFolder.



Log Files
--------------------
- These are stored in the folder POHelper/RunFolder.  There is extensive logging throughout the app.  All the major
modules will have their own log file.





