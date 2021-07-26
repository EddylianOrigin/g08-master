## LAMA Server

Vor dem Start des Clients client.config und Datenbankserver überprüfen.

Der Server kann mit dem Befehl `java -jar .\LamaServer.jar` gestartet werden. Unter Windows kann die LamaServer.bat verwendet werden. 

### Einrichtung der Datenbank
Benötigt wird ein MySQL Server mit eine Datenbank für Lama und einem Benutzer, welcher vollen Zugriff auf die Datenbank hat.
#### Datenbank
Die genannte Datenbank besteht aus nur einer Tabelle user. Diese Tabelle besteht aus den Spalten id, benutzername, passwort und punktestand und speichert die registrierten LAMA Benutzer.
Durch Import der angefügt lama.sql Datei kann eine passende Datenbankstruktur auf den Server erstellt werden.
### server.config
**lama.mysql.host:** Hostname oder IP des Datenbankserver

**lama.mysql.port:** Port des Datenbankserver

**lama.mysql.database:** Name der Lama Datenbank

**lama.mysql.user:** Name des Nutzers, welcher Zugriff auf die Datenbank hat

**lama.mysql.pass:** Passwort des Nutzers, welcher Zugriff auf die Datenbank hat

**lama.server.port:** Lama Server RMI Port (default: 1099)

<br>

**WICHTIG:** Da die Passwörter der Lama-Benutzer unverschlüsselt in der Datenbank abgespeichert werden, muss die Datenbank dementsprechend geschützt sein.
