package gomoku;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

//Класс реализует интерфейс с пользователем
public class Board extends JPanel {

    //Количество ячеек игрового поля по горизонтали и по вертикали
    private final int widthBoard;
    private final int heightBoard;

    //Константы, определяющие содержимое ячеек игрового поля
    private final int EMPTY_CELL = 0;
    private final int ZERO_CELL = 1;
    private final int CROSS_CELL = 2;

    //Цветовые константы
    private final Color backgroundColor = new Color(180, 230, 230);
    private final Color gridColor = new Color(110, 170, 250);
    private final Color zeroColor = new Color(10, 10, 255);
    private final Color crossColor = new Color(255, 10, 10);
    private final Color lineColor = new Color(20, 140, 0);

    //Вспомогательный объект, необходимый для рисования линии, соединяющй ячейки
    private Line lineBetweenCells;

    //Объект, отвечающий за реализацию игровой логики. В него отправляются данные о ходах игрока
    private Game gameLogicObject;

    //Всплывающее меню для игрового поля
    private JPopupMenu popupMenu;

    private JMenuItem newGameMenuItem;
    private JRadioButtonMenuItem playCrossMenuItem;
    private JRadioButtonMenuItem playZeroMenuItem;
    private ButtonGroup buttonGroupForMenuItem;

    //Объект, необходимый для обработки щелчков мышкой
    private MouseAdapter mouseAdapter = new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON3) popupMenu.show(e.getComponent(), e.getX(), e.getY());
            if (e.getButton() == MouseEvent.BUTTON1) {
                int x, y;
                double wCell, hCell;
                wCell = (double) getWidth() / (double) widthBoard;
                hCell = (double) getHeight() / (double) heightBoard;
                x = (int) (e.getX() / wCell);
                y = (int) (e.getY() / hCell);
                if (boardContent[y][x] == EMPTY_CELL) {
                    boardContent[y][x] = playerStrokeType;
                    paintComponent(getGraphics());
                    gameLogicObject.playerStroke(new Coord(x,y));
                }
            }
        }
    };

    //Объекты, необходимые для обработки выбора пунктов всплывающего меню
    private ActionListener newGameListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            lineBetweenCells = null;
            clearBoardContent();
            gameLogicObject.clearBoardContent();
        }
    };

    private ActionListener changePlayerStrokeType = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            switch (e.getActionCommand()) {
                case "cross": {
                    setPlayerStrokeTypeAsCross();
                    break;
                }
                case "zero": {
                    setPlayerStrokeTypeAsZero();
                }
            }
        }
    };

    //Объект, необходимый для работы с клавиатурными командами
    private KeyAdapter keyAdapter = new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_N & e.isControlDown()) {
                lineBetweenCells = null;
                clearBoardContent();
                gameLogicObject.clearBoardContent();
                return;
            }
            if (e.getKeyCode() == KeyEvent.VK_C & e.isControlDown()) {
                setPlayerStrokeTypeAsCross();
                return;
            }
            if (e.getKeyCode() == KeyEvent.VK_Z & e.isControlDown()) {
                setPlayerStrokeTypeAsZero();
            }
        }
    };

    //Содержимое ячеек игрового поля
    private int[][] boardContent;

    //Равен CROSS_CELL, если игрок играет крестиками и равен ZERO_CELL, если игрок играет ноликами
    private int playerStrokeType;

    public Board(int widthBoard, int heightBoard) {
        super(null);
        this.widthBoard = widthBoard;
        this.heightBoard = heightBoard;
        boardContent = new int[heightBoard][widthBoard];
        for (int i = 0; i < heightBoard; i++)
            for (int j = 0; j < widthBoard; j++) {
                boardContent[i][j] = EMPTY_CELL;
            }
        lineBetweenCells = null;
        playerStrokeType = CROSS_CELL;

        UIManager.put("OptionPane.yesButtonText", "Да");
        UIManager.put("OptionPane.noButtonText", "Нет");

        popupMenu = new JPopupMenu();
        newGameMenuItem = new JMenuItem("Новая игра");
        playCrossMenuItem = new JRadioButtonMenuItem("Играть крестиками", true);
        playCrossMenuItem.setActionCommand("cross");
        playZeroMenuItem = new JRadioButtonMenuItem("Играть ноликами", false);
        playZeroMenuItem.setActionCommand("zero");
        buttonGroupForMenuItem = new ButtonGroup();
        buttonGroupForMenuItem.add(playCrossMenuItem);
        buttonGroupForMenuItem.add(playZeroMenuItem);
        popupMenu.add(newGameMenuItem);
        popupMenu.addSeparator();
        popupMenu.add(playCrossMenuItem);
        popupMenu.add(playZeroMenuItem);

        newGameMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        playCrossMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
        playZeroMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));

        newGameMenuItem.addActionListener(newGameListener);
        playCrossMenuItem.addActionListener(changePlayerStrokeType);
        playZeroMenuItem.addActionListener(changePlayerStrokeType);

        addMouseListener(mouseAdapter);
        addKeyListener(keyAdapter);

        setBackground(backgroundColor);
        setFocusable(true);
    }

    //Метод инициализирует объект игровой логики
    public void setGameLogicObject(Game gameLogicObject) {
        this.gameLogicObject = gameLogicObject;
    }

    //Метод полностью очищает игровое поле (делает все ячейки пустыми и удаляет линию)
    public void clearBoardContent() {
        for (int i = 0; i < heightBoard; i++)
            for (int j = 0; j < widthBoard; j++) {
                boardContent[i][j] = EMPTY_CELL;
            }
        lineBetweenCells = null;
        repaint();
    }

    //Метод выводит линию между центрами ячеек [x1,y1] и [x2,y2]
    public void showLine(Line line) {
        lineBetweenCells = line;
        repaint();
    }

    //Метод обрабатывает ход компьютера
    public void computerStroke(Coord coord) {
        boardContent[coord.y][coord.x] = (playerStrokeType == CROSS_CELL ? ZERO_CELL : CROSS_CELL);
        repaint();
    }

    //Метод выводит сообщение с запросом о начале новой игры
    public boolean showEndGameDialog(String msg) {
        msg = "<html>" + msg + "<br>Хотите сыграть еще раз?";
        int answer;
        answer = JOptionPane.showConfirmDialog(this, msg, "Игра окончена", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
        return (answer == JOptionPane.YES_OPTION);
    }

    //Метод меняет тип клеток игрока на крестики (теперь игрок будет ходить крестиками)
    private void setPlayerStrokeTypeAsCross() {
        if (playerStrokeType == CROSS_CELL) return;
        playerStrokeType = CROSS_CELL;
        revertContent();
    }

    //Метод меняет тип клеток игрока на нолики (теперь игрок будет ходить ноликами)
    private void setPlayerStrokeTypeAsZero() {
        if (playerStrokeType == ZERO_CELL) return;
        playerStrokeType = ZERO_CELL;
        revertContent();
    }

    //Метод необходим для смены содержимого игрового поля при смене игроком типа своих клеток
    private void revertContent() {
        for (int i = 0; i < heightBoard; i++)
            for (int j = 0; j < widthBoard; j++) {
                if (boardContent[i][j] == EMPTY_CELL) continue;
                boardContent[i][j] = (boardContent[i][j] == CROSS_CELL ? ZERO_CELL : CROSS_CELL);
            }
        repaint();
    }

    //Методы, необходимые для отрисовки игрового поля на экране
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        paintGrid(g2d);
        paintCellsContent(g2d);
        if (lineBetweenCells!=null)paintLine(g2d);
    }

    private void paintGrid(Graphics2D g2d) {
        g2d.setColor(gridColor);
        double delta;
        delta = (double) getWidth() / (double) widthBoard;
        for (int i = 0; i <= widthBoard; i++) {
            g2d.drawLine((int) (delta * i), 0, (int) (delta * i), getHeight());
        }
        delta = (double) getHeight() / (double) heightBoard;
        for (int i = 0; i <= heightBoard; i++) {
            g2d.drawLine(0, (int) (i * delta), getWidth(), (int) (i * delta));
        }
    }

    private void paintCellsContent(Graphics2D g2d) {
        g2d.setStroke(new BasicStroke(7));
        double wCell, hCell;                            //Ширина и высота клетки игрового поля
        double wArea, hArea;                            //Ширина и высота области рисования внутри ячейки
        double x1, y1, x2, y2;                          //Вспомогательные переменные для хранения координат
        wCell = (double) getWidth() / (double) widthBoard;
        hCell = (double) getHeight() / (double) heightBoard;
        wArea = 0.6 * wCell;
        hArea = 0.6 * hCell;
        for (int i = 0; i < widthBoard; i++)
            for (int j = 0; j < heightBoard; j++) {
                switch (boardContent[i][j]) {
                    case ZERO_CELL: {
                        g2d.setColor(zeroColor);
                        x1 = (j * wCell) + (0.2 * wCell);
                        y1 = (i * hCell) + (0.2 * hCell);
                        g2d.drawOval((int) x1, (int) y1, (int) wArea, (int) hArea);
                        break;
                    }
                    case CROSS_CELL: {
                        g2d.setColor(crossColor);
                        x1 = (j * wCell) + (0.2 * wCell);
                        y1 = (i * hCell) + (0.2 * hCell);
                        x2 = x1 + wArea;
                        y2 = y1 + hArea;
                        g2d.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
                        g2d.drawLine((int) x2, (int) y1, (int) x1, (int) y2);
                    }
                }
            }
    }

    private void paintLine(Graphics2D g2d) {
        g2d.setStroke(new BasicStroke(5));
        g2d.setColor(lineColor);
        double wCell, hCell;
        double x1, y1;
        double x2, y2;
        wCell = (double) getWidth() / (double) widthBoard;
        hCell = (double) getHeight() / (double) heightBoard;
        x1 = (wCell * lineBetweenCells.begin.x) + (wCell * 0.5);
        y1 = (hCell * lineBetweenCells.begin.y) + (hCell * 0.5);
        x2 = (wCell * lineBetweenCells.end.x) + (wCell * 0.5);
        y2 = (hCell * lineBetweenCells.end.y) + (hCell * 0.5);
        g2d.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
    }

}
