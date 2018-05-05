package es.ucm.fdi.control;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class SimulatorTextArea extends JTextArea {

  SimulatorTextArea(boolean editable) {
    super();
    setEnabled(editable);
  }

  public void writeFromFile(File file) throws IOException {
    setText(new String(Files.readAllBytes(file.toPath()), "UTF-8"));
  }
  
  public void saveToFile(File file) throws IOException {
    Files.write(file.toPath(), getText().getBytes("UTF-8"));
  }

  public void clear() {
    setText("");
  }

}
