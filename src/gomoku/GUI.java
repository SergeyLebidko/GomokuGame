package gomoku;

import javax.swing.*;
import java.awt.*;

//Этот класс реализует главное окно приложения
public class GUI {

    private JFrame frm;

    public GUI(int widthFrm, int heightFrm) {
        frm = new JFrame("Гомоку");
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frm.setSize(widthFrm, heightFrm);
        frm.setResizable(false);
        int xPos = Toolkit.getDefaultToolkit().getScreenSize().width / 2 - widthFrm / 2;
        int yPos = Toolkit.getDefaultToolkit().getScreenSize().height / 2 - heightFrm / 2;
        frm.setLocation(xPos, yPos);
    }

    public void showGUI(){
        frm.setVisible(true);
    }

    public void setBoard(Board board){
        frm.setContentPane(board);
    }

}
