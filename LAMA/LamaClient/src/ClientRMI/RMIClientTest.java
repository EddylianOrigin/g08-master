package ClientRMI;

import ClientGUI.LobbyGUI;
import ClientGUI.SpielraumGUI;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.testng.annotations.Test;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests fuer RMIClient.
 */
public class RMIClientTest {

    @Test
    void set_getNutzerNameTest() {
        RMIClient.setNutzerName("test");
        assertEquals("test", RMIClient.getNutzerName());
    }

    @Test
    void createLobbyServiceTest() {
        try {
            LocateRegistry.createRegistry(RMIClient.getRegistryPort());
            Registry registry = LocateRegistry.getRegistry(RMIClient.getRegistryPort());
            RMIClient.createLobbyService(new LobbyGUI());
            //wenn noch kein Eintrag im Registry ist, wird eine NotBoundException ausgegeben
            assertDoesNotThrow(() -> registry.lookup("Lobby"));

            //teste ob es auch funktioniert wenn bereits ein Dienst vorhanden ist
            RMIClient.createLobbyService(new LobbyGUI());
            assertDoesNotThrow(() -> registry.lookup("Lobby"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void createSpielraumServiceTest() {
        try {
            LocateRegistry.createRegistry(RMIClient.getRegistryPort());
            Registry registry = LocateRegistry.getRegistry(RMIClient.getRegistryPort());
            RMIClient.createSpielraumService(new SpielraumGUI());
            //wenn noch kein Eintrag im Registry ist, wird eine NotBoundException ausgegeben
            assertDoesNotThrow(() -> registry.lookup("Spielraum"));

            //teste ob es auch funktioniert wenn bereits ein Dienst vorhanden ist
            RMIClient.createSpielraumService(new SpielraumGUI());
            assertDoesNotThrow(() -> registry.lookup("Spielraum"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
