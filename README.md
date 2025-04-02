# Chess Game

A complete chess game implemented in Java, featuring a graphical user interface (GUI) and support for all standard chess rules.

## Features

- Local two-player mode.
- Full adherence to chess rules:
  - Pawn promotion
  - Castling
  - En passant
  - Check and checkmate validation
- Interactive GUI with highlighted possible moves.
- Custom-designed chess piece assets.

## Requirements

- Java Development Kit (JDK) 8 or later.

## How to Run

1. **Clone the Repository**  
   Clone the project to your local machine using Git:

   ```bash
   git clone https://github.com/BeshoyEhab/Chess-Game
   cd Chess-Game
   ```

2. **Compile the Source Code**  
   Use the following command to compile the Java source files:

   ```bash
   javac -d out/production/Chess-Game src/**.java
   ```

3. **Start the Game**  
   Run the compiled program to launch the chess game:

   ```bash
   java -cp out/production/Chess-Game Main
   ```

## Project Structure

```
Chess-Game/
├── src/                # Java source files
├── assets/             # Chess piece images and other graphical resources
├── README.md           # Project documentation
└── LICENSE             # License information
```

## License

This project is distributed under the MIT License. Refer to the [LICENSE](LICENSE) file for more details.
