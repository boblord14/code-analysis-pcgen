Rationale for SpellInfo Tests:
- 1.) toString was overridded to be a combination of featlists in a special string. This tests that the edge case of an empty featlist does properly output an empty string
- 2.) This tests the toString with a couple feats present, that the formatting is as expected and all feats are included
- 3.) compareTo was overridded to compare a myriad of attributes in the SpellInfo class. This checks that an identical copy is still identical
- 4.) compareTo check, but for the "greater than" case for the compare target
- 5.) compareTo check, but for the "less than" case for the compare target

Rationale for Ability Tests:

- 1.) `testGetCategory`: Needed coverage verifying that `get(ObjectKey.ABILITY_CAT)` would return the correct category object with the set key name.
- 2.) `testGetCategoryNull`: Error case in which an ability has a CDOM category value set to `null`. Currently, behavior is to raise a null pointer exception.
- 3.) `testGetPCCText`: `Ability:getPCCText` is a previously uncovered method breaking down an Ability into a reconstructable string. It depends on a static
  method `Globals::getContext` that needed to be mocked along with its `LoadContext` return value to provide usable string output.
  Mockito was added as a dependency in order to test this method. 
- 4.) `testGetPCCText_emptyContext`: Case for the `Ability::getPCCText` where the mock return for the parsed context is empty.
- 5.) `testGetPCCText_emptyPrerequisites`: Case for the `Ability::getPCCText` after the prerequisites list has been cleared.


New Test Results + Coverage Comparison:
- Number of tests ran: 15
- Number of tests passed 15
- Number of tests failed: 0
- Old coverage: `25% instruction coverage, 22% branch coverage`
- New coverage: `26% instruction coverage, 22% branch coverage`
- There are so many tests and so many uncovered lines/branches,
  honestly I'm shocked we even made a dent.