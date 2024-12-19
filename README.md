# Chess GameManager.Game

A fully functional chess game built in Java, featuring all the standard rules of chess and a graphical user interface (GUI).

## Features

- Supports two-player gameplay on the same machine.
- Implements standard chess rules, including:
  - Pieces.Pawn promotion
  - Castling
  - En passant
  - Check and checkmate detection
- Graphical representation of the board and pieces using custom assets.
- Highlights possible moves for selected pieces.

## Requirements

- Java Development Kit (JDK) 8 or higher.

## Setup and Running the GameManager.Game

1. **Clone the repository**:
   ```bash
   git clone https://github.com/BeshoyEhab/Chess-Game
   cd Chess-Game
   ```

2. **Compile the source code**:
   ```bash
   javac -d out/production/Chess-GameManager.Game src/*.java
   ```

3. **Run the game**:
   ```bash
   java -cp out/production/Chess-GameManager.Game GameManager.Game
   ```

## Directory Structure

```
Chess-GameManager.Game/
├── src/                # Source code files
├── assets/             # Images of chess pieces
├── out/                # Compiled Java classes
├── .idea/              # IntelliJ IDEA project files
└── README.md           # Project documentation
```

## How to Play

1. Run the game using the instructions above.
2. The chessboard GUI will appear.
3. Players take turns to move their pieces by clicking on them and selecting valid moves.
4. The game ends when one player checkmates the other.

## Contributing

Contributions are welcome! To contribute:

1. Fork the repository.
2. Create a feature branch: `git checkout -b feature-name`.
3. Commit your changes: `git commit -m 'Add feature-name'`.
4. Push to the branch: `git push origin feature-name`.
5. Open a pull request.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Chess piece images sourced from the project assets.
- Built using Java and designed for learning and entertainment purposes.

