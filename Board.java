public class Board {

    public static final int SIZE = 8;   // 8x8
    private Tile[][] tiles = new Tile[SIZE][SIZE];

    public Board() {
        int num = 1;

        // pengisian tile seperti ular tangga (zigzag)
        for (int row = SIZE - 1; row >= 0; row--) {
            if ((SIZE - row) % 2 == 1) { // kiri -> kanan
                for (int col = 0; col < SIZE; col++) {
                    tiles[row][col] = new Tile(num++);
                }
            } else { // kanan -> kiri
                for (int col = SIZE - 1; col >= 0; col--) {
                    tiles[row][col] = new Tile(num++);
                }
            }
        }
    }

    public Tile[][] getTiles() {
        return tiles;
    }
}
