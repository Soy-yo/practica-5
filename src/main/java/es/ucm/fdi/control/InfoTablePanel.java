package es.ucm.fdi.control;

import es.ucm.fdi.model.Describable;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class InfoTablePanel<T extends Describable> extends JScrollPane {

  private JTable table;
  private String[] titles;

  InfoTablePanel(String panelTitle, Dimension minimumSize, String[] titles) {
    super();
    this.titles = titles;
    initialize(minimumSize, panelTitle);
  }

  private void initialize(Dimension size, String title) {
    table = new JTable(new DefaultTableModel(titles, 0));
    table.setShowGrid(false);
    table.setEnabled(false);
    setBorder(new TitledBorder(new LineBorder(Color.BLACK), title));
    setMinimumSize(size);
    setViewportView(table);
  }

  public void setElements(List<T> elements) {
    DefaultTableModel model = (DefaultTableModel) table.getModel();
    // FIXME: por qué esta mierda es sincronizada? :_(
    reset(model);
    for (T element : elements) {
      model.addRow(generateRow(element, model.getRowCount()));
    }
  }

  private void reset(DefaultTableModel model) {
    model.setRowCount(0);
  }

  private String[] generateRow(T element, int row) {
    Map<String, String> values = element.describe();
    String[] result = new String[titles.length];
    for (int i = 0; i < result.length; i++) {
      result[i] = "#".equals(titles[i]) ? "" + row : values.get(titles[i]);
    }
    return result;
  }

}
