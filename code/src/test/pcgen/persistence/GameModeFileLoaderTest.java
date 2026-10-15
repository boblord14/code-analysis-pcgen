package pcgen.persistence;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import pcgen.cdom.base.Constants; 
import pcgen.cdom.content.TabInfo;
import pcgen.core.GameMode;
import pcgen.core.UnitSet;
import pcgen.system.LanguageBundle;
import pcgen.util.enumeration.Tab;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.io.TempDir;
import pcgen.persistence.lst.LstLineFileLoader;
import pcgen.rules.context.LoadContext;
import pcgen.system.ConfigurationSettings;
import pcgen.util.Logging;

class GameModeFileLoaderTest
{

    @TempDir
    Path temp;

	@Test
    void loadGameModeWhereRequestedFileExists()throws Exception{
        Path gameModes = temp.resolve("gameModes");
        Path gameMode = gameModes.resolve("custom");
        Files.createDirectories(gameMode);
        Path gameModeFile = gameMode.resolve("test.lst");
        Files.createFile(gameModeFile);

        LoadContext context = mock(LoadContext.class);
        LstLineFileLoader loader = mock(LstLineFileLoader.class);

        try (MockedStatic<ConfigurationSettings> settings = mockStatic(ConfigurationSettings.class)){

            settings.when(ConfigurationSettings::getSystemsDir).thenReturn(temp.toString());
            boolean result = invokeLoadGameModeLstFile(context, loader, "Test Game Mode", "custom", "test.lst", true);

            assertTrue(result);
            verify(loader).loadLstFile(context, gameModeFile.toUri(), "Test Game Mode");
        }
    }

    @Test
    void loadGameModeFileWithFallback() throws Exception{
        Path gameModes = temp.resolve("gameModes");
        Path defaultDir = gameModes.resolve("default");
        Files.createDirectories(defaultDir);
        Path defaultFile = defaultDir.resolve("test.lst");
        Files.createFile(defaultFile);

        LoadContext context = mock(LoadContext.class);
        LstLineFileLoader loader = mock(LstLineFileLoader.class);

        try (MockedStatic<ConfigurationSettings> settings = mockStatic(ConfigurationSettings.class)){

            settings.when(ConfigurationSettings::getSystemsDir).thenReturn(temp.toString());
            boolean result = invokeLoadGameModeLstFile(context, loader, "Test Game Mode", "custom", "test.lst", true);

            assertTrue(result);
            verify(loader).loadLstFile(context, defaultFile.toUri(), "Test Game Mode");
        }
    }

    @Test
    void loadGameModeFallsBackWhenFailsToLoad() throws Exception{
        Path gameModes = temp.resolve("gameModes");
        Path gameMode = gameModes.resolve("custom");
        Path defaultDir = gameModes.resolve("default");
        Files.createDirectories(gameMode);
        Files.createDirectories(defaultDir);
        Path gameModeFile = gameMode.resolve("test.lst");
        Path defaultFile = defaultDir.resolve("test.lst");
        Files.createFile(gameModeFile);
        Files.createFile(defaultFile);

        LoadContext context = mock(LoadContext.class);
        LstLineFileLoader loader = mock(LstLineFileLoader.class);

        doThrow(PersistenceLayerException.class).when(loader).loadLstFile(context, gameModeFile.toUri(), "Test Game Mode");

        try (MockedStatic<ConfigurationSettings> settings = mockStatic(ConfigurationSettings.class)){

            settings.when(ConfigurationSettings::getSystemsDir).thenReturn(temp.toString());
            boolean result = invokeLoadGameModeLstFile(context, loader, "Test Game Mode", "custom", "test.lst", true);

            assertTrue(result);

            verify(loader).loadLstFile(context, gameModeFile.toUri(), "Test Game Mode");
            verify(loader).loadLstFile(context, defaultFile.toUri(), "Test Game Mode");
        }
    }

    private boolean invokeLoadGameModeLstFile(LoadContext context, LstLineFileLoader loader, String gameModeName, String gameModeFolderName, String lstFileName, boolean showMissing) throws Exception{
        Method method = GameModeFileLoader.class.getDeclaredMethod("loadGameModeLstFile", LoadContext.class, LstLineFileLoader.class, String.class, String.class, String.class, boolean.class);

        method.setAccessible(true);

        return (boolean) method.invoke(null, context, loader, gameModeName, gameModeFolderName, lstFileName, showMissing);
    }
}