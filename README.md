# Battleship with Java

A console-based Battleship game written in Java as part of the JetBrains Academy / Hyperskill Java course.

## About the project

This project implements the classic Battleship game for two players.

Each player places five ships on a 10×10 game field and then takes turns shooting at the opponent's fleet. The game continues until one player sinks all of the opponent's ships.

## Features

* 10×10 game field
* Two-player mode
* Ship placement validation
* Five different ships
* Horizontal and vertical ship placement
* Prevention of overlapping or touching ships
* Fog of war
* Hit and miss detection
* Detection of sunk ships
* Input validation
* Turn-based gameplay
* Win condition

## Ships

* Aircraft Carrier — 5 cells
* Battleship — 4 cells
* Submarine — 3 cells
* Cruiser — 3 cells
* Destroyer — 2 cells

## Technologies

* Java
* Object-oriented programming concepts
* Arrays
* Methods
* Loops
* Exception handling
* User input with `Scanner`

## How to run

Compile the program:

```bash
javac src/battleship/Main.java
```

Run it:

```bash
java -cp src battleship.Main
```

## What I learned

While working on this project, I practiced working with multidimensional arrays, methods, loops, input validation, exception handling, and program state.

I also learned how to break a larger program into smaller methods and implement game logic such as ship placement, shooting, fog of war, multiplayer turns, and win conditions.

## Author

Created as a learning project for the JetBrains Academy / Hyperskill Java course.
