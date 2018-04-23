package es.ucm.fdi.control;

import es.ucm.fdi.events.Event;
import es.ucm.fdi.events.EventBuilder;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;

public class EventsEditorPanel extends JScrollPane {

  private JTextArea editor;
  private TitledBorder border;

  EventsEditorPanel(Dimension minimumSize, String text) {
    super();
    initialize(minimumSize, text);
  }

  private void initialize(Dimension size, String text) {
    editor = new JTextArea(text);
    border = new TitledBorder(new LineBorder(Color.BLACK), "Events editor");
    setBorder(border);
    setMinimumSize(size);
    editor.setComponentPopupMenu(new EventsEditorPopupMenu());
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

  private class EventsEditorPopupMenu extends JPopupMenu {

    EventsEditorPopupMenu() {
      super();
      initialize();
    }

    private void initialize() {
      setEnabled(true);
      // Aprovecha los Event.Builder
      for (Event.Builder builder : EventBuilder.SUPPORTED_EVENTS) {
        JMenuItem item = new JMenuItem(builder.getEventName());
        item.addActionListener(e ->
            editor.insert("\n" + builder.getEventFileTemplate(), editor.getCaretPosition()));
        add(item);
      }
    }

  }

}
