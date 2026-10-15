## Mutation Testing Report

### Tool added
We added PIT via gradle for this project. Config is mostly stock standard, copied from their docs and mixed with the existing
flag set for testing already present in the build.gradle file. 

### Initial mutation score report
> Line Coverage (for mutated classes only): 1573/19757 (8%)
> 
> 2352 tests examined
> 
> Generated 11300 mutations Killed 389 (3%)
> 
> Mutations with no coverage 10602. Test strength 56%
> 
> Ran 7801 tests (0.69 tests per mutation)
 
### Initial Analysis
Testing unsurprisingly is... not good. Lots of mutations simply aren't killed because many pieces aren't tested. 

I've tried to constrain it to `pcgen.core` for mutations created, because I don't want to wait for it to create mutations
on everything. All tests run though because their tests kind of go cross class at times. 

On looking at a few files, the mutation it's getting burned on the most is returns, being set to null or another awkward value. 
Alternatively, the occasional edge case with checking if classes/files are null or just ensuring the dependence you've 
received are good to go seems to fail frequently as well. 

This is only a fraction though, and most of the business logic or changing more common objects(lists and the like) to null
seem to be properly killed. 

### Group Contributions

#### Ethan
I wrote a test in `NameGenDataLoaderTest.java` because there were some easy gimmies that should have been tested but simply never were. 
Specifically, checking this if statement `if (dt == null || !"GENERATOR".equals(dt.getName()))` and getting the proper
`return RawFile.EMPTY;` response was never validated. Mutating down to actually checking them never changed anything. As such, 
I wrote a rather straightforward test that makes a few files, one with no doctype(dt == null) and one where the doctype is 
not generator()!"GENERATOR".equals(dt.getName())). If the load still shows an empty list, we're properly handling it. If a 
mutation tries to mess around, having it covered like this should properly kill the mutant. 

If you need it, the if statement in question is at line 154 in `NameGenDataLoaderTest.java`.

Before I wrote the test, `NameGenDataLoaderTest.java` has 90%(179/198) line coverage, 60%(40/67) mutation coverage, and 65%
(40/62) test strength. 

Afterward, it now has 91%(180/198) line coverage, 63%(42/67) mutation coverage, and 67%(42/63) test strength. Successful test. 

#### Collin

#### Coray

### Post-new-test mutation score report
`insert post score report here from the console output after the last person finishes it`