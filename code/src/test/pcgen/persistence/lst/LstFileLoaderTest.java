package pcgen.persistence.lst;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.ArgumentMatchers.*;


import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import pcgen.cdom.base.Constants;
import pcgen.core.SettingsHandler;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.LstFileLoader;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;
import org.mockito.MockedStatic;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Optional;

public class LstFileLoaderTest {

    @TempDir
    Path temp;

    @Test
    public void testBomFile() throws Exception {
        Path newFile = temp.resolve("bom.lst");
        Files.writeString(newFile, "\uFEFFGenericStringText");

        try(MockedStatic<Logging> logging = mockStatic(Logging.class)){
            LstFileLoader.readFromURI(newFile.toUri());

            logging.verify(() -> Logging.log(eq(Logging.WARNING), contains("UTF-8-BOM")));
        }
    }

    @Test
    public void testInvalidUTF8() throws Exception {
        Path newFile = temp.resolve("bom.lst");
        Files.writeString(newFile, "\u00FF", StandardCharsets.ISO_8859_1);

        try(MockedStatic<Logging> logging = mockStatic(Logging.class)){
            LstFileLoader.readFromURI(newFile.toUri());

            logging.verify(() -> Logging.errorPrint(contains("LST files must be UTF-8"), isA(MalformedInputException.class)));
        }
    }

    @Test
    public void testIOE() throws Exception {
        Path newFile = temp.resolve("bom.lst");

        try(MockedStatic<Logging> logging = mockStatic(Logging.class)){
            LstFileLoader.readFromURI(newFile.toUri());

            logging.verify(() -> Logging.errorPrint(contains("Message:"), isA(NoSuchFileException.class)));
        }
    }

    @Test
    public void rightDestinationTest() throws Exception {
        URI testUri = URI.create("https://example.com");

        HttpClient testClient = mock(HttpClient.class);
        HttpResponse<String> testResponse = mock(HttpResponse.class);

        when(testResponse.body()).thenReturn("Some data");

        when(testClient.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any())).thenReturn(testResponse);

        try(
        MockedStatic<SettingsHandler> testSettings = mockStatic(SettingsHandler.class);
        MockedStatic<HttpClient> testHttp = mockStatic(HttpClient.class);
        ){
            testSettings.when(SettingsHandler::isLoadURLs).thenReturn(true);
            testHttp.when(HttpClient::newHttpClient).thenReturn(testClient);

            Optional<String> result = LstFileLoader.readFromURI(testUri);

            ArgumentCaptor<HttpRequest> captor = ArgumentCaptor.forClass(HttpRequest.class);
            verify(testClient);
            testClient.send(captor.capture(), ArgumentMatchers.<HttpResponse.BodyHandler>any());

            assertEquals(testUri, captor.getValue().uri());
            assertEquals("GET", captor.getValue().method());
            assertEquals(Optional.of("Some data"), result);
        }


    }

    @Test
    public void properBodyReturnTest() throws Exception {
        URI testUri = URI.create("https://example.com");

        HttpClient testClient = mock(HttpClient.class);
        HttpResponse<String> testResponse = mock(HttpResponse.class);

        when(testResponse.body()).thenReturn("Some data");

        when(testClient.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any())).thenReturn(testResponse);

        try(MockedStatic<SettingsHandler> testSettings = mockStatic(SettingsHandler.class);
                    MockedStatic<HttpClient> testHttp = mockStatic(HttpClient.class);
                    ){
            testSettings.when(SettingsHandler::isLoadURLs).thenReturn(true);
            testHttp.when(HttpClient::newHttpClient).thenReturn(testClient);

            assertEquals(Optional.of("Some data"), LstFileLoader.readFromURI(testUri));
        }



    }

    @Test
    public void noLoadTest() throws Exception {
        URI testUri = URI.create("https://example.com");

        HttpClient testClient = mock(HttpClient.class);
        HttpResponse<String> testResponse = mock(HttpResponse.class);

        when(testResponse.body()).thenReturn("Some data");

        when(testClient.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any())).thenReturn(testResponse);

        try(MockedStatic<ShowMessageDelegate> dialog = mockStatic(ShowMessageDelegate.class);

        MockedStatic<SettingsHandler> testSettings = mockStatic(SettingsHandler.class);
        MockedStatic<HttpClient> testHttp = mockStatic(HttpClient.class);
        MockedStatic<LanguageBundle> testLanguageBundle = mockStatic(LanguageBundle.class);
        ){
            testLanguageBundle.when(() -> LanguageBundle.getFormattedString(eq("in_err_remote_lst_warn"), eq(testUri))).thenReturn("error msg string");

            testSettings.when(SettingsHandler::isLoadURLs).thenReturn(false);
            testHttp.when(HttpClient::newHttpClient).thenReturn(testClient);
            assertEquals(Optional.empty(), LstFileLoader.readFromURI(testUri));

            dialog.verify(() -> ShowMessageDelegate.showMessageDialog(eq("error msg string"), eq(Constants.APPLICATION_NAME), eq(MessageType.ERROR)));
        }


    }

    @Test
    public void clientIoeTest() throws Exception {
        URI testUri = URI.create("https://example.com");

        HttpClient testClient = mock(HttpClient.class);
        HttpResponse<String> testResponse = mock(HttpResponse.class);

        when(testResponse.body()).thenReturn("Some data");

        try(MockedStatic<Logging> logging = mockStatic(Logging.class);
            MockedStatic<SettingsHandler> testSettings = mockStatic(SettingsHandler.class);
            MockedStatic<HttpClient> testHttp = mockStatic(HttpClient.class);
            ){

            testSettings.when(SettingsHandler::isLoadURLs).thenReturn(true);
            testHttp.when(HttpClient::newHttpClient).thenReturn(testClient);

            when(testClient.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any())).thenThrow(IOException.class);
            LstFileLoader.readFromURI(testUri);

            logging.verify(() -> Logging.errorPrint(contains("Message:"), isA(IOException.class)));
        }


    }

    @Test
    public void clientInterruptTest() throws Exception {
        URI testUri = URI.create("https://example.com");

        HttpClient testClient = mock(HttpClient.class);
        HttpResponse<String> testResponse = mock(HttpResponse.class);

        when(testResponse.body()).thenReturn("Some data");

        try(MockedStatic<Logging> logging = mockStatic(Logging.class);
            MockedStatic<SettingsHandler> testSettings = mockStatic(SettingsHandler.class);
            MockedStatic<HttpClient> testHttp = mockStatic(HttpClient.class);
        ){

            testSettings.when(SettingsHandler::isLoadURLs).thenReturn(true);
            testHttp.when(HttpClient::newHttpClient).thenReturn(testClient);

            when(testClient.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any())).thenThrow(InterruptedException.class);
            LstFileLoader.readFromURI(testUri);

            logging.verify(() -> Logging.errorPrint(contains("Message:"), isA(InterruptedException.class)));
        }


    }
}
