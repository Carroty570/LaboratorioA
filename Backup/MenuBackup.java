
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;


public class MenuBackup {

    //Calcola la lunghezza massima tra il titolo del menu e le opzioni

    public static int getMaxLunghezza(String[] opzioni, String titolo) {
        int maxLunghezza = titolo.length(); // Inizializziamo con la lunghezza del titolo
        for (String opzione : opzioni) {
            if (opzione.length() > maxLunghezza) {
                maxLunghezza = opzione.length();
            }
        }
        return maxLunghezza;
    }

    //Stampa Cornici
    public static void corniceAst(String frase){

        int lunghezza = frase.length();
        String linea = "*".repeat(lunghezza + 2);

        //Stampa 

        System.out.println(linea);
        System.out.println("* " + frase + " *");
        System.out.println(linea);

    }
    public static void corniceLin(String frase){

        int lunghezza = frase.length();
        String linea = "-".repeat(lunghezza + 2);

        //Stampa 

        System.out.println(linea);
        System.out.println("- " + frase + " -");
        System.out.println(linea);

    }

    //Stampa separatori 

    public static void separatoreAst(int lunghezza){

        String linea = "*".repeat(lunghezza + 4);
        System.out.println(linea);
    
    }
    public static void separatoreLin(int lunghezza){
        
        String linea = "-".repeat(lunghezza + 4);
        System.out.print("                           ");
        System.out.println(linea);
    
    }

    //Stampa Menu completo

    public static void menuLinea(String titolo, String[] opzioni) {
        
        int maxLunghezza = getMaxLunghezza(opzioni, titolo);
        int cont = 1;

        //Stampa scritta Ascii

        try (InputStream inputStream = MenuBackup.class.getClassLoader().getResourceAsStream("banner.txt")) {
            if (inputStream == null) {
                System.out.println("File banner non trovato!");
                return;
            }
            String banner = new BufferedReader(new InputStreamReader(inputStream))
                                .lines().collect(Collectors.joining("\n"));
            System.out.println(banner);
        } catch (Exception e) {
            System.out.println("Errore nella lettura del banner:");
            e.printStackTrace();
        }

        // Stampa la cornice superiore
        separatoreLin(maxLunghezza);

        // Stampa il titolo del menu

        System.out.println("");
        System.out.println("                           - " + titolo + " ".repeat(maxLunghezza - titolo.length()) + " -");

        // Stampa la cornice in mezzo
        separatoreLin(maxLunghezza);

        // Stampa le opzioni
        for (String opzione : opzioni) {
            System.out.println("                           \033[31m- " +"\033[0m"+ cont + "."+ opzione + " ".repeat(maxLunghezza - opzione.length() - 2) + " -");
            cont += 1;
        }

        // Stampa la cornice inferiore
        separatoreLin(maxLunghezza);
    }


    public static void menuAsterisco(String titolo, String[] opzioni) {
        // Calcola la lunghezza massima tra il titolo e le opzioni
        int maxLunghezza = getMaxLunghezza(opzioni, titolo);
        int cont = 1;

        //Stampa titolo Ascii

        try {
            Path path = Paths.get("resources/banner.txt");
            String banner = Files.readString(path);
            System.out.println(banner);
        } catch (IOException e) {
            System.out.println("Errore nella lettura del file banner:");
            e.printStackTrace();
        }

        // Stampa la cornice superiore
        separatoreAst(maxLunghezza);

        // Stampa il titolo del menu
        System.out.println("* " + titolo + " ".repeat(maxLunghezza - titolo.length()) + " *");

        // Stampa la cornice in mezzo
        separatoreAst(maxLunghezza);

        // Stampa le opzioni
        for (String opzione : opzioni) {
            System.out.println("\033[31m* " + "\033[0m" + cont + "."+ opzione + " ".repeat(maxLunghezza - opzione.length() - 2) + " *");
            cont += 1;
        }

        // Stampa la cornice inferiore
        separatoreAst(maxLunghezza);
    }
   
}
