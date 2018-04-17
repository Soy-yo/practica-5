package es.ucm.fdi.control;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class InfoTablePanel extends JScrollPane {

  private JTable table;
  private Object[] titles;
  private Object[][] values;

  InfoTablePanel(String panelTitle, Dimension minimumSize, Object[] titles) {
    super();
    this.titles = titles;
    values = new Object[1][titles.length];
    initialize(minimumSize, panelTitle);
  }

  private void initialize(Dimension size, String title) {
    table = new JTable(values, titles);
    table.setShowGrid(false);
    setBorder(new TitledBorder(new LineBorder(Color.BLACK), title));
    setMinimumSize(size);
    setViewportView(table);
  }

}
