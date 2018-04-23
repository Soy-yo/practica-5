package es.ucm.fdi.control;

import es.ucm.fdi.events.Event;
import es.ucm.fdi.events.EventBuilder;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class EventsEditorPanel extends JScrollPane {

  private JTextArea editor;
  private TitledBorder border;

  EventsEditorPanel(Dimension minimumSize, File file) {
    super();
    initialize(minimumSize, file);
  }

  private void initialize(Dimension size, File file) {
    String text = null;
    if (file != null) {
      try {
        text = new String(Files.readAllBytes(file.toPath()), "UTF-8");
      } catch (IOException e) {
        // TODO: hacer algo con esto
      }
    }
    editor = new JTextArea(text);
    border = new TitledBorder(new LineBorder(Color.BLACK), file == null ? "Events editor" :
        makeTitle(file));
    setBorder(border);
    setMinimumSize(size);
    editor.setComponentPopupMenu(new EventsEditorPopupMenu());
    setViewportView(editor);
  }

  public void writeFromFile(File file) throws IOException {
    // FIXME: no funciona
    border.setTitle(makeTitle(file));
    String text = new String(Files.readAllBytes(file.toPath()), "UTF-8");
    editor.setText(text);
  }

  private String makeTitle(File file) {
    return "Events: " + file.getName();
  }
  
  public void saveToFile(File file) throws IOException {
	  Files.write(file.toPath(), editor.getText().getBytes("UTF-8"));
  }

  public void clear() {
    editor.setText("");
  }

  public String getText() {
    return editor.getText();
  }

  @SuppressWarnings("serial")
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
