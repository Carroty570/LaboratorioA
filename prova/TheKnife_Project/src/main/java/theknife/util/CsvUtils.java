package theknife.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CsvUtils {
    public static List<String[]> readAll(File file) throws IOException {
        List<String[]> rows = new ArrayList<>();
        if (!file.exists()) return rows;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            boolean headerSkipped = false;
            while ((line = br.readLine()) != null) {
                if (!headerSkipped) { headerSkipped = true; continue; }
                rows.add(parseCsvLine(line));
            }
        }
        return rows;
    }

    public static void writeAll(File file, String header, List<String[]> rows) throws IOException {
        file.getParentFile().mkdirs();
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            bw.write(header);
            bw.newLine();
            for (String[] cols : rows) {
                bw.write(escapeCsv(cols));
                bw.newLine();
            }
        }
    }

    private static String[] parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        result.add(sb.toString());
        return result.toArray(new String[0]);
    }

    private static String escapeCsv(String[] cols) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cols.length; i++) {
            String col = cols[i] == null ? "" : cols[i];
            if (col.contains(",") || col.contains(""") || col.contains("
")) {
                col = """ + col.replace(""", """") + """;
            }
            sb.append(col);
            if (i < cols.length - 1) sb.append(",");
        }
        return sb.toString();
    }
}
