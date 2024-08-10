package controller;

import models.Game;
import models.GameState;
import models.Player;
import stratergies.WinningStrategy;

import java.util.List;

public class GameController {
    public Game startGame(int dimension, List<Player> players, List<WinningStrategy> winningStrategies) throws Exception {
        return Game.getBuilder()
                .setDimension(dimension)
                .setWinningStrategies(winningStrategies)
                .setPlayers(players)
                .build();

    }

    public void printBoard(Game game) {
        game.printBoard();
    }

    public GameState checkState(Game game) {
        return game.getGameState();
    }

    public void makeMove(Game game) {
        game.makeMove();
    }

    public void undo(Game game) {
        game.undo();
    }

    public String getWinner(Game game) {
        return game.getWinner().getName();
    }
}
