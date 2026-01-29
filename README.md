# Tower Defense: The Last Stand

**Course:** CSE 212 - Software Development Methodologies  
**Semester:** Spring 2026  
**Developer:** Gülce Çelik  

---

## Project Overview

"Tower Defense: The Last Stand" is a strategy-based graphical application developed as a final project for CSE 212. The primary objective of this project is to demonstrate the practical application of core software engineering principles, including Object-Oriented Programming (OOP), concurrency control, and event-driven architecture within the Java ecosystem.

The application is built entirely using the standard Java Development Kit (JDK) and Swing library, ensuring cross-platform compatibility without reliance on external game engines.

## Technical Architecture

This project strictly adheres to the methodologies discussed in the course curriculum:

*   **Object-Oriented Design**:
    *   **Inheritance Hierarchy**: An abstract `Entity` class provides the foundation for all game objects (`Tower`, `Enemy`, `Projectile`), promoting code reuse and polymorphism.
    *   **Encapsulation**: Game states (e.g., resources, wave progression) are encapsulated within the `GamePanel` class, protecting data integrity.
*   **Multithreading & Concurrency**:
    *   The game loop operates on a dedicated `Thread`, distinct from the Swing Event Dispatch Thread (EDT). This separation ensures that logic updates do not block the user interface rendering.
    *   `SwingUtilities.invokeLater` is utilized to safely update UI components from the game thread.
*   **Design Patterns**:
    *   **Observer Pattern**: Implemented via Swing listeners for handling user input events.
    *   **Factory Method**: Used implicitly in the creation of different enemy and tower types.

## Gameplay Specifications

The game challenges users to defend a base from waves of autonomous enemies using a variety of defensive structures.

### Defensive Units (Towers)

| Tower Type | Cost | Characteristics |
| :--- | :--- | :--- |
| **Fast Tower** | $100 | High rate of fire, low damage per hit. Effective against unarmored targets. |
| **Strong Tower** | $400 | Low rate of fire, high impact damage. Capable of neutralizing armored units. |
| **Double Rocket** | $800 | Simultaneous dual-projectile launch system. Provides area denial capability. |
| **Big Rocket** | $1200 | Large area-of-effect (AOE) explosive damage. High strategic value against grouped enemies. |

### Enemy Units

*   **Green Class**: High mobility, low durability structure.
*   **White Class**: Standard infantry unit with balanced attributes.
*   **Orange Class**: Heavy infantry with increased hit points and armor.
*   **Air Units**: Airborne enemies that bypass terrain obstacles, requiring specific targeting priority.
*   **Tank Class (Boss)**: High-durability unit serving as a "boss" encounter, requiring sustained damage output to defeat.

## Installation and Execution

### Prerequisites
*   Java Runtime Environment (JRE) 8 or higher.
*   IDE (Eclipse, IntelliJ) or Command Line Interface.

### Running from Source (Eclipse)
1.  Import the project via **File > Open Projects from File System**.
2.  Navigate to the `src/tdgame` package.
3.  Execute `Main.java` as a Java Application.

### Running from Command Line
Compile and execute the application using the following commands from the project root directory:

```bash
javac -d bin -sourcepath src src/tdgame/Main.java
java -cp bin tdgame.Main
```

---

**Contact Information**  
Gülce Çelik  
[GitHub Profile](https://github.com/gulce-celik)
