package es.ucm.fdi.control;

import javax.swing.*;
import java.awt.*;

public class EventsEditorPanel extends JScrollPane {

  private JTextArea editor;

  EventsEditorPanel(Dimension size) {
    super(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
    initialize(size);
  }

  private void initialize(Dimension size) {
    editor = new JTextArea();
    editor.setEditable(true);
    setPreferredSize(size);
    add(editor);
  }

}
