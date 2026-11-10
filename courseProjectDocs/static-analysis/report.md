## Tool Used
    SonarQube/SonarCloud
## Key Findings

- Security
    SonarQube reported 21 security issues with a security rating of E
    76% of them are high severity, 19% are low, 5% blocker
    While this is a disappointing showing, PCGen is run locally on a device most of the time and very rarely uses networked features

- Reliability
    SonarQube reports 555 Reliability issues with a reliability rating of E
    29% of them are blockers, 5% are high, 9% are medium, with 57% low, 0.2% info
    the majority being low is deceptive, because 29% of 555 is very high for blocker and likely presents defects that adversely affect software behavior

- Maintainability
    SonarQube reports a staggering 8,722 maintainability issues. Despite this, the maintainability rating is an A
    this doesn't make a lot of sense, but would tend to suggest that the majority of these issues are of low severity. Who knows, with the incredible size of this repository, it is entirely possible that 8,722 is a relatively small proportion. 

- duplication
    with only 6.6% duplication, it hardly presents a problem, but some refactoring might marginally improve maintainability


## Fix summary
    Fixing this in any measurable way is quite a tall order that would probably amount to a full-time job. Instead, we elected to find several small disparate issues to mitigate. generally localized to code/src/java/pcgen/gui2

## Group Contributions

Ethan: Set up sonarCloud. Some strange licensing and access issues prevented us from using SonarQube in the way we intended, however, Ethan got the setup working and was able to get us the results and find several easily fixable issues.

Coray: 

Collin: fixed a small issue in the facade/CharacterAbilities.java which handled an if statement present in decimal displays for character abilities that relied on a .equals() instead of a compareTo() which could be causing unintended behavior. Wrote the report