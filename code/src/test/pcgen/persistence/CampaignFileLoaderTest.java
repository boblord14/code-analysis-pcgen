package pcgen.persistence;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import pcgen.core.Campaign;
import pcgen.core.Globals;
import pcgen.persistence.lst.CampaignLoader;
import pcgen.system.ConfigurationSettings;
import pcgen.system.PCGenSettings;
import pcgen.util.Logging;

import java.io.File;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CampaignFileLoaderTest {

    private final List<Campaign> globalCampaignsList = new LinkedList<>();

    private final Answer<Boolean> addCampaign = invocation
            -> addToLists(
                invocation.getArgument(0),
                invocation.getArgument(1),
                globalCampaignsList
            );

    private boolean addToLists(File file, List<URI> innerList, List<Campaign> globalList) {
        innerList.add(file.toURI());
        globalList.add(new Campaign()); // this would usually be loaded from the file

        return true;
    }

    @Mock
    private CampaignLoader campaignLoader;
    @Mock
    private RecursiveFileFinder recursiveFileFinder;

    private MockedStatic<Globals> globals;
    private MockedStatic<ConfigurationSettings> configurationSettings;
    private MockedStatic<PCGenSettings> pcGenSettings;
    private MockedStatic<Logging> logging;

    private CampaignFileLoader campaignFileLoader;

    @BeforeEach
    public void setup() throws Exception {
        campaignFileLoader = new CampaignFileLoader(
                recursiveFileFinder,
                campaignLoader
        );

        CampaignFileLoader.setCampaignLoaderStatic(campaignLoader);

        globals = Mockito.mockStatic(Globals.class);
        configurationSettings = Mockito.mockStatic(ConfigurationSettings.class);
        pcGenSettings = Mockito.mockStatic(PCGenSettings.class);
        logging = Mockito.mockStatic(Logging.class);
    }

    @AfterEach
    public void teardown() {
        globals.close();
        configurationSettings.close();
        pcGenSettings.close();
        logging.close();

        globalCampaignsList.clear();
    }

    @Test
    public void testRun_Normal() {
        configurationSettings.when(ConfigurationSettings::getPccFilesDir)
                .thenReturn("campaign1");
        pcGenSettings.when(PCGenSettings::getVendorDataDir)
                .thenReturn("campaign2");
        pcGenSettings.when(PCGenSettings::getHomebrewDataDir)
                .thenReturn("campaign3");

        globals.when(() -> Globals.getCampaignByURI(any(URI.class), anyBoolean()))
            .thenReturn(null);
        globals.when(Globals::getCampaignList).thenReturn(globalCampaignsList);

        doAnswer(addCampaign).when(recursiveFileFinder).findFiles(any(File.class), anyList());

        campaignFileLoader.run();

        assertEquals(3, campaignFileLoader.getMaximum());
        verify(campaignLoader, times(3)).initRecursivePccFiles(any());
    }

    @Test
    public void testRun_NothingFound() {
        configurationSettings.when(ConfigurationSettings::getPccFilesDir)
                .thenReturn("campaign1");
        pcGenSettings.when(PCGenSettings::getVendorDataDir)
                .thenReturn("campaign2");
        pcGenSettings.when(PCGenSettings::getHomebrewDataDir)
                .thenReturn("campaign3");

        globals.when(() -> Globals.getCampaignByURI(any(URI.class), anyBoolean()))
                .thenReturn(null);
        globals.when(Globals::getCampaignList).thenReturn(globalCampaignsList);

        doNothing().when(recursiveFileFinder).findFiles(any(File.class), anyList());

        campaignFileLoader.run();

        assertEquals(0, campaignFileLoader.getMaximum());
        verify(campaignLoader, never()).initRecursivePccFiles(any());
    }

    @Test
    public void testRun_AlternateSourceFolder() {
        campaignFileLoader.setAlternateSourceFolder(new File("campaign4"));

        globals.when(() -> Globals.getCampaignByURI(any(URI.class), anyBoolean()))
                .thenReturn(null);
        globals.when(Globals::getCampaignList).thenReturn(globalCampaignsList);

        doAnswer(addCampaign).when(recursiveFileFinder).findFiles(any(File.class), anyList());

        campaignFileLoader.run();

        assertEquals(1, campaignFileLoader.getMaximum());
        verify(campaignLoader, times(1)).initRecursivePccFiles(any());
    }

    @Test
    public void testRun_PersistenceException() throws PersistenceLayerException {
        configurationSettings.when(ConfigurationSettings::getPccFilesDir)
                .thenReturn("campaign1");
        pcGenSettings.when(PCGenSettings::getVendorDataDir)
                .thenReturn("campaign2");
        pcGenSettings.when(PCGenSettings::getHomebrewDataDir)
                .thenReturn("campaign3");

        globals.when(() -> Globals.getCampaignByURI(any(URI.class), anyBoolean()))
                .thenReturn(null);
        globals.when(Globals::getCampaignList).thenReturn(globalCampaignsList);

        logging.when(() -> Logging.errorPrint(anyString(), any(PersistenceLayerException.class)))
                .thenAnswer(_ -> null);

        doAnswer(addCampaign).when(recursiveFileFinder).findFiles(any(File.class), anyList());

        doThrow(new PersistenceLayerException("")).when(campaignLoader).loadCampaignLstFile(any(URI.class));

        campaignFileLoader.run();

        logging.verify(
            () -> Logging.errorPrint(anyString(), any(PersistenceLayerException.class)),
            times(3)
        );
    }

}
