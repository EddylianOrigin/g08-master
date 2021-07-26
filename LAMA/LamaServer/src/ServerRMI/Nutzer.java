package ServerRMI;

import java.net.InetAddress;

/**
 * Verwaltet Informationen der System-Nutzer, die benoetigt werden.
 */
public class Nutzer {
    /**
     * Name des Nutzers.
     */
    public final String name;
    /**
     * IP-Adresse des Nutzers.
     */
    public final InetAddress ip;
    /**
     * Port des Nutzers(RMI-Registry).
     */
    public final int portNr;

    public Nutzer(String name, InetAddress ip, int portNr) {
        this.name = name;
        this.ip = ip;
        this.portNr = portNr;
    }
}
