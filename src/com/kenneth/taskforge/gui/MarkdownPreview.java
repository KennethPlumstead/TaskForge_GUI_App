package com.kenneth.taskforge.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.nio.charset.StandardCharsets;

public final class MarkdownPreview {
    public static void show(Component parent, File mdFile) {
        String md = read(mdFile);
        String html = toHtml(md);
        JDialog d = new JDialog(SwingUtilities.getWindowAncestor(parent), "Preview — " + mdFile.getName(), Dialog.ModalityType.MODELESS);
        JEditorPane pane = new JEditorPane("text/html", html);
        pane.setEditable(false);
        pane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);

        JScrollPane scroll = new JScrollPane(pane);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG);
        root.setBorder(new EmptyBorder(8,8,8,8));
        root.add(scroll, BorderLayout.CENTER);

        d.setContentPane(root);
        d.setSize(820, 600);
        d.setLocationRelativeTo(parent);
        d.setVisible(true);
    }

    private static String read(File f) {
        try {
            byte[] b = java.nio.file.Files.readAllBytes(f.toPath());
            return new String(b, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "# Error\n\nCould not read file: " + e.getMessage();
        }
    }

    // Minimal MD → HTML tuned for this app’s output (headers, tables, hr, paragraphs, blockquotes)
    private static String toHtml(String md) {
        String[] lines = md.split("\\r?\\n");
        StringBuilder body = new StringBuilder();
        boolean inTable = false;

        StringBuilder head = new StringBuilder();
        head.append("<style>")
            .append("body{background:#181a1f;color:#e8e8ec;font:14px -apple-system,BlinkMacSystemFont,Segoe UI,Roboto,Ubuntu,Inter,sans-serif;margin:0;padding:16px;}")
            .append("h1{color:#00beff;font-size:20px;margin:0 0 12px;font-weight:700}")
            .append("h2{color:#e8e8ec;font-size:16px;margin:18px 0 8px;font-weight:700}")
            .append("table{border-collapse:collapse;width:100%;}")
            .append("th,td{border:1px solid #383c44;padding:8px;text-align:left;}")
            .append("th{background:#24272e;font-weight:600}")
            .append("tr:nth-child(even) td{background:#1f2228}")
            .append("hr{border:0;border-top:1px solid #383c44;margin:16px 0}")
            .append("blockquote{opacity:.9;border-left:3px solid #3c5fa0;padding:6px 10px;margin:8px 0;background:#1f2228}")
            .append("</style>");

        for (String raw : lines) {
            String line = raw.trim();
            if (line.startsWith("# ")) { body.append("<h1>").append(esc(line.substring(2))).append("</h1>"); continue; }
            if (line.startsWith("## ")) { body.append("<h2>").append(esc(line.substring(3))).append("</h2>"); continue; }
            if (line.startsWith("> ")) { body.append("<blockquote>").append(esc(line.substring(2))).append("</blockquote>"); continue; }
            if (line.startsWith("|")) {
                if (!inTable) { body.append("<table><thead>"); inTable = true; }
                if (line.matches("\\|[-\\s|]+\\|")) {
                    body.append("</thead><tbody>");
                } else {
                    String[] cells = line.substring(1, line.length()-1).split("\\|");
                    if (body.toString().endsWith("<thead>")) {
                        body.append("<tr>");
                        for (String c : cells) body.append("<th>").append(esc(c.trim())).append("</th>");
                        body.append("</tr>");
                    } else {
                        body.append("<tr>");
                        for (String c : cells) body.append("<td>").append(esc(c.trim())).append("</td>");
                        body.append("</tr>");
                    }
                }
                continue;
            }
            if (inTable) { body.append("</tbody></table>"); inTable = false; }
            if (line.equals("---") || line.equals("***")) { body.append("<hr/>"); continue; }
            if (line.isEmpty()) { body.append("<div style='height:8px'></div>"); continue; }
            body.append("<p>").append(esc(line)).append("</p>");
        }
        if (inTable) body.append("</tbody></table>");

        // Put CSS in <head> so Swing renders it as styles (not text)
        return "<html><head>" + head + "</head><body>" + body + "</body></html>";
    }

    private static String esc(String s){
        return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;");
    }
}
