package gomoku;

import javax.swing.*;

public class MainClass {

    //Ширина и высота окна программы (в пикселях)
    private static final int WIDTH_WINDOW=800;
    private static final int HEIGHT_WINDOW=800;

    //Количество клеток на игровом поле по горизонтали и по вертикали
    private static final int WIDTH_BOARD=15;
    private static final int HEIGHT_BOARD=15;

    private static GUI gui;
    private static Board board;
    private static Game game;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                gui=new GUI(WIDTH_WINDOW, HEIGHT_WINDOW);
                board=new Board(WIDTH_BOARD, HEIGHT_BOARD);
                game=new Game(WIDTH_BOARD, HEIGHT_BOARD);
                board.setGameLogicObject(game);
                game.setUserInterfaceObject(board);
                gui.setBoard(board);
                gui.showGUI();
            }
        });
    }

}
