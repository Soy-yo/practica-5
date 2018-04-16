package es.ucm.fdi.control;

import javax.swing.*;
import java.awt.*;

public class ReportsAreaPanel extends JScrollPane {

  private JTextArea area;

  ReportsAreaPanel(Dimension minimumSize) {
    super();
    initialize(minimumSize);
  }

  private void initialize(Dimension size) {
    area = new JTextArea();
    area.setEditable(false);
    setMinimumSize(size);
    setViewportView(area);
  }

}
