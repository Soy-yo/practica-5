package es.ucm.fdi.control;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;

public class ReportsAreaPanel extends JScrollPane {

  private JTextArea area;

  ReportsAreaPanel(Dimension minimumSize) {
    super();
    initialize(minimumSize);
  }

  public void clear() {
	  area.setText("");
  }
  
  public void saveToFile(File file) throws IOException {
	  Files.write(file.toPath(), area.getText().getBytes("UTF-8"));
  }
  
  private void initialize(Dimension size) {
    area = new JTextArea();
    //area.setEditable(false);
    setBorder(new TitledBorder(new LineBorder(Color.BLACK), "Reports"));
    setMinimumSize(size);
    setViewportView(area);
  }

}
