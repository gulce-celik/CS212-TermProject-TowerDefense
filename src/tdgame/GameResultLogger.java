
package tdgame;

import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GameResultLogger {

    private static final String FILE_NAME = "game_results.csv";
    
     // username,level,score,money,status,time

    public static void logGameResult(User user,
                                     int level,
                                     int score,
                                     int money,
                                     boolean gameOver) {

        String username = (user != null) ? user.getName() : "Guest";

        LocalDateTime now = LocalDateTime.now();
        String timeStr = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String status = gameOver ? "GAME_OVER" : "EXIT";

        // CSV formatı: username,level,score,money,status,datetime
        String line = String.format("%s,%d,%d,%d,%s,%s",
                username, level, score, money, status, timeStr);

        File file = new File(FILE_NAME);
        boolean needHeader = !file.exists() || file.length() == 0;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            if (needHeader) {
                writer.write("username,level,score,money,status,datetime");
                writer.newLine();
            }
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //CSV'den, belirli kullanıcı + level için en yüksek skor
    //Kullanıcı null ise veya hiç kayıt yoksa 0
    public static int getBestScoreForUserOnLevel(User user, int level) {
        if (user == null) {
            return 0;
        }

        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return 0;
        }

        int best = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                // ilk satır header olabilir, onu atla
                if (firstLine) {
                    firstLine = false;
                    if (line.toLowerCase().startsWith("username")) {
                        continue;
                    }
                }

                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(",");
                // username,level,score,money,status,datetime
                if (parts.length < 3) continue;

                String username = parts[0];

                int lvl;
                int scoreVal;
                try {
                    lvl = Integer.parseInt(parts[1]);
                    scoreVal = Integer.parseInt(parts[2]);
                } catch (NumberFormatException ex) {
                    continue;
                }

                if (username.equals(user.getName()) && lvl == level) {
                    if (scoreVal > best) {
                        best = scoreVal;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return best;
    }
	 // Builds a simple high-score text for the given user using game_results.csv
    public static String buildHighScoreTextForUser(User user) {
    	if (user == null) {
    		return "You must login first to see your high scores.";
	    }
	
	    String username = user.getName();
	    java.io.File file = new java.io.File(FILE_NAME);
	    if (!file.exists()) {
	        return "No game results file found yet.";
	    }
	
	    // assuming levels 1..3 
	    int[] bestScores = new int[4]; // index 1,2,3 used
	    boolean anyRecord = false;
	
	    try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(file))) {
	        String line = br.readLine(); // skip header
	        while ((line = br.readLine()) != null) {
	            String[] parts = line.split(",");
	            if (parts.length < 6) continue;
	
	            String name = parts[0];
	            if (!name.equals(username)) continue;
	
	            int lvl;
	            int sc;
	            try {
	                lvl = Integer.parseInt(parts[1]);
	                sc  = Integer.parseInt(parts[2]);
	            } catch (NumberFormatException ex) {
	                continue;
	            }
	
	            if (lvl >= 1 && lvl < bestScores.length) {
	                if (sc > bestScores[lvl]) {
	                    bestScores[lvl] = sc;
	                    anyRecord = true;
	                }
	            }
	        }
	    } catch (Exception ex) {
	        ex.printStackTrace();
	        return "Error while reading high scores.";
	    }
	
	    if (!anyRecord) {
	        return "No recorded games for user: " + username;
	    }
	
	    StringBuilder sb = new StringBuilder();
	    sb.append("High Scores for ").append(username).append("\n\n");
	    for (int lvl = 1; lvl <= 3; lvl++) { 
	        sb.append("Level ").append(lvl).append(": ")
	          .append(bestScores[lvl]).append("\n");
	    }
	    return sb.toString();
    }
}

