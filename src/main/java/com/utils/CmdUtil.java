package com.utils;

import java.io.IOException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import com.Main;

public class CmdUtil {

    //Gestioni Terminali 
    public static void apriNuovoTerminale(String parametro) throws IOException, URISyntaxException {

        // Ottieni il percorso della cartella o jar da cui è stata caricata la classe com.Main
        File codeSource = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        
        // Se è un jar, prendi la cartella contenente il jar
        File workingDir = codeSource.isFile() ? codeSource.getParentFile() : codeSource;
        
        String path = workingDir.getAbsolutePath();

        // Comando Windows: cd alla directory + esegui java con classpath corrente
        // Nota: uso -cp . per classpath (cartella corrente)
        String comando = String.format(
            "cmd /c start cmd /k \"cd /d \"%s\" && java -cp . com.Main %s\"",
            path,
            parametro
        );

        Runtime.getRuntime().exec(comando);
    }



    
    
}
