package com.utils;

import java.io.IOException;
import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;

import com.Main;

public class CmdUtil {

    //Gestione terminale
    public static void apriNuovoTerminale(String parametro) throws IOException, URISyntaxException {
       
        // Vai nella root del progetto
        File rootDir = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getParentFile();
        String path = rootDir.getAbsolutePath();

        // Percorso corretto al target/classes
        String targetClassesPath = path + File.separator + "target" + File.separator + "classes";

        // Carica classpath.txt generato da Maven
        File classpathFile = new File(path, "classpath.txt");
        String dependenciesClasspath = "";
        if (classpathFile.exists()) {
            dependenciesClasspath = Files.readString(classpathFile.toPath(), StandardCharsets.UTF_8).trim();
        }

        // Unione classpath
        String fullClasspath = dependenciesClasspath.isEmpty()
                ? targetClassesPath
                : dependenciesClasspath + ";" + targetClassesPath;

        String comando = String.format("cd /d \"%s\" && java -cp \"%s\" com.Main %s", path, fullClasspath, parametro);

        ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "start", "cmd", "/k", comando);
        pb.directory(rootDir);
        pb.start();
    }
}
