# 🎄 Snake & Ladders – Christmas Party Edition

Final Project for **Algorithms and Data Structures** Course

---

## 📌 Project Description

**Snake & Ladders – Christmas Party Edition** is a Java-based GUI implementation of the classic *Snake and Ladders* board game with a Christmas theme. This project was developed as a **Final Project** to demonstrate the application of algorithms and data structures in a real, interactive software system.

The game supports **multiplayer gameplay**, a **ranking system**, and applies **graph algorithms** to manage movement logic and board traversal.

---

## 🎯 Project Objectives

* Apply **Algorithms & Data Structures** concepts in a practical case study
* Implement **data structures**, **graph algorithms**, and **file handling**
* Develop a modular and structured **Java GUI application**
* Integrate game logic with visual and interactive components

---

## 🧠 Algorithms & Data Structures Used

* **Graph & Shortest Path Algorithm**
  Implemented using **Dijkstra’s Algorithm** to simulate board traversal and movement calculations.

* **Arrays & Lists**
  Used to store tiles, player data, positions, and turn order.

* **Object-Oriented Programming (OOP)**

  * Encapsulation
  * Inheritance
  * Modular class design

* **File Handling**
  Used to store and retrieve player rankings (`ranking.txt`).

---

## 🎮 Main Features

* 🎲 Snake & Ladders gameplay with Java GUI
* 👥 Multiplayer support
* 🧩 Board tiles with bonus and penalty values
* 🪜 Snakes and ladders with visual representation
* 🔊 Interactive sound effects
* 🏆 Ranking and leaderboard system
* 🎄 Christmas-themed user interface

---

## 🖥️ Application Screens

### 1. Main Menu

* Player count selection
* Champion ranking access
* Player setup

### 2. Game Board

* Snake & Ladders board visualization
* Active player indicator
* Dice roll button
* Session leaderboard display

---

## 📁 Project Structure

```
SNAKEANDLADDERS/
│
├── assets/                  # Image and audio assets
├── Board.java               # Game board logic
├── BoardPanel.java          # Main board panel
├── BoardPanelIsometrik.java # Alternative board visualization
├── DijkstraSolver.java      # Dijkstra algorithm implementation
├── GameFrame.java           # Main application frame
├── HistoryManager.java      # Game history management
├── Main.java                # Application entry point
├── Player.java              # Player model
├── SoundManager.java        # Sound effect manager
├── StyleTheme.java          # UI theme configuration
├── Tile.java                # Board tile representation
├── VideoBackgroundPanel.java# Animated background
├── WinDatabase.java         # Win and score data management
├── ranking.txt              # Player ranking storage
└── README.md                # Project documentation
```

---

## ⚙️ How to Run the Program

1. Ensure **Java JDK** is installed
2. Clone or extract the project directory
3. Compile and run the application:

```bash
javac Main.java
java Main
```

Alternatively, run the project directly using an IDE such as **IntelliJ IDEA** or **VS Code**.

---

## 🏁 Conclusion

This project was developed as a practical implementation of **Algorithms and Data Structures**, focusing on game logic, algorithmic efficiency, and Java GUI application design.

It demonstrates how theoretical algorithm concepts can be translated into a functional and interactive software product.

---

✨ *Snake & Ladders – Christmas Party Edition* ✨
