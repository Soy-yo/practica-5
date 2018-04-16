package es.ucm.fdi.control;

import javax.swing.*;
import java.awt.*;

public class EventsEditorPanel extends JScrollPane {

  private JTextArea editor;

  EventsEditorPanel(Dimension minimumSize) {
    super();
    initialize(minimumSize);
  }

  private void initialize(Dimension size) {
    editor = new JTextArea();
    setMinimumSize(size);
    setViewportView(editor);
  }

}
