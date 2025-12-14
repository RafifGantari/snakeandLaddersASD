import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class HistoryManager {
    private static final String FILE_NAME = "game_history.txt";

    // Format Data: Nama,Skor,Tanggal
    public static void saveWinner(String name, int score) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
            writer.write(name + "," + score + "," + date);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String[]> loadHistory() {
        List<String[]> history = new ArrayList<>();
        File file = new File(FILE_NAME);
        if (!file.exists()) return history;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    history.add(parts);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // Balik urutan agar yang terbaru ada di atas
        List<String[]> reversed = new ArrayList<>();
        for (int i = history.size() - 1; i >= 0; i--) {
            reversed.add(history.get(i));
        }
        return reversed;
    }
}