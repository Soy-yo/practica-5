package es.ucm.fdi.extra.dialog;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class DialogWindowExample extends JFrame  {

  private DialogWindow dialog;

  List<String> names;
  List<Integer> ages;
	
	public DialogWindowExample() {
		super("Dialog Example");
		initGUI();
	}

	private void initGUI() {

		// initialize lists;
    names = new ArrayList<>();
    ages = new ArrayList<>();
		for(int i=0;i<10; i++) {
      names.add("Item " + i);
      ages.add(i + 30);
		}
		
		JPanel mainPanel = new JPanel();
		mainPanel.add(new JLabel("If you click "));

    dialog = new DialogWindow(this);
    dialog.setData(names, ages);
		
		JButton here = new JButton("here");
    here.addActionListener(e -> {
      int status = dialog.open();
      if (status == 0) {
        System.out.println("Canceled");
      } else {
        System.out.println("The following items where selected:");
        for (String s : dialog.getSelectedItems()) {
          System.out.println(s);
        }
        System.out.println("The following numbers where selected:");
        for (Integer s : dialog.getSelectedNumbers()) {
          System.out.println(s);
        }
      }
    });
		mainPanel.add(here);
		mainPanel.add(new JLabel("a dialog window is opened and the main window blocks."));

		this.setContentPane(mainPanel);
    this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.pack();
		this.setVisible(true);

	}

	public static void main(String[] args) {
    SwingUtilities.invokeLater(DialogWindowExample::new);
	}
}