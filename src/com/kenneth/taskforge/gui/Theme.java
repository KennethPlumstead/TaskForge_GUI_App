package com.kenneth.taskforge.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

public final class Theme {
    // Dark palette (single mode)
    public static final Color FRAME   = new Color(19,21,25);   // outside frame feel
    public static final Color BG      = new Color(24,26,31);   // app background
    public static final Color BG_ALT  = new Color(30,32,38);   // table background
    public static final Color SURFACE = new Color(36,39,46);   // toolbar/status bars
    public static final Color SURFACE_HI = new Color(46,49,57); // header strip
    public static final Color ACCENT  = new Color(0,190,255);
    public static final Color TEXT    = new Color(232,232,236);
    public static final Color MUTED   = new Color(160,165,175);
    public static final Color BORDER  = new Color(62,67,76);    // regular border
    public static final Color BORDER_STRONG = new Color(96,102,116); // emphasis border
    public static final Color HILITE  = new Color(60,95,160);

    public static void applyNimbus() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}
        UIManager.put("control", BG);
        UIManager.put("info", SURFACE);
        UIManager.put("nimbusBase", SURFACE);
        UIManager.put("nimbusBlueGrey", SURFACE);
        UIManager.put("nimbusLightBackground", BG_ALT);
        UIManager.put("text", TEXT);
        UIManager.put("nimbusSelectionBackground", HILITE);
        UIManager.put("nimbusSelection", HILITE);
        UIManager.put("textForeground", TEXT);
        UIManager.put("Menu.foreground", TEXT);
        UIManager.put("Label.foreground", TEXT);
        UIManager.put("Button.background", SURFACE);
        UIManager.put("Button.foreground", TEXT);
        UIManager.put("Table.background", BG_ALT);
        UIManager.put("Table.foreground", TEXT);
        UIManager.put("Table.gridColor", BORDER);
        UIManager.put("TableHeader.background", SURFACE_HI);
        UIManager.put("TableHeader.foreground", TEXT);
        UIManager.put("TextField.background", SURFACE);
        UIManager.put("TextField.foreground", TEXT);
    }

    public static DefaultTableCellRenderer tableRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                          boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setForeground(TEXT);
                c.setBackground(isSelected ? HILITE : BG_ALT);
                if (c instanceof JComponent) {
                    ((JComponent)c).setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
                }
                return c;
            }
        };
    }

    public static void styleTable(JTable table) {
        table.setBackground(BG_ALT);
        table.setForeground(TEXT);
        table.setRowHeight(28);
        table.setShowGrid(true);
        table.setGridColor(BORDER);
        table.setAutoCreateRowSorter(true);
        table.setFillsViewportHeight(true);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS); // fill width

        int[] widths = {320, 90, 120, 100, 220};
        for (int i = 0; i < widths.length && i < table.getColumnModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
            table.getColumnModel().getColumn(i).setCellRenderer(tableRenderer());
        }
        JTableHeader header = table.getTableHeader();
        header.setBackground(SURFACE_HI);
        header.setForeground(TEXT);
        header.setReorderingAllowed(false);
        header.setResizingAllowed(true);
    }
}
