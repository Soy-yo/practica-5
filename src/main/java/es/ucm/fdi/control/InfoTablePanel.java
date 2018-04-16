package es.ucm.fdi.control;

import javax.swing.*;
import java.awt.*;

public class InfoTablePanel extends JScrollPane {

  private JTable table;
  private Object[] titles;
  private Object[][] values;

  InfoTablePanel(Dimension size, Object[] titles) {
    super();
    this.titles = titles;
    values = new Object[1][titles.length];
    initialize(size);
  }

  private void initialize(Dimension size) {
    table = new JTable(values, titles);
    setPreferredSize(size);
    add(table);
  }

}
