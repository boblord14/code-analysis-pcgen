package pcgen.core.character;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import pcgen.core.Ability;
import pcgen.core.PCClass;
import pcgen.core.spell.Spell;

import java.util.ArrayList;
import java.util.Collection;

public class SpellInfoTest {
    SpellInfo spellInfo;
    CharacterSpell characterSpell;

    @BeforeEach
    public void setup() {
        PCClass owner = new PCClass();
        owner.setName("TestName");
        owner.setKeyName("TestKey");

        Spell spell = new Spell();
        spell.setName("TestSpell");

        characterSpell = new CharacterSpell(owner, spell);

        spellInfo = new SpellInfo(characterSpell, 1, 1, 1, null);
    }

    @Test
    void testEmptyFeatList(){
        // toString is overridden as a printed out feat list, ensuring an empty one is actually empty
        assertEquals("", spellInfo.toString());
    }

    @Test
    void testFeats(){
        // toString is overridden as a printed out feat list, ensuring an empty one is actually empty
        ArrayList<Ability> abilities = new ArrayList<>();

        Ability ability = new Ability();
        ability.setName("TestAbility1");
        abilities.add(ability);

        Ability ability2 = new Ability();
        ability2.setName("TestAbility2");
        abilities.add(ability2);

        spellInfo.addFeatsToList(abilities);

        String expected = " [" + abilities.get(0) + ", " + abilities.get(1) + "] ";
        assertEquals(expected, spellInfo.toString());
    }

    @Test
    void testCompareEqual(){
        SpellInfo Copier = spellInfo;
        assertEquals(0, spellInfo.compareTo(Copier));
    }

    @Test
    void testCompareLess(){
        SpellInfo Copier = new SpellInfo(characterSpell, 1, 1, 2, null);

        assertEquals(-1, spellInfo.compareTo(Copier));
    }

    @Test
    void testCompareMore(){
        SpellInfo Copier = new SpellInfo(characterSpell, 1, 1, 1, null);
        spellInfo.setTimes(2);
        assertEquals(1, spellInfo.compareTo(Copier));
    }


}
