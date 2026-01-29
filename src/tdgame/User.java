

package tdgame;

public class User {

    private String name;
    private String password;

    // level 1, 2, 3 için ayrı best skor
    private int[] bestScores = new int[3];

    public User(String name, String password, int[] bestScores) {
        this.name = name;
        this.password = password;
        if (bestScores != null && bestScores.length >= 3) {
            this.bestScores[0] = bestScores[0];
            this.bestScores[1] = bestScores[1];
            this.bestScores[2] = bestScores[2];
        }
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }


    public int getBestScore() {
        int max = 0;
        for (int v : bestScores) {
            if (v > max) max = v;
        }
        return max;
    }

    //Belirli level’ın best skoru (1..3) 
    public int getBestScore(int level) {
        if (level < 1 || level > 3) return 0;
        return bestScores[level - 1];
    }

    public void setBestScore(int level, int score) {
        if (level < 1 || level > 3) return;
        bestScores[level - 1] = score;
    }

    // Dosyaya yazarken 
    // Format: name;password;score1;score2;score3
    public String toFileLine() {
        return name + ";" + password + ";"
                + bestScores[0] + ";"
                + bestScores[1] + ";"
                + bestScores[2];
    }
}

