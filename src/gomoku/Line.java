package gomoku;

public class Line {
    Coord begin;
    Coord end;
    int lenght;

    public Line(Coord begin, Coord end, int lenght) {
        this.begin = begin;
        this.end = end;
        this.lenght = lenght;
    }

}
