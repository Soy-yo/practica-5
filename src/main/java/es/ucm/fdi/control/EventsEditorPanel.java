package es.ucm.fdi.control;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class EventsEditorPanel extends JScrollPane {

  private JTextArea editor;
  private TitledBorder border;

  EventsEditorPanel(Dimension minimumSize) {
    super();
    initialize(minimumSize);
  }

  private void initialize(Dimension size) {
    editor = new JTextArea();
    // TODO reemplazar título cuando se abra un archivo (Events: [nombre de archivo])
    border = new TitledBorder(new LineBorder(Color.BLACK), "Events editor");
    setBorder(border);
    setMinimumSize(size);
    setViewportView(editor);
  }

  public void setTitle(String title) {
    border.setTitle(title);
  }

}
