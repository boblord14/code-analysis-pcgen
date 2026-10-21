package pcgen.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RuleCheckTest {

    private RuleCheck ruleCheck;

    @BeforeEach
    public void setup() {
        ruleCheck = new RuleCheck();
    }

    @Test
    public void testSetParameter() {
        ruleCheck.setVariable("default");
        ruleCheck.setParameter("parameter");

        String expectedParameter = "parameter";
        String expectedVariable = "default";
        String actualParameter = ruleCheck.getParameter();
        String actualVariable = ruleCheck.getVariable();

        assertEquals(expectedParameter, actualParameter);
        assertEquals(expectedVariable, actualVariable);
    }

    @Test
    public void testSetParameterWithEmptyVariable() {
        ruleCheck.setVariable("");
        ruleCheck.setParameter("parameter");

        String expected = "parameter";
        String actualParameter = ruleCheck.getParameter();
        String actualVariable = ruleCheck.getVariable();

        assertEquals(expected, actualParameter);
        assertEquals(expected, actualVariable);
    }
}
