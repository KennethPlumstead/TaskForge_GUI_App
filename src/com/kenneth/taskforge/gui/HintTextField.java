package com.kenneth.taskforge.gui;

import javax.swing.*;
import java.awt.*;

public class HintTextField extends JTextField {
    private String hint;
    public HintTextField(String hint, int columns){
        super(columns);
        this.hint = hint;
        setToolTipText(hint);
    }
    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        if(getText().isEmpty() && !isFocusOwner()){
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(Theme.MUTED);
            g2.setFont(getFont().deriveFont(Font.PLAIN));
            Insets in = getInsets();
            g2.drawString(hint, in.left + 2, getHeight()/2 + g2.getFontMetrics().getAscent()/2 - 2);
            g2.dispose();
        }
    }
}
