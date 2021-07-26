package ServerRMI;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AccoutImpl Klasse für Tests. Nutzt Map anstall MySQL Datenbank
 */
class AccountImplwoMysql extends AccountImpl {

    public Map<String, String> data = new HashMap<>();


    /**
     * Kreirt das AccountImpl Objekt und weist diesem eine Lobby zu.
     *
     * @param lobby Objekt in dem die Lobby und deren Nutzer verwaltet werden.
     */
    public AccountImplwoMysql(LobbyImpl lobby) {
        super(lobby);
    }

    /**
     * Testet ob in der Datenbank ein Datensatz mit den angegebenen Argumenten Nutzername und Passwort existiert
     *
     * @param nutzername Zu überprüfender Nutzername
     * @param pw         Zu überprüfendes Passwort
     * @return true, wenn Datensatz in Datenbank, sonst false
     */
    @Override
    boolean benutzernamePasswortKorrekt(String nutzername, String pw) {

        if (this.data.containsKey(nutzername)) {
            return this.data.get(nutzername).equals(pw);
        }
        return false;
    }

    /**
     * Testet ob der angegebene Benutzername bereits in der Datenbank gespeichert ist
     *
     * @param nutzername Zu Überprüfender Benutzername
     * @return true, wenn Benutzername in Datenbank, sonst false
     */
    @Override
    boolean benutzernameNichtVergeben(String nutzername) {
        return !this.data.containsKey(nutzername);
    }

    /**
     * Fügt einen Benutzer mit Nutzername und Passwort zur Datenbank hinzu
     *
     * @param nutzername Hinzuzufügender Nutzername
     * @param pw         Hinzuzufügendes Passwort
     */
    @Override
    void benutzerHinzufuegen(String nutzername, String pw) {
        this.data.put(nutzername, pw);
    }

    /**
     * Löscht einen Benutzer anhand des Nutzernames aus der Datenbank
     *
     * @param nutzername Nutzername des zu löschenden Benutzers
     */
    @Override
    void benutzerLoeschen(String nutzername) {
        this.data.remove(nutzername);
    }
}

class AccountImplTest {

    @Test
    void login_PasswortFehlt_MissingInputException() {
        assertThrows(Exceptions.Anmeldung.MissingInputException.class, () -> new AccountImplwoMysql(new LobbyImpl()).login("name", null, 1, InetAddress.getByName("127.0.0.1")));
    }

    @Test
    void login_BenutzernameFehlt_MissingInputException() {
        assertThrows(Exceptions.Anmeldung.MissingInputException.class, () -> new AccountImplwoMysql(new LobbyImpl()).login(null, "passwort", 1, InetAddress.getByName("127.0.0.1")));
    }

    @Test
    void login_BenutzernameFalsch_AnmeldenameException() {
        assertThrows(Exceptions.Anmeldung.AnmeldenameException.class, () -> new AccountImplwoMysql(new LobbyImpl()).login("benutzer", "passwort", 1, InetAddress.getByName("127.0.0.1")));
    }

    @Test
    void login_PasswortFalsch_PasswortException() {
        try {
            String testnutzername = "benutzer";
            LobbyImpl lobby = new LobbyImpl();
            AccountImplwoMysql account = new AccountImplwoMysql(lobby);
            account.register(testnutzername, "passwort", 1, InetAddress.getByName("127.0.0.1"));
            account.logout(testnutzername, 1, InetAddress.getByName("127.0.0.1"));
            assertThrows(Exceptions.Anmeldung.PasswortException.class, () -> account.login(testnutzername, "nichtpasswort", 1, InetAddress.getByName("127.0.0.1")));
            assertTrue(lobby.getNutzerListe().isEmpty());
            assertTrue(lobby.getLobbyNutzerListe().isEmpty());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void login_BereitsAngemeldet_BereitsAngemeldetException() {
        try {
        String testnutzername = "benutzer";
        String testpasswort = "passwort";
        LobbyImpl lobby = new LobbyImpl();
        AccountImplwoMysql account = new AccountImplwoMysql(lobby);
        account.register(testnutzername, testpasswort, 1, InetAddress.getByName("127.0.0.1"));
        account.logout(testnutzername, 1, InetAddress.getByName("127.0.0.1"));
        account.login(testnutzername, testpasswort, 1, InetAddress.getByName("127.0.0.1"));
        assertThrows(Exceptions.Anmeldung.BereitsAngemeldetException.class, () -> account.login(testnutzername, "passwort", 1, InetAddress.getByName("127.0.0.1")));
        assertEquals(Collections.singletonList(testnutzername), lobby.getNutzerListe());
        assertEquals(Collections.singletonList(testnutzername), lobby.getLobbyNutzerListe());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void login_ErfolgreichesLogin() {
        try {
            String testnutzername = "benutzer";
            String testpasswort = "passwort";
            LobbyImpl lobby = new LobbyImpl();
            AccountImplwoMysql account = new AccountImplwoMysql(lobby);
            account.register(testnutzername, testpasswort, 1, InetAddress.getByName("127.0.0.1"));
            account.logout(testnutzername, 1, InetAddress.getByName("127.0.0.1"));
            account.login(testnutzername, testpasswort, 1, InetAddress.getByName("127.0.0.1"));
            assertEquals(Collections.singletonList(testnutzername), lobby.getNutzerListe());
            assertEquals(Collections.singletonList(testnutzername), lobby.getLobbyNutzerListe());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void logout_ErfolgreichesLogout() {
        try {
            String testnutzername = "benutzer";
            String testpasswort = "passwort";
            LobbyImpl lobby = new LobbyImpl();
            AccountImplwoMysql account = new AccountImplwoMysql(lobby);
            account.register(testnutzername, testpasswort, 1, InetAddress.getByName("127.0.0.1"));
            account.logout(testnutzername, 1, InetAddress.getByName("127.0.0.1"));
            assertTrue(lobby.getNutzerListe().isEmpty());
            assertTrue(lobby.getLobbyNutzerListe().isEmpty());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void register_BenutzernameFehlt_MissingInputException() {
        assertThrows(Exceptions.Registrierung.MissingInputException.class, () -> new AccountImplwoMysql(new LobbyImpl()).register("name", null, 1, InetAddress.getByName("127.0.0.1")));
    }

    @Test
    void register_PasswortFehlt_MissingInputException() {
        assertThrows(Exceptions.Registrierung.MissingInputException.class, () -> new AccountImplwoMysql(new LobbyImpl()).register(null, "passwort", 1, InetAddress.getByName("127.0.0.1")));
    }

    @Test
    void register_BenutzernameVergeben_RegistrierungnameException() {
        String testnutzername = "benutzer";
        String testpasswort = "passwort";
        LobbyImpl lobby = new LobbyImpl();
        AccountImplwoMysql account = new AccountImplwoMysql(lobby);
        assertAll(() -> account.register(testnutzername, testpasswort, 1, InetAddress.getByName("127.0.0.1")));
        assertThrows(Exceptions.Registrierung.RegistrierungnameException.class, () -> account.register(testnutzername, "anderespasswort", 1, InetAddress.getByName("127.0.0.1")));
    }

    @Test
    void register_ErfolgreicheRegistrierung() {
        assertAll(() -> {
            String testnutzername = "benutzer";
            String testpasswort = "passwort";
            LobbyImpl lobby = new LobbyImpl();
            AccountImplwoMysql account = new AccountImplwoMysql(lobby);
            account.register(testnutzername, testpasswort, 1, InetAddress.getByName("127.0.0.1"));
            assertEquals(Collections.singletonList(testnutzername), lobby.getNutzerListe());
            assertEquals(Collections.singletonList(testnutzername), lobby.getLobbyNutzerListe());
        });
    }

    @Test
    void delete_BenutzernameFehlt_MissingInputException() {
        assertThrows(Exceptions.SpielerLoeschen.MissingInputException.class, () -> new AccountImplwoMysql(new LobbyImpl()).delete("name", null, "name"));
    }

    @Test
    void delete_PasswortFehlt_MissingInputException() {
        assertThrows(Exceptions.SpielerLoeschen.MissingInputException.class, () -> new AccountImplwoMysql(new LobbyImpl()).delete(null, "passwort", "name"));
    }


    @Test
    void delete_BenutzernameFalsch_NameException() {
        assertThrows(Exceptions.SpielerLoeschen.NameException.class, () -> new AccountImplwoMysql(new LobbyImpl()).delete("benutzer", "passwort", "benutzer"));
    }


    @Test
    void delete_KontoNichtAngemeldetException() {
        try {

            String testnutzername = "benutzer";
            String testpasswort = "passwort";
            LobbyImpl lobby = new LobbyImpl();
            AccountImplwoMysql account = new AccountImplwoMysql(lobby);
            account.register(testnutzername, testpasswort, 1, InetAddress.getByName("127.0.0.1"));
            assertThrows(Exceptions.SpielerLoeschen.KontoNichtAngemeldetException.class, () -> account.delete(testnutzername, testpasswort, "name2"));
            account.delete(testnutzername, testpasswort, testnutzername);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }


    @Test
    void delete_PasswortFalsch_PasswortException() {
        String testnutzername = "benutzer";
        LobbyImpl lobby = new LobbyImpl();
        AccountImplwoMysql account = new AccountImplwoMysql(lobby);
        assertAll(() -> account.register(testnutzername, "passwort", 1, InetAddress.getByName("127.0.0.1")));
        assertThrows(Exceptions.SpielerLoeschen.PasswortException.class, () -> account.delete(testnutzername, "nichtpasswort", testnutzername));
    }


    @Test
    void delete_ErfolgreichesLoeschen() {
        try {
            String testnutzername = "benutzer";
            String testpasswort = "passwort";
            LobbyImpl lobby = new LobbyImpl();
            AccountImplwoMysql account = new AccountImplwoMysql(lobby);
            account.register(testnutzername, testpasswort, 1, InetAddress.getByName("127.0.0.1"));
            account.delete(testnutzername, testpasswort, testnutzername);
            assertTrue(lobby.getNutzerListe().isEmpty());
            assertTrue(lobby.getLobbyNutzerListe().isEmpty());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}