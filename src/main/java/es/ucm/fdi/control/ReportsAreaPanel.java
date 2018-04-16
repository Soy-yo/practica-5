package es.ucm.fdi.control;

import javax.swing.*;
import java.awt.*;

public class ReportsAreaPanel extends JScrollPane {

  private JTextArea area;

  ReportsAreaPanel(Dimension size) {
    super();
    initilaize(size);
  }

  private void initilaize(Dimension size) {
    area = new JTextArea();
    area.setEditable(false);
    setPreferredSize(size);
    add(area);
  }

}
