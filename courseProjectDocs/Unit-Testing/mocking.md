## Test Rationale

### LstFileLoaderTest.java

Mostly covering every branch and every caught exception to ensure that what they expect to happen, does happen. 

testBomFile and testInvalidUTF8 simply check file format shenanigans and that the expected errors/warnings are properly thrown. 
Needed to mock the logging system for this to yank the outputs

testIOE simply checks that a checked for IOException is actually caught and managed when triggered. 
quick mock to pick up the error from the login system.  


rightDestinationTest is a much more complicated test, requiring multiple mocks and stubs to simulate the http client behavior
and double check that we're sending a curl GET request to the intended destination with the intended body text. 

properBodyReturnTest is the other end of rightDestinationTest, checking that when the target URI sends back data, we actually
receive and parse it properly. 

noLoadTest checks what happens when SettingsHandler::isLoadURLs is false, which should just spit out a message, 
in which case we mock the message outputter and check that it does that correctly. 

clientIoeTest and clientInterruptTest are much the same as the other exception tests, but we check that if the https client
returns these, and that they're managed properly at that level. Mostly just mocking the logger again to ensure exceptions are logged correctly


## Mocking Strategy
Mocking strategy is pretty much "ensure that there's no chance for pieces outside of our system to influence the system".

If something that would require an external file or site(let's say, a real URI), we need to mock our own instead. 

Furthermore, in regards to places where specific responses are relevant, stubs are prudent to simulate behavior instead of 
having to set up complex states that may influence testing when those change unexpectedly. Being able to explicitly define
state and results is rather valuable. 


### GameModeFileLoaderTest.java

These tests are mainly focused on the different paths through `loadGameModeLstFile`, specifically covering that files can be loaded from the expected game mode directory, ensuring that it falls back to the default directory when needed, and that a failed load results in trying the default file instead.

loadGameModeWhereRequestedFileExists checks the normal case or 'happy path' scenario where the game mode file exists and should be loaded directly.

loadGameModeFileWithFallback verifies the fallback behavior when the game mode file isn't found, making sure it goes for the default game mode directory instead.

loadGameModeFallsBackWhenFailsToLoad checks the other fallback case where the file exists but it throws a persistence layer exception when loaded. The test stubs the loader to cause the exception and then checks that the default file is attempted.

### Mocking Strategy

LstLineFileLoader needs to be mocked, since the tests are meant to check the logic in `GameModeFileLoader` rather than actually parsing LST files. Stubbing the loader allows the throwing of the persistence layer exception, without needing to create a broken LST file.

ConfigurationSettings is statically mocked so the tests can control which temp directory is treated as the systems directory. This keeps the tests isolated from the actual PCGen and lets the temporary test files control exactly which branch is taken.
