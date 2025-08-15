package com.kenneth.taskforge.gui;

import com.kenneth.taskforge.model.Plan;
import com.kenneth.taskforge.model.Task;
import com.kenneth.taskforge.store.Store;
import com.kenneth.taskforge.export.MarkdownExport;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TaskForgeApp extends JFrame {

    private Plan plan;
    private HintTextField weekField;
    private TaskTableModel model;
    private JTable table;
    private JLabel status;
    private JLabel emptyHint;

    public TaskForgeApp() {
        super("TaskForge — Weekly Planner");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(980, 640));
        setLocationByPlatform(true);
        setIconImage(loadIcon("/app.png"));

        Theme.applyNimbus();
        plan = Store.load();

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.FRAME);
        root.setBorder(new EmptyBorder(12,12,12,12));

        // ===== Header strip =====
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.SURFACE_HI);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1,1,0,1, Theme.BORDER_STRONG),
                new EmptyBorder(8,12,8,12)
        ));

        JLabel title = new JLabel("TaskForge 1.2.0");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        title.setForeground(Theme.ACCENT);
        header.add(title, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        JLabel weekLbl = new JLabel("Week start:");
        weekLbl.setForeground(Theme.TEXT);
        right.add(weekLbl);

        // Placeholder field
        weekField = new HintTextField("YYYY-MM-DD", 10);
        if (plan.getWeekStart()!=null) weekField.setText(plan.getWeekStart().format(DateTimeFormatter.ISO_DATE));
        right.add(weekField);

        JButton help = new JButton("Help");
        help.setToolTipText("Quick how-to");
        help.addActionListener(e -> showHelp());
        right.add(help);
        header.add(right, BorderLayout.EAST);

        // ===== Toolbar =====
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.setOpaque(false);

        JButton addBtn    = iconButton(" Add",    "/add.png",    e -> { addTask(); updateEmptyState(); });  addBtn.setToolTipText("Add a new task");
        JButton editBtn   = iconButton(" Edit",   "/edit.png",   e -> editSelected());                       editBtn.setToolTipText("Edit selected task");
        JButton delBtn    = iconButton(" Delete", "/del.png",    e -> { deleteSelected(); updateEmptyState(); }); delBtn.setToolTipText("Delete selected task");
        JButton saveBtn   = iconButton(" Save",   "/save.png",   e -> savePlan());                           saveBtn.setToolTipText("Save to ~/TaskForge/data.json");
        JButton exportBtn = iconButton(" Export", "/export.png", e -> exportMarkdown());                     exportBtn.setToolTipText("Export Markdown to ~/TaskForge/plans/");
        JButton previewBtn= iconButton(" Preview","/export.png", e -> previewLatest());                      previewBtn.setToolTipText("Preview latest exported Markdown in-app");
        JButton openBtn   = iconButton(" Plans",  "/open.png",   e -> openPlansFolder());                    openBtn.setToolTipText("Open the plans folder");

        Dimension btnSize = new Dimension(110, 32); // prevent truncation
        for (JButton b : new JButton[]{addBtn,editBtn,delBtn,saveBtn,exportBtn,previewBtn,openBtn}) {
            b.setPreferredSize(btnSize);
            b.setMinimumSize(btnSize);
            b.setMaximumSize(btnSize);
        }

        toolbar.add(addBtn); toolbar.add(editBtn); toolbar.add(delBtn);
        toolbar.addSeparator(); toolbar.add(saveBtn); toolbar.add(exportBtn); toolbar.add(previewBtn); toolbar.add(openBtn);

        JPanel toolbarBar = new JPanel(new BorderLayout());
        toolbarBar.setBackground(Theme.SURFACE);
        toolbarBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0,1,1,1, Theme.BORDER_STRONG),
                new EmptyBorder(6,12,6,12)
        ));
        toolbarBar.add(toolbar, BorderLayout.WEST);

        // ===== Center: table in card with overlay hint (real overlay, not replacement) =====
        model = new TaskTableModel(plan.getTasks());
        table = new JTable(model);
        Theme.styleTable(table);
        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(Theme.BG_ALT);
        scroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1,1,1,1, Theme.BORDER_STRONG),
                new EmptyBorder(6,6,6,6)
        ));

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Theme.BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1,1,1,1, Theme.BORDER),
                new EmptyBorder(6,6,6,6)
        ));
        card.add(scroll, BorderLayout.CENTER);

        emptyHint = new JLabel("Click Add to create your first task. Set your week start above.", SwingConstants.CENTER);
        emptyHint.setForeground(Theme.MUTED);
        emptyHint.setOpaque(false);
        emptyHint.setEnabled(false);
        emptyHint.setFocusable(false);
        emptyHint.setAlignmentX(0.5f);
        emptyHint.setAlignmentY(0.5f);

        // Proper overlay using JLayeredPane + OverlayLayout
        JLayeredPane layered = new JLayeredPane();
        layered.setLayout(new OverlayLayout(layered));
        layered.add(emptyHint, Integer.valueOf(1));
        layered.add(card, Integer.valueOf(0));

        // Click background clears selection/focus
        MouseAdapter clearFocus = new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                table.clearSelection();
                getRootPane().requestFocusInWindow();
            }
        };
        layered.addMouseListener(clearFocus);
        card.addMouseListener(clearFocus);
        scroll.getViewport().addMouseListener(clearFocus);
        emptyHint.addMouseListener(clearFocus);

        // ESC also clears selection
        table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
             .put(KeyStroke.getKeyStroke("ESCAPE"), "clearSel");
        table.getActionMap().put("clearSel", new AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) { table.clearSelection(); }
        });

        updateEmptyState();

        // ===== Status bar =====
        status = new JLabel(" Ready. Add tasks → Save → Export → Preview.");
        status.setForeground(Theme.MUTED);
        status.setBorder(new EmptyBorder(6,8,6,8));

        JPanel statusWrap = new JPanel(new BorderLayout());
        statusWrap.setBackground(Theme.SURFACE);
        statusWrap.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1,1,1,1, Theme.BORDER_STRONG),
                new EmptyBorder(0,0,0,0)
        ));
        statusWrap.add(status, BorderLayout.WEST);

        // Compose
        JPanel north = new JPanel(new BorderLayout());
        north.setBackground(Theme.FRAME);
        north.add(header, BorderLayout.NORTH);
        north.add(toolbarBar, BorderLayout.CENTER);

        root.add(north, BorderLayout.NORTH);
        root.add(layered, BorderLayout.CENTER);
        root.add(statusWrap, BorderLayout.SOUTH);
        setContentPane(root);
        pack();
    }

    private void updateEmptyState(){
        boolean show = (table.getModel().getRowCount() == 0);
        emptyHint.setVisible(show);
    }

    private void showHelp(){
        String msg = "<html><body style='width:440px'>"
                + "<b>How to use TaskForge</b><br><br>"
                + "1) Set <i>Week start</i> (YYYY-MM-DD).<br>"
                + "2) <b>Add</b> tasks (Title, Priority, optional Due/Hours/Tags).<br>"
                + "3) <b>Save</b> to store in <code>~/TaskForge/data.json</code>.<br>"
                + "4) <b>Export</b> to create Markdown in <code>~/TaskForge/plans/</code>.<br>"
                + "5) <b>Preview</b> to view the Markdown inside the app.<br><br>"
                + "Tip: Use the <b>Plans</b> button to open the folder of past exports."
                + "</body></html>";
        JOptionPane.showMessageDialog(this, msg, "Quick Help", JOptionPane.INFORMATION_MESSAGE);
    }

    private JButton iconButton(String text, String res, java.awt.event.ActionListener fn){
        JButton b = new JButton(text);
        b.setIcon(new ImageIcon(loadIcon(res).getScaledInstance(18,18,Image.SCALE_SMOOTH)));
        b.addActionListener(fn);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setBorder(new EmptyBorder(8,12,8,12));
        return b;
    }

    private Image loadIcon(String path){
        try {
            return new ImageIcon(TaskForgeApp.class.getResource(path)).getImage();
        } catch (Exception e){
            return new BufferedImage(32,32,BufferedImage.TYPE_INT_ARGB);
        }
    }

    private void addTask(){
        Task t = TaskDialog.show(this, null);
        if(t != null){
            model.add(t);
            status("Added: " + t.getTitle());
        }
    }

    private void editSelected(){
        int row = table.getSelectedRow();
        if(row < 0){ warn("Select a task to edit."); return; }
        Task t = model.get(row);
        Task edited = TaskDialog.show(this, t);
        if(edited != null){
            model.set(row, edited);
            status("Updated: " + edited.getTitle());
        }
    }

    private void deleteSelected(){
        int row = table.getSelectedRow();
        if(row < 0){ warn("Select a task to delete."); return; }
        Task t = model.get(row);
        int ok = JOptionPane.showConfirmDialog(this, "Delete \""+t.getTitle()+"\"?", "Confirm", JOptionPane.OK_CANCEL_OPTION);
        if(ok == JOptionPane.OK_OPTION){
            model.remove(row);
            status("Deleted.");
        }
    }

    private void savePlan(){
        try {
            LocalDate d = LocalDate.parse(weekField.getText().trim());
            plan.setWeekStart(d);
        } catch(Exception e){
            warn("Week date must be YYYY-MM-DD");
            return;
        }
        plan.setTasks(model.items);
        try {
            Store.save(plan);
            status("Saved ✓  →  " + Store.dataFile());
        } catch(Exception e){
            warn("Save failed: " + e.getMessage());
        }
    }

    private void exportMarkdown(){
        try {
            savePlan();
            File f = MarkdownExport.export(plan);
            status("Exported → " + f.getAbsolutePath());
            int choice = JOptionPane.showConfirmDialog(this, "Open now in the app preview?", "Preview", JOptionPane.YES_NO_OPTION);
            if(choice == JOptionPane.YES_OPTION){
                MarkdownPreview.show(this, f);
            }
        } catch(Exception e){
            warn("Export failed: " + e.getMessage());
        }
    }

    private void previewLatest(){
        try {
            java.nio.file.Path dir = Store.plansDir();
            java.util.Optional<java.nio.file.Path> newest = java.nio.file.Files.list(dir)
                    .filter(p -> p.getFileName().toString().toLowerCase().endsWith(".md"))
                    .max(java.util.Comparator.comparingLong(p -> p.toFile().lastModified()));
            if (!newest.isPresent()) { warn("No exported Markdown found. Use Export first."); return; }
            MarkdownPreview.show(this, newest.get().toFile());
        } catch (Exception e) {
            warn("Preview failed: " + e.getMessage());
        }
    }

    private void openPlansFolder(){
        try {
            Desktop.getDesktop().open(Store.plansDir().toFile());
        } catch(Exception e){
            warn("Cannot open folder: " + e.getMessage());
        }
    }

    private void warn(String msg){
        JOptionPane.showMessageDialog(this, msg, "TaskForge", JOptionPane.WARNING_MESSAGE);
        status.setText(" " + msg);
    }
    private void status(String msg){
        status.setText(" " + msg);
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> {
            TaskForgeApp app = new TaskForgeApp();
            app.setVisible(true);
        });
    }

    // ===== Table model =====
    static class TaskTableModel extends AbstractTableModel {
        String[] cols = {"Title","Priority","Due","Hours","Tags"};
        List<Task> items;
        TaskTableModel(List<Task> t){ items = new ArrayList<>(t); }
        public int getRowCount(){ return items.size(); }
        public int getColumnCount(){ return cols.length; }
        public String getColumnName(int c){ return cols[c]; }
        public Object getValueAt(int r,int c){
            Task t = items.get(r);
            switch(c){
                case 0: return t.getTitle();
                case 1: return t.getPriority()==1? "High" : t.getPriority()==2? "Medium" : "Low";
                case 2: return t.dueString();
                case 3: return t.getHours()==null? "" : t.getHours();
                case 4: return t.getTags();
            }
            return "";
        }
        public Task get(int r){ return items.get(r); }
        public void add(Task t){ items.add(t); fireTableDataChanged(); }
        public void set(int r, Task t){ items.set(r,t); fireTableRowsUpdated(r,r); }
        public void remove(int r){ items.remove(r); fireTableRowsDeleted(r,r); }
    }
}
