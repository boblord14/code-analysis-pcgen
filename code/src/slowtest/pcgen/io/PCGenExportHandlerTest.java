package pcgen.io;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedWriter;
import java.io.File;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.core.Equipment;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.LevelInfo;
import pcgen.core.PCClass;
import pcgen.core.PCStat;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.SettingsHandler;
import pcgen.core.Skill;
import pcgen.core.analysis.SkillRankControl;
import pcgen.core.character.EquipSet;
import pcgen.rules.context.LoadContext;
import pcgen.util.TestHelper;

class PCGenExportHandlerTest extends AbstractCharacterTestCase
{
    @TempDir
    Path tempDir;

    private Equipment armor;

    @BeforeEach
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

        LevelInfo levelInfo = new LevelInfo();
        levelInfo.setLevelString("LEVEL");
        levelInfo.setMaxClassSkillString("LEVEL+3");
        levelInfo.setMaxCrossClassSkillString("(LEVEL+3)/2");
        GameMode gamemode = SettingsHandler.getGameAsProperty().get();
        gamemode.addLevelInfo("Default", levelInfo);

        LoadContext context = Globals.getContext();

        Race testRace = new Race();
        testRace.setName("TestRace");
        context.getReferenceContext().importObject(testRace);

        PCClass myClass = new PCClass();
        myClass.setName("MyClass");
        myClass.put(FormulaKey.START_SKILL_POINTS, FormulaFactory.getFormulaFor(3));
        context.getReferenceContext().importObject(myClass);

        CDOMDirectSingleRef<PCStat> dexRef = CDOMDirectSingleRef.getRef(dex);

        Skill balance = new Skill();
        context.unconditionallyProcess(balance, "CLASSES", "MyClass");
        balance.setName("Balance");
        balance.addToListFor(ListKey.TYPE, Type.getConstant("DEX"));
        balance.put(ObjectKey.KEY_STAT, dexRef);
        context.getReferenceContext().importObject(balance);

        Skill tumble = new Skill();
        context.unconditionallyProcess(tumble, "CLASSES", "MyClass");
        tumble.setName("Tumble");
        tumble.addToListFor(ListKey.TYPE, Type.getConstant("DEX"));
        tumble.put(ObjectKey.KEY_STAT, dexRef);
        context.getReferenceContext().importObject(tumble);

        armor = new Equipment();
        armor.setName("TestArmorSuit");
        TestHelper.addType(armor, "armor.suit");

        finishLoad();

        PlayerCharacter character = getCharacter();
        setPCStat(character, dex, 16);
        character.setRace(testRace);
        character.incrementClassLevel(5, myClass, true);
        character.setName("Zed");

        character.setSkillOrder(balance, 1);
        character.setSkillOrder(tumble, 2);
        SkillRankControl.modRanks(4.0, myClass, true, character, balance);
        SkillRankControl.modRanks(7.0, myClass, true, character, tumble);
        character.calcActiveBonuses();
    }

    @Override
    protected void defaultSetupEnd() {}

    private String export(String... templateLines) throws Exception
    {
        Path file = tempDir.resolve("sheet.txt");
        Files.writeString(
                file,
                String.join("\n", templateLines),
                StandardCharsets.UTF_8
        );

        return export(file.toFile());
    }

    private String export(File template) throws Exception
    {
        ExportHandler handler = ExportHandler.createExportHandler(template);
        StringWriter stringWriter = new StringWriter();
        try (BufferedWriter out = new BufferedWriter(stringWriter))
        {
            handler.write(getCharacter(), out);
        }
        return stringWriter.toString().replace("\r\n", "\n");
    }

    @Test
    void testForStopsWhenNoMoreItems() throws Exception
    {
        PlayerCharacter pc = getCharacter();
        pc.addEquipment(armor);
        pc.addEquipSet(new EquipSet("1", "Default", "", armor));

        String result = export(
                "|FOR,%n,0,99,1,1|",
                "|ARMOR.SUIT.ALL.%n.NAME|",
                "|ENDFOR|",
                "After");

        assertEquals("TestArmorSuit\n\nAfter\n", result);
    }

    @Test
    void testMissingTemplateDoesNotThrowError()
    {
        File missing = tempDir.resolve("does-not-exist.txt").toFile();

        String[] holder = new String[1];
        assertDoesNotThrow(() -> holder[0] = export(missing));

        assertEquals("", holder[0]);
    }
}