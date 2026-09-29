package pcgen.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pcgen.cdom.base.Category;
import pcgen.core.prereq.Prerequisite;
import pcgen.rules.context.LoadContext;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class AbilityTest {
    private Ability ability;

    @Mock
    private LoadContext contextMock;

    @BeforeEach
    public void setup() {
        ability = new Ability();
        ability.setName("BasicAbility");
        ability.setDisplayName("Basic Ability");
        ability.setKeyName("basic-ability");

        Category<Ability> cat = new AbilityCategory();
        cat.setName("Basic Abilities");
        ability.setCDOMCategory(cat);

        Prerequisite prerequisite = new Prerequisite();
        prerequisite.setCategoryName(cat.getName());
        prerequisite.setKey("prereq-basic");
        ability.addPrerequisite(prerequisite);
    }

    @Test
    public void testGetCategory() {
        String category = ability.getCategory();

        assertEquals("Basic Abilities", category);
    }

    @Test
    public void testGetCategoryNull() {
        ability.setCDOMCategory(null);

        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            ability.getCategory();
        });

        assertTrue(exception.getMessage().contains(
                "the return value of \"pcgen.core.Ability.get(pcgen.cdom.enumeration.ObjectKey)\" is null"
        ));
    }

    @Test
    public void testGetPCCText() {
        ArrayList<String> fakeContextStrings = new ArrayList<>();
        fakeContextStrings.add("ctx1");
        fakeContextStrings.add("ctx2");
        when(contextMock.unparse(Mockito.any())).thenReturn(fakeContextStrings);

        try (MockedStatic<Globals> globals = Mockito.mockStatic(Globals.class)) {
            globals.when(Globals::getContext).thenReturn(contextMock);

            String pccText = ability.getPCCText();

            assertEquals(
                    "Basic Ability\tCATEGORY:Basic Abilities\tctx1\tctx2\tPRESKILLTOT:=1",
                    pccText
            );
        }
    }

    @Test
    public void testGetPCCText_emptyContext() {
        ArrayList<String> fakeContextStrings = new ArrayList<>();
        when(contextMock.unparse(Mockito.any())).thenReturn(fakeContextStrings);

        try (MockedStatic<Globals> globals = Mockito.mockStatic(Globals.class)) {
            globals.when(Globals::getContext).thenReturn(contextMock);

            String pccText = ability.getPCCText();

            assertEquals(
                    "Basic Ability\tCATEGORY:Basic Abilities\tPRESKILLTOT:=1",
                    pccText
            );
        }
    }

    @Test
    public void testGetPCCText_emptyPrerequisites() {
        ability.clearPrerequisiteList();

        ArrayList<String> fakeContextStrings = new ArrayList<>();
        fakeContextStrings.add("ctx1");
        fakeContextStrings.add("ctx2");
        when(contextMock.unparse(Mockito.any())).thenReturn(fakeContextStrings);

        try (MockedStatic<Globals> globals = Mockito.mockStatic(Globals.class)) {
            globals.when(Globals::getContext).thenReturn(contextMock);

            String pccText = ability.getPCCText();

            assertEquals(
                    "Basic Ability\tCATEGORY:Basic Abilities\tctx1\tctx2\t",
                    pccText
            );
        }
    }
}
