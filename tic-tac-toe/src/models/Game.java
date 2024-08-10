package models;

import exceptions.MoreThanOneBotException;
import stratergies.WinningStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game {
    private List<Player> players;
    private Board board;
    private List<WinningStrategy> winningStrategies;
    private int nextMovePlayerIndex;
    private Player winner;
    private GameState gameState;
    private List<Move> moves;
    private Game(int dimension, List<Player> players, List<WinningStrategy> winningStrategies) {
        this.players = players;
        this.winningStrategies = winningStrategies;
        this.nextMovePlayerIndex = 0;
        this.gameState = GameState.IN_PROGRESS;
        this.moves = new ArrayList<>();
        this.board = new Board(dimension);
    }

    public static Builder getBuilder() throws Exception{
        return new Builder();
    }

    public List<Move> getMoves() {
        return moves;
    }

    public void setMoves(List<Move> moves) {
        this.moves = moves;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public List<WinningStrategy> getWinningStrategies() {
        return winningStrategies;
    }

    public void setWinningStrategies(List<WinningStrategy> winningStrategies) {
        this.winningStrategies = winningStrategies;
    }

    public int getNextMovePlayerIndex() {
        return nextMovePlayerIndex;
    }

    public void setNextMovePlayerIndex(int nextMovePlayerIndex) {
        this.nextMovePlayerIndex = nextMovePlayerIndex;
    }

    public Player getWinner() {
        return winner;
    }

    public void setWinner(Player winner) {
        this.winner = winner;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public static class Builder {
        private List<Player> players;
        private List<WinningStrategy> winningStrategies;
        private int dimension;

        public Builder setDimension(int dimension) {
            this.dimension = dimension;
            return this;
        }

        public Builder setPlayers(List<Player> players) {
            this.players = players;
            return this;
        }

        public Builder addPlayer(Player player) {
            this.players.add(player);
            return this;
        }

        public Builder setWinningStrategies(List<WinningStrategy> winningStrategies) {
            this.winningStrategies = winningStrategies;
            return this;
        }

        public Builder addWinningStrategies(WinningStrategy winningStrategy) {
            this.winningStrategies.add(winningStrategy);
            return this;
        }

        public Game build() {
            validate();
            return new Game(dimension, players, winningStrategies);
        }

        private void validate() {

        }

        private void validateBotCounts() throws MoreThanOneBotException {
            int botCount = 0;
            for (Player player : players) {
                if (player.getPlayerType().equals(PlayerType.BOT)) {
                    botCount += 1;
                }
            }
            if (botCount > 1) {
                throw new MoreThanOneBotException();
            }
        }

        private void validateDimensionAndPlayersCount() throws Exception {
            if (players.size() != dimension - 1) {
                throw new Exception();
            }
        }

        private void validateUniqueSymbolsForPlayers() throws Exception {
            Map<Character, Integer> map = new HashMap<>();
            for (Player player : players) {
                if (!map.containsKey(player.getSymbol().getaChar())) {
                    map.put(player.getSymbol().getaChar(), 0);
                }
                map.put(
                        player.getSymbol().getaChar(),
                        map.get(player.getSymbol().getaChar()) + 1
                );

                if (map.get(player.getSymbol().getaChar()) > 1) {
                    throw new Exception();
                }
            }
        }
    }

    public void printBoard() {
        board.printBoard();
    }

    private boolean validateMove(Move move) {
        int row = move.getCell().getRow();
        int col = move.getCell().getCol();

        if (row >= board.getSize()) return false;
        if (col >= board.getSize()) return false;

        if (board.getBoard().get(row).get(col).getCellState().equals(CellState.EMPTY)) return true;

        return false;
    }

    private boolean checkWinner(Board board, Move move) {
        for (WinningStrategy winningStrategy : winningStrategies) {
            if (winningStrategy.checkWinner(board, move)) {
                return true;
            }
        }

        return false;
    }

    public void makeMove() {
        Player currentMovePlayer = players.get(nextMovePlayerIndex);
        System.out.println("It is " + currentMovePlayer.getName() + "'s turn please make your move");
        Move move = currentMovePlayer.makeMove(board);
        System.out.println(currentMovePlayer.getName() + " has made a move at row : " + move.getCell().getRow() + " and col: " +  move.getCell().getCol());

        if (!validateMove(move)) {
            System.out.println("Invalid move, Please try again");
            return;
        }

        int row = move.getCell().getRow();
        int col = move.getCell().getCol();

        Cell cellToChange = board.getBoard().get(row).get(col);
        cellToChange.setCellState(CellState.FILLED);
        cellToChange.setPlayer(currentMovePlayer);

        //adding list of moves

        Move finalMove = new Move(cellToChange, currentMovePlayer);
        moves.add(finalMove);
        nextMovePlayerIndex += 1;

        // to avoid index out of bound we mod it to player size;
        nextMovePlayerIndex %= players.size();

        if (checkWinner(board, finalMove)) {
            gameState = GameState.WINNER;
            winner = currentMovePlayer;
        } else if (moves.size() == board.getSize() * board.getSize()) {
            gameState = GameState.DRAW;
        }
    }

    public void undo() {
        if (moves.size() == 0) {
            System.out.println("No move to undo");
            return;
        }

        Move lastMove = moves.get(moves.size() - 1);
        moves.remove(lastMove);
        Cell cell = lastMove.getCell();
        cell.setPlayer(null);
        cell.setCellState(CellState.EMPTY);

        for (WinningStrategy winningStrategy : winningStrategies) {
            winningStrategy.handleUndo(board, lastMove);
        }

        nextMovePlayerIndex -= 1;
        nextMovePlayerIndex = (nextMovePlayerIndex + players.size()) % players.size();// player who undo the move it will be his turn only, in makeMove we are increasing
                                                                                    // nextMovePlayerIndex. So in undo we will decrease it. Mod logic is applied here to prevent negative numbers
                                                                                    // also, we have to decrease maps in wining strategies
    }
}
