Rationale for SpellInfo Tests:
- 1.) toString was overridded to be a combination of featlists in a special string. This tests that the edge case of an empty featlist does properly output an empty string
- 2.) This tests the toString with a couple feats present, that the formatting is as expected and all feats are included
- 3.) compareTo was overridded to compare a myriad of attributes in the SpellInfo class. This checks that an identical copy is still identical
- 4.) compareTo check, but for the "greater than" case for the compare target
- 5.) compareTo check, but for the "less than" case for the compare target