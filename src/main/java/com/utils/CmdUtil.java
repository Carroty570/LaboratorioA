package com.utils;

import java.io.IOException;
import java.io.File;
import java.net.URISyntaxException;

import com.Main;

public class CmdUtil {

    // Gestione Terminali 
    public static void apriNuovoTerminale(String parametro) throws IOException, URISyntaxException {

        // Ottieni il percorso della cartella o jar da cui è stata caricata la classe com.Main
        File codeSource = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());

        // Se è un jar, prendi la cartella contenente il jar
        File workingDir = codeSource.isFile() ? codeSource.getParentFile() : codeSource;

        String path = workingDir.getAbsolutePath();

        // Comando: start nuovo cmd, cambia directory, esegui java con classpath locale
        String comando = String.format("cd /d \"%s\" && java -cp . com.Main %s", path, parametro);

        // Usa ProcessBuilder con: cmd /c start cmd /k "<comando>"
        ProcessBuilder pb = new ProcessBuilder(
            "cmd", "/c", "start", "cmd", "/k", comando
        );

        // Imposta la directory di lavoro (facoltativo, per sicurezza)
        pb.directory(workingDir);

        // Avvia il processo
        pb.start();
    }
}
