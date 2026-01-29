# 🏰 Tower Defense

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Swing](https://img.shields.io/badge/Swing-GUI-blue?style=for-the-badge)
![Course](https://img.shields.io/badge/CSE%20212-Software%20Development-purple?style=for-the-badge)
![Semester](https://img.shields.io/badge/Semester-Fall%202025-orange?style=for-the-badge)

> **Final Project for CSE 212 - Software Development Methodologies**

---

## 📖 Project Overview

**Tower Defense** is a 2D strategy game developed as a term project. The goal was to build a complete graphical application using **Java** and **Swing**, focusing on applying software engineering principles learned in class.

This project demonstrates core concepts such as **Object-Oriented Design**, **Multithreading** for the game loop, and **File Input/Output** for saving user data.

---

## 📸 Screenshots

<div align="center">
  <img src="screenshots/Game%20Photo_4.png" width="45%" alt="Main Menu">
  <img src="screenshots/Game%20Photo_1.png" width="45%" alt="Map Selection">
  <br>
  <img src="screenshots/Game%20Photo_2.png" width="45%" alt="Gameplay Action">
  <img src="screenshots/Game%20Photo_3.png" width="45%" alt="Gameplay Strategy">
</div>

---

## 🎮 Game Features

### ⚔️ Towers
Strategically place towers to stop the enemy waves.

| Icon | Tower | Cost | Type | Description |
| :---: | :--- | :--- | :--- | :--- |
| ⚡ | **Fast Tower** | `$100` | Rapid Fire | Fires quickly with low damage. Good for early waves. |
| 🛡️ | **Strong Tower** | `$400` | Heavy Hitter | Slow but deals high damage. Effective against armor. |
| 🚀 | **Double Rocket**| `$800` | Twin Missiles | Fires two missiles at once for better area control. |
| 💥 | **Big Rocket** | `$1200`| Area Damage | Creates large explosions to damage groups of enemies. |

### 👾 Enemies
Different enemies require different strategies.

*   🟢 **Green Runner**: Very fast but weak health.
*   ⚪ **White Soldier**: Balanced speed and durability.
*   🟠 **Orange Elite**: Heavily armored unit.
*   ✈️ **Air Unit**: Flies over the map path! Requires careful tower placement.
*   ☠️ **Boss Tank**: High health unit that appears in later waves.

### ✨ Key Mechanics
*   **Maps & Levels**: Choose from 3 different levels (Grass, Desert, Lava) with unique paths.
*   **Save System**: High scores are saved to `game_results.csv`.
*   **User Login**: Simple login system to track player progress.
*   **Economy**: Earn money by defeating enemies to buy upgrades.
*   **Sell Feature**: Misplaced a tower? Sell it to recover some resources.
*   **Visuals**: Includes smoke effects for aircraft and floating text for earnings/damage.

---

## 🕹️ How to Play

1.  **Start**: Run the application and enter a username.
2.  **Select Level**: Choose a map difficulty.
3.  **Defend**: Click a tower from the top menu and place it on the map.
4.  **Survive**: Defend your health (starts at 100) against all waves.
5.  **Game Over**: If health reaches 0, the game ends. Try to beat your high score!

---

## 🛠️ Technical Details

*   **Language**: Java (JDK 8+)
*   **GUI Library**: Swing (JPanel, JFrame, Graphics2D)
*   **Architecture**:
    *   **Game Loop**: Runs on a separate thread to keep the UI responsive.
    *   **Inheritance**: Uses an abstract `Entity` class for all game objects.
    *   **Pathfinding**: Coordinate-based movement system.

---

## 🚀 How to Run

### Via Eclipse IDE
1.  Clone the repository:
    ```bash
    git clone https://github.com/gulce-celik/CS212-TermProject-TowerDefense.git
    ```
2.  Import as a Java Project.
3.  Run `src/tdgame/Main.java`.

### Via Command Line
```bash
javac -d bin -sourcepath src src/tdgame/Main.java
java -cp bin tdgame.Main
```

---

<div align="center">
  <p><b>Developed by Gülce Çelik</b></p>
  <p><i>Computer Engineering Student</i></p>
  <p>Fall 2025</p>
</div>
