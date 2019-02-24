package gomoku;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

//Класс реализует игровую логику
public class Game {

    //Количество ячеек игрового поля по горизонтали и по вертикали
    private int widthBoard;
    private int heightBoard;

    //Константы, определяющие содержимое ячеек игрового поля
    private final int EMPTY_CELL = 0;
    private final int PLAYER_CELL = 1;
    private final int COMPUTER_CELL = 2;

    //Объект, реализующий графический интерфейс. В него отправляются данные о ходах компьютера
    private Board userInterfaceObject;

    //Содержимое ячеек игрового поля
    private int[][] boardContent;

    //Истинно только когда игровое поле не пусто
    private boolean isBoardEmpty;

    public Game(int widthBoard, int heightBoard) {
        this.widthBoard = widthBoard;
        this.heightBoard = heightBoard;
        boardContent = new int[heightBoard][widthBoard];
        for (int i = 0; i < heightBoard; i++)
            for (int j = 0; j < widthBoard; j++) {
                boardContent[i][j] = EMPTY_CELL;
            }
        isBoardEmpty = true;
    }

    //Метод устанавливает объект графического интерфейса
    public void setUserInterfaceObject(Board userInterfaceObject) {
        this.userInterfaceObject = userInterfaceObject;
    }

    //Метод очищает игровое поле
    public void clearBoardContent() {
        for (int i = 0; i < heightBoard; i++)
            for (int j = 0; j < widthBoard; j++) {
                boardContent[i][j] = EMPTY_CELL;
            }
        isBoardEmpty = true;
    }

    //Метод принимает ход игрока
    public void playerStroke(Coord coord) {
        isBoardEmpty = false;
        strokeProcessing(coord, PLAYER_CELL);
        if (!isBoardEmpty) computerStroke(getNextComputerStroke());
    }

    //Метод принимает ход компьютера
    private void computerStroke(Coord coord) {
        userInterfaceObject.computerStroke(coord);
        strokeProcessing(coord, COMPUTER_CELL);
    }

    //Метод необходим для обработки результатов хода
    private void strokeProcessing(Coord coord, int stroke) {
        boardContent[coord.y][coord.x] = stroke;

        //Проверяем условие победы или поражения и выводим диалог о завершении или начале новой игры
        Line line = getMaxLine(coord, stroke);
        if (isFinalLine(line)) {
            String msg = "";
            if (stroke == PLAYER_CELL) msg = "Вы победили!";
            if (stroke == COMPUTER_CELL) msg = "Вы проиграли!";
            userInterfaceObject.showLine(line);
            boolean answer = userInterfaceObject.showEndGameDialog(msg);
            if (answer) {
                userInterfaceObject.clearBoardContent();
                clearBoardContent();
                return;
            }
            System.exit(0);
        }

        //Проверяем условие ничьи и выводим диалог о завершении или начале новой игры
        if (isBoardFull()) {
            boolean answer = userInterfaceObject.showEndGameDialog("Ничья!");
            if (answer) {
                userInterfaceObject.clearBoardContent();
                clearBoardContent();
                return;
            }
            System.exit(0);
        }

    }

    //Метод ищет следующий ход компьютера
    private Coord getNextComputerStroke() {
        //Объявляем необходимые переменные
        Line line;
        Coord tmpCoord;

        //Получаем список доступных ходов
        Coord[] avlStroks = getAvailableStroke();

        //Этап 1: Среди доступных ходов ищем ход, который сразу даёт компьютеру победу
        for (Coord coord : avlStroks) {
            line = getMaxLine(coord, COMPUTER_CELL);
            if (isFinalLine(line)) return coord;
        }

        //Этап 2: Если на предыдущем этапе победных ходов для компьютера не было обнаружено,
        //ищем победный ход для игрока и сразу же блокируем его
        for (Coord coord : avlStroks) {
            line = getMaxLine(coord, PLAYER_CELL);
            if (isFinalLine(line)) return coord;
        }

        //Этап 3: Среди всех доступных ходов ищем тот, который позволяет компьютеру создать вилку
        tmpCoord = getForkStroke(avlStroks, COMPUTER_CELL);
        if (tmpCoord != null) return tmpCoord;

        //Этап 4: Среди всех доступных ходов ищем тот, который позволяет игроку создать вилку и сразу же блокируем его
        tmpCoord = getForkStroke(avlStroks, PLAYER_CELL);
        if (tmpCoord != null) return tmpCoord;

        //Объявляем необходимые на этапах 5,6,7 переменные
        int lenMaxLineComputer, lenMaxLinePlayer;
        Coord coordMaxLineComputer, coordMaxLinePlayer;
        coordMaxLineComputer = coordMaxLinePlayer = null;

        //Этап 5: Ищем ход, дающий компьютеру максимально длинную линию. Фиксируем его и длину этой линии
        lenMaxLineComputer = 0;
        for (Coord coord : avlStroks) {
            line = getMaxLine(coord, COMPUTER_CELL);
            if (line.lenght > lenMaxLineComputer) {
                lenMaxLineComputer = line.lenght;
                coordMaxLineComputer = coord;
            }
        }

        //Этап 6: Ищем ход, дающий игроку максимально длинную линию. Фиксируем его и длину этой линии
        lenMaxLinePlayer = 0;
        for (Coord coord : avlStroks) {
            line = getMaxLine(coord, PLAYER_CELL);
            if (line.lenght > lenMaxLinePlayer) {
                lenMaxLinePlayer = line.lenght;
                coordMaxLinePlayer = coord;
            }
        }

        //Этап 7: Выбор наилучшего хода на основе сравнения максимально длинных линий игрока и компьютера
        if (lenMaxLinePlayer == 4 & lenMaxLineComputer < 4) return coordMaxLinePlayer;

        //Этап 8: Если на предыдущих этапах не удалось определить оптимальный ход, то случайным образом выбираем ход из
        //тех ходов, которые дают компьютеру максимально длинную линию
        ArrayList<Coord> tmpCoords = new ArrayList<>();
        Random rnd = new Random();
        for (Coord coord : avlStroks) {
            line = getMaxLine(coord, COMPUTER_CELL);
            if (line.lenght == lenMaxLineComputer) {
                tmpCoords.add(coord);
            }
        }
        coordMaxLineComputer = tmpCoords.get(rnd.nextInt(tmpCoords.size()));
        return coordMaxLineComputer;
    }

    //Метод возвращает координаты хода, дающего вилку. Если такого хода нет, возвращается null
    private Coord getForkStroke(Coord[] avlStroks, int stroke) {
        int count;
        Line line;
        Coord[] avlStroksTmp;
        for (Coord coord : avlStroks) {
            boardContent[coord.y][coord.x] = stroke;
            count = 0;
            avlStroksTmp = getAvailableStroke();
            for (Coord coordTmp : avlStroksTmp) {
                line = getMaxLine(coordTmp, stroke);
                if (isFinalLine(line)) {
                    count++;
                    if (count > 1) break;
                }
            }
            boardContent[coord.y][coord.x] = EMPTY_CELL;
            if (count > 1) return coord;
        }
        return null;
    }

    //Метод возвращает true, если на игровом поле не осталось доступных ходов, то есть все клетки заполены
    private boolean isBoardFull() {
        for (int i = 0; i < heightBoard; i++)
            for (int j = 0; j < widthBoard; j++) {
                if (boardContent[i][j] == EMPTY_CELL) return false;
            }
        return true;
    }

    //Метод возвращает наиболее длинную линию, которая получается в результате хода игрока
    // или компьютера (параметр stroke) в позицию coord
    private Line getMaxLine(Coord coord, int stroke) {
        Line maxLine;
        int[] dx = {0, 1, 1, 1, 0, -1, -1, -1};
        int[] dy = {-1, -1, 0, 1, 1, 1, 0, -1};
        int xTmp, yTmp;
        int[] len = new int[8];
        int numMaxLen = 0;
        int maxLen = 0;
        int lenTmp;

        //В цикле перебираем направления
        for (int i = 0; i < 8; i++) {
            xTmp = coord.x;
            yTmp = coord.y;
            len[i] = 0;
            for (int j = 1; ; j++) {
                xTmp = coord.x + dx[i] * j;
                yTmp = coord.y + dy[i] * j;
                if (xTmp < 0 || xTmp == widthBoard || yTmp < 0 || yTmp == heightBoard) break;
                if (boardContent[yTmp][xTmp] != stroke) break;
                len[i]++;
            }
        }

        //Ищем направление с максимальной длиной линии
        for (int i = 0; i < 4; i++) {
            lenTmp = len[i] + 1 + len[i + 4];
            if (lenTmp > maxLen) {
                maxLen = lenTmp;
                numMaxLen = i;
            }
        }

        //Формируем ответ
        Coord begin;
        Coord end;
        begin = new Coord(coord.x + dx[numMaxLen] * len[numMaxLen], coord.y + dy[numMaxLen] * len[numMaxLen]);
        end = new Coord(coord.x + dx[numMaxLen + 4] * len[numMaxLen + 4], coord.y + dy[numMaxLen + 4] * len[numMaxLen + 4]);
        maxLine = new Line(begin, end, maxLen);

        return maxLine;
    }

    //Метод проверяет, является ли переданная линия достаточной для завершения игры
    private boolean isFinalLine(Line line) {
        return line.lenght >= 5;
    }

    //Метод возвращает список ячеек, в которые возможен ход
    private Coord[] getAvailableStroke() {
        HashSet<Coord> avlCoordSet = new HashSet<>();
        Coord[] avlCoordArray;

        //Заполняем массивы смещений
        int[] dx = new int[16];
        int[] dy = new int[16];
        int t = 0;
        int sum = 0;
        for (int i = (-2); i < 3; i++)
            for (int j = (-2); j < 3; j++) {
                sum = Math.abs(i) + Math.abs(j);
                if (sum == 0 || sum == 3) continue;
                dx[t] = j;
                dy[t] = i;
                t++;
            }

        //Формируем множество доступных для следующего ходя ячеек
        int x, y;
        for (int i = 0; i < heightBoard; i++)
            for (int j = 0; j < widthBoard; j++) {
                if (boardContent[i][j] != EMPTY_CELL) {
                    for (int k = 0; k < 16; k++) {
                        x = j + dx[k];
                        y = i + dy[k];
                        if (x < 0 || x >= widthBoard || y < 0 || y >= heightBoard) continue;
                        if (boardContent[y][x] != EMPTY_CELL) continue;
                        avlCoordSet.add(new Coord(x, y));
                    }
                }
            }

        avlCoordArray = new Coord[avlCoordSet.size()];
        avlCoordSet.toArray(avlCoordArray);
        return avlCoordArray;
    }

}
