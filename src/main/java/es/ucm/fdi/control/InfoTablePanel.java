package es.ucm.fdi.control;

import es.ucm.fdi.model.Describable;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class InfoTablePanel<T extends Describable> extends JScrollPane {

  private JTable table;
  private String[] titles;
  private String[][] values;

  InfoTablePanel(String panelTitle, Dimension minimumSize, String[] titles) {
    super();
    this.titles = titles;
    values = new String[0][titles.length];
    initialize(minimumSize, panelTitle);
  }

  private void initialize(Dimension size, String title) {
    setBorder(new TitledBorder(new LineBorder(Color.BLACK), title));
    setMinimumSize(size);
    refreshTable();
  }

  public void setElements(List<T> elements) {
    values = new String[elements.size()][titles.length];
    for (int i = 0; i < elements.size(); i++) {
      updateValues(elements.get(i).describe(), i);
    }
    refreshTable();
  }

  private void refreshTable() {
    table = new JTable(values, titles);
    table.setShowGrid(false);
    setViewportView(table);
  }

  private void updateValues(Map<String, String> newValues, int row) {
    for (int j = 0; j < titles.length; j++) {
      values[row][j] = newValues.get(titles[j].toLowerCase());
    }
  }

}
