# 🏰 Tower Defense: The Last Stand

<div align="center">

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Swing](https://img.shields.io/badge/Swing-GUI-blue?style=for-the-badge)
![Course](https://img.shields.io/badge/CSE%20212-Software%20Development-purple?style=for-the-badge)
![Grade](https://img.shields.io/badge/Grade-A%2B-green?style=for-the-badge)

### **CSE 212 - Software Development Methodologies**
#### Final Project

Developed by:
**Gülce Çelik**

</div>

---

## 📖 Project Overview

**Tower Defense: The Last Stand** is a high-performance 2D strategy game engineered in **Java**. This project demonstrates mastery of advanced software engineering concepts including **Multithreading**, **Object-Oriented Design Principles (OOP)**, and **Event-Driven UI**.

Defend your base against waves of relentless enemies by strategically managing resources and deploying advanced defensive towers.

---

## 🎮 Features & Mechanics

### 🛡️ Defensive Systems (Towers)

| Icon | Class | Cost | Type | Strategic Utility |
| :---: | :--- | :--- | :--- | :--- |
| <img src="https://img.icons8.com/color/48/turret.png" width="30"/> | **Fast Tower** | `$100` | RAPID FIRE | High attack speed, low damage. Best for early waves and unarmored enemies. |
| <img src="https://img.icons8.com/isometric/48/artillery.png" width="30"/> | **Strong Tower** | `$400` | ARMOR PIERCING | High single-target damage. Penetrates heavy armor. |
| <img src="https://img.icons8.com/external-flaticons-lineal-color-flat-icons/48/missile.png" width="30"/> | **Double Rocket**| `$800` | MULTI-TARGET | Fires twin missiles. Excellent for crowd control. |
| <img src="https://img.icons8.com/dusk/48/explosion.png" width="30"/> | **Big Rocket** | `$1200`| AOE NUKE | Massive area-of-effect damage. The ultimate weapon against bosses. |

### ⚔️ The Enemy

*   **⚡ Green Runner**: Extremely fast but weak. They swarm in large numbers.
*   **🛡️ White Soldier**: Balanced health and speed. The backbone of the enemy army.
*   **🔱 Orange Elite**: High health and armor. Requires concentrated fire.
*   **✈️ Air Support**: **Planes** that fly over obstacles! requires Anti-Air capability.
*   **☠️ Boss Tank**: A massive, heavily armored unit. The ultimate test of your defense.

---

## 🛠️ Technical Architecture

This project was built to meet specific academic requirements for **CSE 212**:

*   **Core Logic**: Implemented in pure Java without external game engines.
*   **Concurrency**: Uses a dedicated `Thread` for the game loop to maintain 60 FPS separate from the Swing Event Dispatch Thread (EDT).
*   **OOP**: Extensive use of **Inheritance** (`Entity` -> `Tower`, `Enemy`) and **Polymorphism** for dynamic object management.
*   **Persistence**: File I/O for saving high scores and user data.

---

## 🚀 Installation

### 1️⃣ Clone the Repository
```bash
git clone https://github.com/gulce-celik/CS212-TermProject-TowerDefense.git
cd CS212-TermProject-TowerDefense
```

### 2️⃣ Run with Eclipse (UI)
1.  Open Eclipse IDE.
2.  `File` > `Open Projects from File System...`
3.  Select the project folder.
4.  Run `src/tdgame/Main.java`.

### 3️⃣ Run form Terminal
```bash
javac -d bin -sourcepath src src/tdgame/Main.java
java -cp bin tdgame.Main
```

---

<div align="center">
  <p>Made with ❤️ by <b>Gülce Çelik</b></p>
  <p><i>CSE 212 Spring 2026</i></p>
</div>
