import controller.GameController;
import models.*;
import stratergies.ColWinningStrategy;
import stratergies.DiagWinningStrategy;
import stratergies.RowWinningStrategy;
import stratergies.WinningStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        GameController gameController = new GameController();
        Scanner scanner = new Scanner(System.in);
       int dimension = 3;
       List<Player> players = new ArrayList<>();
       players.add(new Player(1L,  new Symbol('X'),"Dhiraj", PlayerType.HUMAN));
       players.add(new Bot(2L,  new Symbol('O'),"Suraj", BotDifficultyLevel.EASY));
       List<WinningStrategy> winningStrategies = List.of(
               new RowWinningStrategy(),
               new ColWinningStrategy(),
               new DiagWinningStrategy()
       );

       Game game = gameController.startGame(dimension, players, winningStrategies);// starting game with inputs as a dimension, list of players and list of wining strategy

        while(gameController.checkState(game).equals(GameState.IN_PROGRESS)) {
            gameController.printBoard(game);
            System.out.println("Do you want to undo? (y/n)");
            String undoAnswer = scanner.next();
            if (undoAnswer.equalsIgnoreCase("y")) {
                gameController.undo(game);
                continue;
            }
            gameController.makeMove(game);
        }

        System.out.println("Game is over");
        gameController.printBoard(game);
        GameState gameState = gameController.checkState(game);

        if (gameState.equals(GameState.DRAW)) {
            System.out.println("Game has drawn");
        } else {
            System.out.println("We have a winner");
        }
    }
}
