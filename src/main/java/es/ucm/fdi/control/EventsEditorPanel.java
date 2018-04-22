package es.ucm.fdi.control;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;

public class EventsEditorPanel extends JScrollPane {

  private JTextArea editor;
  private TitledBorder border;

  EventsEditorPanel(Dimension minimumSize) {
    super();
    initialize(minimumSize);
  }

  private void initialize(Dimension size) {
    editor = new JTextArea();
    border = new TitledBorder(new LineBorder(Color.BLACK), "Events editor");
    setBorder(border);
    setMinimumSize(size);
    setViewportView(editor);
  }

  public void writeFromFile(File file) throws IOException {
    // FIXME: no funciona
    border.setTitle("Events: " + file.getName());
    String text = new String(Files.readAllBytes(file.toPath()), "UTF-8");
    editor.setText(text);
  }
  
  public void saveToFile(File file) throws IOException {
	  PrintWriter out = new PrintWriter(file);
	  out.print(editor.getText());
	  out.close();
  }

  public void clear() {
    editor.setText("");
  }

  public String getText() {
    return editor.getText();
  }

}
