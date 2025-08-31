package com.service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * CsvService: parsing/lettura/scrittura CSV compatibile con campi tra virgolette.
 * - parseLine:   "a","b,c","d""e"  ->  [a, b,c, d"e]
 * - read:        legge tutto il file, con/ senza header
 * - appendRow:   appende una riga garantendo newline finale; crea dir/file; scrive header se nuovo
 * - rewrite:     riscrive file da zero (opzionale header)
 */

public final class CsvService {

    private CsvService() {}

    //Parsing e join

    //Parser CSV stile RFC 4180: gestisce virgole tra virgolette e doppie virgolette "" come escape. 
    public static List<String> parseLine(String line) {

        List<String> out = new ArrayList<>();
        if (line == null) return out;
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {

            char c = line.charAt(i);
            if (inQuotes) {

                if (c == '"') {

                    // doppia-virgolette -> virgolette letterale
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        sb.append('"');
                        i++;
                    } else {
                        inQuotes = false;
                    }
                } else {
                    sb.append(c);
                }
            } else {

                if (c == ',') {
                    out.add(sb.toString());
                    sb.setLength(0);
                } else if (c == '"') {
                    inQuotes = true;
                } else {
                    sb.append(c);
                }
            }
        }

        out.add(sb.toString());
        return out;
    }

    // Escaping per CSV: quota se contiene , " o newline; raddoppia le " interne. 
    public static String escape(String s) {

        if (s == null) s = "";
        boolean needQuotes = s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r");
        if (!needQuotes) return s;
        return "\"" + s.replace("\"", "\"\"") + "\"";
    }

    // Concatena i campi in riga CSV con escaping corretto. 
    public static String joinCsv(List<String> fields) {

        if (fields == null) return "";
        return fields.stream().map(CsvService::escape).collect(Collectors.joining(","));
    }

    //Lettura file

    // Contenitore tabellare: header opzionale + righe. 

    public static final class CsvTable {

        private final List<String> header;          // può essere null se non presente
        private final List<List<String>> rows;      // sempre non null
        private Map<String, Integer> nameToIndex;   // lazy

        public CsvTable(List<String> header, List<List<String>> rows) {

            this.header = (header == null || header.isEmpty()) ? null : new ArrayList<>(header);
            this.rows = (rows == null) ? new ArrayList<>() : new ArrayList<>(rows);
        }

        public List<String> getHeader() { return header; }
        public List<List<String>> getRows() { return rows; }

        // Restituisce l'indice di una colonna dell'header, o -1 se assente/non trovata. 
        public int columnIndex(String name) {

            if (header == null || name == null) return -1;
            if (nameToIndex == null) {
                nameToIndex = new HashMap<>();
                for (int i = 0; i < header.size(); i++) nameToIndex.put(header.get(i), i);
            }
            return nameToIndex.getOrDefault(name, -1);
        }
    }

    /**
     * Legge un CSV in UTF-8.
     * @param path      percorso file
     * @param hasHeader true se la prima riga è un header
     */

    public static CsvTable read(Path path, boolean hasHeader) throws IOException {

        List<List<String>> rows = new ArrayList<>();
        List<String> header = null;

        if (!Files.exists(path)) {
            return new CsvTable(header, rows);
        }

        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {

            String line;
            boolean first = true;
            while ((line = br.readLine()) != null) {
                List<String> cols = parseLine(line);
                // rimuovi BOM dal primo campo della prima riga
                if (first && !cols.isEmpty()) {
                    cols.set(0, stripBom(cols.get(0)));
                }
                if (first && hasHeader) {
                    header = cols;
                } else {
                    rows.add(cols);
                }
                first = false;
            }
        }
        return new CsvTable(header, rows);
    }

    //Scrittura

    /**
     * Appende una riga al CSV (UTF-8). Se il file non esiste:
     * - crea la cartella padre
     * - se headerIfCreate non è vuoto, scrive prima l'header.
     * Garantisce che la nuova riga inizi su una nuova linea.
     */

    public static void appendRow(Path path, List<String> fields, List<String> headerIfCreate) throws IOException {

        Objects.requireNonNull(path, "path");
        Objects.requireNonNull(fields, "fields");
        if (path.getParent() != null) Files.createDirectories(path.getParent());

        File file = path.toFile();
        boolean exists = file.exists();

        if (!exists && headerIfCreate != null && !headerIfCreate.isEmpty()) {

            try (BufferedWriter bw = Files.newBufferedWriter(path, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {

                bw.write(joinCsv(headerIfCreate));
                bw.write(System.lineSeparator());
            }
        }

        // Append con controllo newline finale
        try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {

            raf.seek(file.length());
            if (file.length() > 0) {

                raf.seek(Math.max(0, file.length() - 1));
                int last = raf.read();
                if (last != '\n') raf.write('\n');
            }

            String row = joinCsv(fields) + System.lineSeparator();
            raf.write(row.getBytes(StandardCharsets.UTF_8));
        }
    }

    /**
     * Riscrive completamente il file (UTF-8), con header opzionale.
     * Crea la directory padre se assente.
     */
    public static void rewrite(Path path, List<String> header, List<List<String>> rows) throws IOException {

        Objects.requireNonNull(path, "path");
        if (path.getParent() != null) Files.createDirectories(path.getParent());

        try (BufferedWriter bw = Files.newBufferedWriter(path, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {

            if (header != null && !header.isEmpty()) {

                bw.write(joinCsv(header));
                bw.newLine();
            }
            if (rows != null) {
                for (List<String> r : rows) {

                    bw.write(joinCsv(r));
                    bw.newLine();
                }
            }
        }
    }

    //Utils
    // Rimuove l'eventuale BOM UTF-8 dal primo campo. 
    
    private static String stripBom(String s) {
        if (s == null || s.isEmpty()) return s;
        return (s.charAt(0) == '\uFEFF') ? s.substring(1) : s;
    }
}
