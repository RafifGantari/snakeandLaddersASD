import java.io.*;
import java.util.*;

public class WinDatabase {
    private static final String FILE_NAME = "ranking.txt";

    // Method untuk menambah kemenangan ke database
    public static void addWin(String playerName) {
        Map<String, Integer> stats = loadStats();
        // Cek nama, tambah 1 kemenangannya
        stats.put(playerName, stats.getOrDefault(playerName, 0) + 1);
        saveStats(stats);
    }

    // Method untuk mengambil data ranking (sudah diurutkan)
    public static List<Map.Entry<String, Integer>> getLeaderboard() {
        Map<String, Integer> stats = loadStats();
        List<Map.Entry<String, Integer>> list = new ArrayList<>(stats.entrySet());
        
        // Sorting: Dari nilai terbesar ke terkecil (Descending)
        list.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        return list;
    }

    // Load data dari file txt
    private static Map<String, Integer> loadStats() {
        Map<String, Integer> stats = new HashMap<>();
        File file = new File(FILE_NAME);
        if (!file.exists()) return stats;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Format di file -> Nama:Jumlah
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    try {
                        stats.put(parts[0], Integer.parseInt(parts[1]));
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stats;
    }

    // Save data ke file txt
    private static void saveStats(Map<String, Integer> stats) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Map.Entry<String, Integer> entry : stats.entrySet()) {
                writer.println(entry.getKey() + ":" + entry.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}