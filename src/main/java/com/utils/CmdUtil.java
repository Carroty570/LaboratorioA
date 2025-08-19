package com.utils;

import java.io.IOException;
import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Files;
import org.jline.utils.InfoCmp.Capability;

import org.jline.terminal.Terminal;

import java.nio.charset.StandardCharsets;

import com.Main;

public class CmdUtil {

    //Gestione terminale
    public static void apriNuovoTerminale(String parametro) throws IOException, URISyntaxException, InterruptedException {
   
        // Vai nella root del progetto
        File rootDir = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI())
                .getParentFile()
                .getParentFile();
        String path = rootDir.getAbsolutePath();

        // Percorso target/classes
        String targetClassesPath = path + File.separator + "target" + File.separator + "classes";

        // Genera classpath.txt in automatico con Maven
        File classpathFile = new File(path, "classpath.txt");
        if (!classpathFile.exists()) {
            new ProcessBuilder("cmd", "/c",
                    "cd /d \"" + path + "\" && mvn dependency:build-classpath -Dmdep.outputFile=classpath.txt")
                    .inheritIO()
                    .start()
                    .waitFor();
        }

        // Legge il classpath generato
        String dependenciesClasspath = Files.readString(classpathFile.toPath(), StandardCharsets.UTF_8).trim();

        // Unisce dipendenze + target/classes
        String fullClasspath = dependenciesClasspath.isEmpty()
                ? targetClassesPath
                : dependenciesClasspath + ";" + targetClassesPath;

        // Comando di avvio
        String comando = String.format("cd /d \"%s\" && java -cp \"%s\" com.Main %s",
                path, fullClasspath, parametro);

        new ProcessBuilder("cmd", "/c", "start", "cmd", "/k", comando)
                .directory(rootDir)
                .start();
    }

    public static void clearScreen(Terminal terminal) {
        try {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
                // Riattiva raw mode
                terminal.enterRawMode();
        } catch (Exception e) {
                e.printStackTrace();
        }  
    }
}

