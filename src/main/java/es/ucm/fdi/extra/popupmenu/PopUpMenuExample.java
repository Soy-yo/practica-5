package es.ucm.fdi.extra.popupmenu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class PopUpMenuExample extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

  private JPanel mainPanel;
  private JTextArea editor;

	public PopUpMenuExample() {
		super("Pop Up Menu Example");
		initGUI();
	}

	private void initGUI() {
    mainPanel = new JPanel(new BorderLayout());
    this.setContentPane(mainPanel);

		addEditor();

    this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.pack();
		this.setVisible(true);
	}

	private void addEditor() {
    mainPanel.add(new JLabel("Right click over the text-area to get the popup menu."), BorderLayout.PAGE_START);

    editor = new JTextArea(20, 20);

    mainPanel.add(new JScrollPane(editor, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);

		// create the events pop-up menu
    JPopupMenu editorPopupMenu = new JPopupMenu();
		
		JMenuItem clearOption = new JMenuItem("Clear");
    clearOption.addActionListener(e -> editor.setText(""));

		JMenuItem exitOption = new JMenuItem("Exit");
    exitOption.addActionListener(e -> System.exit(0));

		JMenu subMenu = new JMenu("Insert");

		String[] greetings = { "Hola!", "Hello!", "Ciao!" };
		for (String s : greetings) {
			JMenuItem menuItem = new JMenuItem(s);
      menuItem.addActionListener(e -> editor.insert(s, editor.getCaretPosition()));
			subMenu.add(menuItem);
		}

    editorPopupMenu.add(subMenu);
    editorPopupMenu.addSeparator();
    editorPopupMenu.add(clearOption);
    editorPopupMenu.add(exitOption);

		// connect the popup menu to the text area _editor
    editor.addMouseListener(new MouseListener() {

			@Override
			public void mousePressed(MouseEvent e) {
				showPopup(e);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				showPopup(e);
			}

			private void showPopup(MouseEvent e) {
        if (e.isPopupTrigger() && editorPopupMenu.isEnabled()) {
          editorPopupMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
			}

		});

	}
	
	public static void main(String[] args) {
    SwingUtilities.invokeLater(PopUpMenuExample::new);
	}

}
