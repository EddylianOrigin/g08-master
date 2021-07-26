package ServerRMI;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
/**
 * Tests fure RMIServer.
 */
public class RMIServerTest {

    @BeforeAll
    public static void CreateRegistry() {
        //teste ob das registry aus vorherigen Testfällen existiert (test auf null funktioniert nicht,
        //list() wirft aber nullpointerexception falls das registry nicht funktioniert)
        try {
            //System.out.println(LocateRegistry.getRegistry(1099));
            LocateRegistry.getRegistry(1099).list();
        } catch (Exception e) {
            try {
                LocateRegistry.createRegistry(1099);
            } catch (RemoteException remoteException) {
                remoteException.printStackTrace();
            }
        }
    }

    @Test
    void createSpielraumServiceTest() {
        try {
            //Registry registry  = LocateRegistry.createRegistry(1099);
            Registry registry = LocateRegistry.getRegistry(1099);
            RMIServer.createSpielraumService(new SpielraumImpl("test", 3, new Nutzer("host", InetAddress.getByName("127.0.0.1"), 1099), new LobbyImpl()));

            //wenn noch kein Eintrag im Registry ist, wird eine NotBoundException ausgegeben
            assertDoesNotThrow(() -> registry.lookup("test"));
        } catch (RemoteException | UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Test
    void unbindSpielraumServiceTest() {
        try {
            //Registry registry  = LocateRegistry.createRegistry(1099);
            Registry registry = LocateRegistry.getRegistry(1099);
            RMIServer.createSpielraumService(new SpielraumImpl("test2", 3, new Nutzer("host", InetAddress.getByName("127.0.0.1"), 1099), new LobbyImpl()));

            //wenn noch kein Eintrag im Registry ist, wird eine NotBoundException ausgegeben
            assertDoesNotThrow(() -> registry.lookup("test2"));

            RMIServer.unbindSpielraumService("test2");
            assertThrows(NotBoundException.class, () -> registry.lookup("test2"));
        } catch (RemoteException | UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
