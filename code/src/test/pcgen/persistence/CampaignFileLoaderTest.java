package pcgen.persistence;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import pcgen.core.Globals;
import pcgen.persistence.lst.CampaignLoader;
import pcgen.system.ConfigurationSettings;
import pcgen.system.PCGenSettings;

import java.io.File;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CampaignFileLoaderTest {

    private final Answer<Boolean> addCampaign = invocation
            -> invocation.<List<URI>>getArgument(1)
            .add(invocation.<File>getArgument(0).toURI());

    @Mock
    private CampaignLoader campaignLoader;
    @Mock
    private RecursiveFileFinder recursiveFileFinder;

    private MockedStatic<Globals> globals;
    private MockedStatic<ConfigurationSettings> configurationSettings;
    private MockedStatic<PCGenSettings> pcGenSettings;

    private CampaignFileLoader campaignFileLoader;

    @BeforeEach
    public void setup() throws Exception {
        campaignFileLoader = new CampaignFileLoader(
                recursiveFileFinder,
                campaignLoader
        );

        globals = Mockito.mockStatic(Globals.class);
        configurationSettings = Mockito.mockStatic(ConfigurationSettings.class);
        pcGenSettings = Mockito.mockStatic(PCGenSettings.class);
    }

    @AfterEach
    public void teardown() {
        globals.close();
        configurationSettings.close();
        pcGenSettings.close();
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

        doAnswer(addCampaign).when(recursiveFileFinder).findFiles(any(File.class), anyList());

        campaignFileLoader.run();

        assertEquals(3, campaignFileLoader.getMaximum());
    }

}
