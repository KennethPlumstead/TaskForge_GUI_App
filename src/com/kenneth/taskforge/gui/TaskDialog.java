
package com.kenneth.taskforge.gui;

import com.kenneth.taskforge.model.Task;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;

public class TaskDialog extends JDialog {

    private JTextField titleField;
    private JComboBox<String> prio;
    private JTextField dueField;
    private JTextField hoursField;
    private JTextField tagsField;
    private Task result;

    public TaskDialog(Window owner, Task existing){
        super(owner, "Task", ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(420, 300);
        setLocationRelativeTo(owner);

        JPanel p = new JPanel(new BorderLayout(0,8));
        p.setBorder(new EmptyBorder(12,12,12,12));

        JPanel form = new JPanel(new GridLayout(0,2,8,8));
        form.setOpaque(false);
        form.add(new JLabel("Title:"));
        titleField = new JTextField();
        form.add(titleField);

        form.add(new JLabel("Priority:"));
        prio = new JComboBox<>(new String[]{"High","Medium","Low"});
        form.add(prio);

        form.add(new JLabel("Due (YYYY-MM-DD):"));
        dueField = new JTextField();
        form.add(dueField);

        form.add(new JLabel("Hours (optional):"));
        hoursField = new JTextField();
        form.add(hoursField);

        form.add(new JLabel("Tags:"));
        tagsField = new JTextField();
        form.add(tagsField);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancel = new JButton("Cancel");
        JButton ok = new JButton("Save Task");
        buttons.add(cancel);
        buttons.add(ok);

        p.add(form, BorderLayout.CENTER);
        p.add(buttons, BorderLayout.SOUTH);
        setContentPane(p);

        cancel.addActionListener(e -> { result = null; dispose(); });
        ok.addActionListener(e -> onSave());

        if(existing != null){
            titleField.setText(existing.getTitle());
            prio.setSelectedIndex(existing.getPriority()-1);
            if(existing.getDue()!=null) dueField.setText(existing.getDue().toString());
            if(existing.getHours()!=null) hoursField.setText(existing.getHours().toString());
            tagsField.setText(existing.getTags());
        }
    }

    private void onSave(){
        String title = titleField.getText().trim();
        if(title.isEmpty()){
            JOptionPane.showMessageDialog(this, "Title is required", "TaskForge", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int priority = prio.getSelectedIndex()+1;
        java.time.LocalDate due = null;
        String ds = dueField.getText().trim();
        if(!ds.isEmpty()){
            try { due = LocalDate.parse(ds); } catch(Exception e){
                JOptionPane.showMessageDialog(this, "Invalid due date (use YYYY-MM-DD)", "TaskForge", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        Double hours = null;
        String hs = hoursField.getText().trim();
        if(!hs.isEmpty()){
            try { hours = Double.parseDouble(hs); } catch(Exception e){
                JOptionPane.showMessageDialog(this, "Hours must be a number", "TaskForge", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        String tags = tagsField.getText().trim();
        result = new Task(title, due, priority, hours, tags);
        dispose();
    }

    public static Task show(Window owner, Task existing){
        TaskDialog d = new TaskDialog(owner, existing);
        d.setVisible(true);
        return d.result;
    }
}
