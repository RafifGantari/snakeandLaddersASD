public class Tile {
    private int number;
    private int destination; // Destinasi (Default = number itu sendiri)

    public Tile(int number) {
        this.number = number;
        this.destination = number; // Awalnya tidak kemana-mana
    }

    public int getNumber() { return number; }
    
    public int getDestination() { return destination; }
    public void setDestination(int destination) { this.destination = destination; }
    
    // Cek apakah ini tangga (jika destinasi > nomor sekarang)
    public boolean isLadder() {
        return destination > number;
    }
}