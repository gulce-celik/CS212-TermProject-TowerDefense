
package tdgame;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserManager {

    private static final String FILE_NAME = "users.txt";

    private static List<User> users = new ArrayList<>();
    private static User currentUser = null;

    public static User getCurrentUser() {
        return currentUser;
    }

    static {
        loadUsers();
    }

    private static void loadUsers() {
        users.clear();

        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length >= 2) {
                    String name = parts[0];
                    String password = parts[1];

                    int[] bestScores = new int[3]; // hepsi 0

                    // Eski format: name;pass;bestScore  (tek skor)
                    if (parts.length == 3) {
                        try {
                            int oldBest = Integer.parseInt(parts[2]);
                            bestScores[0] = oldBest; // level 1'e yaz
                        } catch (NumberFormatException ignored) { }
                    }
                    // Yeni format: name;pass;score1;score2;score3
                    else if (parts.length >= 5) {
                        try {
                            bestScores[0] = Integer.parseInt(parts[2]);
                        } catch (NumberFormatException ignored) { }
                        try {
                            bestScores[1] = Integer.parseInt(parts[3]);
                        } catch (NumberFormatException ignored) { }
                        try {
                            bestScores[2] = Integer.parseInt(parts[4]);
                        } catch (NumberFormatException ignored) { }
                    }

                    users.add(new User(name, password, bestScores));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveUsers() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (User u : users) {
                writer.write(u.toFileLine());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static User findUser(String name) {
        for (User u : users) {
            if (u.getName().equalsIgnoreCase(name)) {
                return u;
            }
        }
        return null;
    }

    // Register
    public static boolean register(String name, String password) {
        if (name == null || name.trim().isEmpty()) return false;
        if (password == null || password.trim().isEmpty()) return false;

        if (findUser(name) != null) {
            return false; // kullanıcı zaten var
        }

        int[] scores = new int[3]; // hepsi 0
        User newUser = new User(name, password, scores);
        users.add(newUser);
        saveUsers();
        return true;
    }

    // Login
    public static User login(String name, String password) {
        User u = findUser(name);
        if (u == null) return null;
        if (!u.getPassword().equals(password)) return null;

        currentUser = u;
        return u;
    }

    public static void logout() {
        currentUser = null;
    }

    // Belirli level için best score güncelle
    public static void updateBestScore(User user, int level, int newScore) {
        if (user == null) return;
        if (level < 1 || level > 3) return;

        int currentBest = user.getBestScore(level);
        if (newScore > currentBest) {
            user.setBestScore(level, newScore);
            saveUsers();
        }
    }
}

