import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Board {
    public static final int SIZE = 8;
    private Tile[][] tiles = new Tile[SIZE][SIZE];

    public Board() {
        int num = 1;
        
        // 1. Buat Papan Dasar
        for (int row = SIZE - 1; row >= 0; row--) {
            if ((SIZE - row) % 2 == 1) { 
                for (int col = 0; col < SIZE; col++) {
                    tiles[row][col] = new Tile(num++);
                }
            } else { 
                for (int col = SIZE - 1; col >= 0; col--) {
                    tiles[row][col] = new Tile(num++);
                }
            }
        }

        // 2. Set Tangga (Hanya 5 di Bilangan Prima)
        setupLimitedPrimeLadders();
    }

    private void setupLimitedPrimeLadders() {
        Random rand = new Random();
        List<Tile> primeTiles = new ArrayList<>();

        // Kumpulkan semua tile yang nomornya Prima & < 55 (supaya aman melompat)
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Tile t = tiles[row][col];
                if (isPrime(t.getNumber()) && t.getNumber() < 55) {
                    primeTiles.add(t);
                }
            }
        }

        // ACAK urutan prima
        Collections.shuffle(primeTiles);

        // AMBIL 5 SAJA
        int count = 0;
        for (Tile t : primeTiles) {
            if (count >= 5) break; // Stop jika sudah 5

            // Tentukan tujuan naik (Random 5 s.d 20 langkah)
            int jump = rand.nextInt(15) + 5; 
            int target = t.getNumber() + jump;

            if (target > 64) target = 64; 
            
            t.setDestination(target);
            count++;
        }
    }

    private boolean isPrime(int n) {
        if (n <= 1) return false;
        for (int i = 2; i <= Math.sqrt(n); i++) {
            if (n % i == 0) return false;
        }
        return true;
    }

    public Tile[][] getTiles() { return tiles; }
    
    public Tile getTileByNumber(int number) {
        if (number < 1 || number > 64) return null;
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (tiles[r][c].getNumber() == number) {
                    return tiles[r][c];
                }
            }
        }
        return null;
    }
}