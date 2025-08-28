package theknife.util;

import java.util.Scanner;

public class ConsoleUtils {
    private static final Scanner SC = new Scanner(System.in);

    public static String readLine(String prompt) {
        System.out.print(prompt);
        return SC.nextLine().trim();
    }

    public static int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                int v = Integer.parseInt(SC.nextLine().trim());
                if (v < min || v > max) throw new NumberFormatException();
                return v;
            } catch (NumberFormatException e) {
                System.out.println("Valore non valido, riprova.");
            }
        }
    }

    public static double readDouble(String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            try {
                double v = Double.parseDouble(SC.nextLine().trim().replace(',', '.'));
                if (v < min || v > max) throw new NumberFormatException();
                return v;
            } catch (NumberFormatException e) {
                System.out.println("Valore non valido, riprova.");
            }
        }
    }

    public static boolean readYesNo(String prompt) {
        while (true) {
            System.out.print(prompt + " (s/n): ");
            String v = SC.nextLine().trim().toLowerCase();
            if (v.equals("s") || v.equals("si") || v.equals("sì")) return true;
            if (v.equals("n") || v.equals("no")) return false;
            System.out.println("Risposta non valida.");
        }
    }
}
