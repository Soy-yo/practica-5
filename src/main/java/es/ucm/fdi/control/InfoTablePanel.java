package es.ucm.fdi.control;

import es.ucm.fdi.model.Describable;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InfoTablePanel<T extends Describable> extends JScrollPane {

  private JTable table;
  private InfoTableModel model;
  private String[] titles;

  InfoTablePanel(String panelTitle, Dimension minimumSize, String[] titles) {
    super();
    this.titles = titles;
    initialize(minimumSize, panelTitle);
  }

  private void initialize(Dimension size, String title) {
    model = new InfoTableModel(titles, 0);
    table = new JTable(model);
    table.setShowGrid(false);
    table.setEnabled(false);
    centerCells();
    setBorder(new TitledBorder(new LineBorder(Color.BLACK), title));
    setMinimumSize(size);
    setViewportView(table);
  }

  // https://stackoverflow.com/questions/7433602/how-to-center-in-jtable-cell-a-value
  private void centerCells() {
    DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
    centerRenderer.setHorizontalAlignment(JLabel.CENTER);
    table.setDefaultRenderer(String.class, centerRenderer);
  }

  public void clear() {
    model.clear();
  }

  public void setElement(T element, int row) {
    model.setRow(element, row);
  }

  public void setElements(List<T> elements) {
    clear();
    for (T element : elements) {
      addElement(element);
    }
  }

  private void addElement(T element) {
    model.addRow(element);
  }

  // TODO: implementación mejorable?
  private class InfoTableModel extends AbstractTableModel {

    private final String[] titles;
    private List<String[]> elements;

    InfoTableModel(String[] titles, int rowCount) {
      this.titles = titles;
      elements = new ArrayList<>(rowCount);
    }

    @Override
    public String getColumnName(int column) {
      checkColumnBounds(column);
      return titles[column];
    }

    @Override
    public int getRowCount() {
      return elements.size();
    }

    @Override
    public int getColumnCount() {
      return titles.length;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
      throw new UnsupportedOperationException("Cannot set value for a single cell");
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
      checkRowBounds(rowIndex);
      checkColumnBounds(columnIndex);
      return elements.get(rowIndex)[columnIndex];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
      return String.class;
    }

    private String[] describe(T element) {
      Map<String, String> values = element.describe();
      String[] result = new String[titles.length];
      for (int i = 0; i < titles.length; i++) {
        result[i] = "#".equals(titles[i]) ? "" + elements.size() : values.get(titles[i]);
      }
      return result;
    }

    void addRow(T element) {
      addRow(describe(element));
    }

    void addRow(String[] newRow) {
      checkColumnCount(newRow);
      elements.add(newRow);
      fireTableRowsInserted(elements.size() - 1, elements.size() - 1);
    }

    void setRow(T element, int rowIndex) {
      setRow(describe(element), rowIndex);
    }

    void setRow(String[] newRow, int rowIndex) {
      checkRowBounds(rowIndex);
      checkColumnCount(newRow);
      elements.set(rowIndex, newRow);
      fireTableRowsUpdated(elements.size() - 1, elements.size() - 1);
    }

    void clear() {
      int lastRow = elements.size() - 1;
      elements = new ArrayList<>(0);
      fireTableRowsDeleted(0, Math.max(0, lastRow));
    }

    private void checkColumnCount(Object[] row) {
      if (row.length != titles.length) {
        throw new IllegalArgumentException("Number of titles: " + titles.length +
            " and number of columns " + row.length + " must match");
      }
    }

    private void checkRowBounds(int row) {
      if (row < 0 || row >= elements.size()) {
        throw new IllegalArgumentException("Row " + row + " does not exist");
      }
    }

    private void checkColumnBounds(int column) {
      if (column < 0 || column >= titles.length) {
        throw new IllegalArgumentException("Column " + column + " does not exist");
      }
    }

  }

}
