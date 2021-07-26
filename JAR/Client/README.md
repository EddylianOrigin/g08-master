## LAMA Client

Vor dem Start des Clients client.config überprüfen.

Der Client kann mit dem Befehl `java --module-path .\javafx\lib --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.web  -jar .\LamaClient.jar` gestartet werden. Unter Windows kann die LamaClient.bat verwendet werden. 

### client.config
**lama.server.host:** Hostname oder IP des Lama Servers

**lama.server.port:** RMI Port des Lama Servers

**lama.client.port:** RMI Port des Lama Clients
