package pcgen.persistence;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import pcgen.core.Globals;
import pcgen.persistence.lst.CampaignLoader;
import pcgen.system.ConfigurationSettings;
import pcgen.system.PCGenSettings;

import java.io.File;
import java.net.URI;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CampaignFileLoaderTest {

    private final LinkedList<URI> campaignFiles = new LinkedList<>();

    private final Answer<Boolean> addCampaign = invocation
            -> campaignFiles.add(((File) invocation.getArgument(0)).toURI());

    private MockedConstruction<CampaignLoader> campaignLoaderMockedConstruction;
    @Mock
    private CampaignLoader campaignLoader;

    private MockedConstruction<RecursiveFileFinder> recursiveFileFinderMockedConstruction;
    @Mock
    private RecursiveFileFinder recursiveFileFinder;

    private MockedStatic<Globals> globals;
    private MockedStatic<ConfigurationSettings> configurationSettings;
    private MockedStatic<PCGenSettings> pcGenSettings;

    private CampaignFileLoader campaignFileLoader;

    @BeforeEach
    public void setup() throws Exception {
//        whenNew(CampaignLoader.class).withNoArguments().thenReturn(campaignLoader);
//        whenNew(RecursiveFileFinder.class).withNoArguments().thenReturn(recursiveFileFinder);
//        whenNew(LinkedList.class).withNoArguments().thenReturn(campaignFiles);

        campaignLoaderMockedConstruction = Mockito.mockConstruction(CampaignLoader.class);
        recursiveFileFinderMockedConstruction = Mockito.mockConstruction(RecursiveFileFinder.class);

        campaignFileLoader = new CampaignFileLoader();

        globals = Mockito.mockStatic(Globals.class);
        configurationSettings = Mockito.mockStatic(ConfigurationSettings.class);
        pcGenSettings = Mockito.mockStatic(PCGenSettings.class);
    }

    @AfterEach
    public void teardown() {
        globals.close();
        configurationSettings.close();
        pcGenSettings.close();

        campaignLoaderMockedConstruction.close();
        recursiveFileFinderMockedConstruction.close();
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

        campaignLoader = campaignLoaderMockedConstruction.constructed().getFirst();
        recursiveFileFinder = recursiveFileFinderMockedConstruction.constructed().getFirst();

        doAnswer(addCampaign).when(recursiveFileFinder).findFiles(any(File.class), anyList());

        campaignFileLoader.run();

        assertEquals(3, campaignFileLoader.getMaximum());
    }

}
