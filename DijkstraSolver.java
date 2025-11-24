import java.util.*;

public class DijkstraSolver {

    // Menyimpan urutan langkah (path) dari start sampai finish
    public static List<Integer> getShortestPath(Board board, int startPos) {
        if (startPos >= 64) return new ArrayList<>();

        // 1. Setup Struktur Data
        int size = 65; // Petak 0-64
        int[] dist = new int[size];     // Jarak minimum (jumlah dadu)
        int[] parent = new int[size];   // Untuk melacak jalur (rekonstruksi path)
        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(parent, -1);

        dist[startPos] = 0;

        // Priority Queue untuk Dijkstra: [NomorPetak, Jarak]
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
        pq.add(new int[]{startPos, 0});

        // 2. Proses Dijkstra
        while (!pq.isEmpty()) {
            int[] current = pq.poll();
            int u = current[0];
            int d = current[1];

            if (d > dist[u]) continue;
            if (u == 64) break; // Sudah sampai finish

            // Coba semua kemungkinan dadu (1 - 6)
            for (int dice = 1; dice <= 6; dice++) {
                int v = u + dice;
                if (v > 64) continue;

                // Cek apakah v adalah kaki tangga (Logic Tangga Sederhana untuk Hint)
                // Kita asumsikan "Best Case Scenario" di mana tangga bisa dipakai
                Tile t = board.getTileByNumber(v);
                if (t != null && t.isLadder()) {
                    v = t.getDestination();
                }

                // Relaxation
                if (dist[u] + 1 < dist[v]) {
                    dist[v] = dist[u] + 1;
                    parent[v] = u;
                    pq.add(new int[]{v, dist[v]});
                }
            }
        }

        // 3. Rekonstruksi Jalur (Backtracking dari 64 ke start)
        List<Integer> path = new ArrayList<>();
        int curr = 64;
        
        // Jika tidak ada jalan ke 64 (misal logika error), return kosong
        if (dist[64] == Integer.MAX_VALUE) return path;

        while (curr != -1) {
            path.add(0, curr); // Masukkan ke depan list
            curr = parent[curr];
        }
        
        // Hapus posisi awal agar path dimulai dari langkah pertama
        if (!path.isEmpty() && path.get(0) == startPos) {
            path.remove(0);
        }

        return path;
    }
}