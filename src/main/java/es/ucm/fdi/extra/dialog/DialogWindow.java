package es.ucm.fdi.extra.dialog;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

class DialogWindow extends JDialog {

	private static final long serialVersionUID = 1L;

  private MyListModel<String> itemsListModel;
  private MyListModel<Integer> numbersListModel;

  private int status;
  private JList<String> itemsList;
  private JList<Integer> numbersList;

  static final private char CLEAR_SELECTION_KEY = 'c';
  private Border defaultBorder = BorderFactory.createLineBorder(Color.BLACK, 2);

	public DialogWindow(Frame parent) {
		super(parent, true);
		initGUI();
	}

	private void initGUI() {

    status = 0;

		setTitle("Some Dialog");
		JPanel mainPanel = new JPanel(new BorderLayout());

		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.X_AXIS));
		mainPanel.add(contentPanel, BorderLayout.CENTER);

		JPanel itemsPanel = new JPanel(new BorderLayout());
		JPanel numsPanel = new JPanel(new BorderLayout());

		contentPanel.add(itemsPanel);
		contentPanel.add(numsPanel);

		itemsPanel.setBorder(
        BorderFactory.createTitledBorder(defaultBorder, "Items", TitledBorder.LEFT, TitledBorder.TOP));
		numsPanel.setBorder(
        BorderFactory.createTitledBorder(defaultBorder, "Numbers", TitledBorder.LEFT, TitledBorder.TOP));

		itemsPanel.setMinimumSize(new Dimension(100, 100));
		numsPanel.setMinimumSize(new Dimension(100, 100));

    itemsListModel = new MyListModel<>();
    numbersListModel = new MyListModel<>();

    itemsList = new JList<>(itemsListModel);
    numbersList = new JList<>(numbersListModel);

    addCleanSelectionListener(itemsList);
    addCleanSelectionListener(numbersList);

    itemsPanel.add(new JScrollPane(itemsList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);

    numsPanel.add(new JScrollPane(numbersList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);


		JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		mainPanel.add(buttonsPanel, BorderLayout.PAGE_END);

		JButton cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(e -> {
      status = 0;
      DialogWindow.this.setVisible(false);
    });
		buttonsPanel.add(cancelButton);

		JButton okButton = new JButton("OK");
    okButton.addActionListener(e -> {
      status = 1;
      DialogWindow.this.setVisible(false);
    });
		buttonsPanel.add(okButton);

		mainPanel.add(buttonsPanel, BorderLayout.PAGE_END);

		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
		mainPanel.add(infoPanel, BorderLayout.PAGE_START);

		infoPanel.add(new JLabel("Select items for which you want to process."));
    infoPanel.add(new JLabel("Use '" + CLEAR_SELECTION_KEY + "' to deselect all."));
		infoPanel.add(new JLabel("Use Ctrl+A to select all"));
		infoPanel.add(new JLabel(" "));

		setContentPane(mainPanel);
		setMinimumSize(new Dimension(100, 100));
		setVisible(false);
	}

  private void addCleanSelectionListener(JList<?> list) {
		list.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == CLEAR_SELECTION_KEY) {
					list.clearSelection();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}
		});

	}

	public void setData(List<String> items, List<Integer> nums) {
    itemsListModel.setList(items);
    numbersListModel.setList(nums);
	}

	public String[] getSelectedItems() {
    int[] indices = itemsList.getSelectedIndices();
		String[] items = new String[indices.length];
		for(int i=0; i<items.length; i++) {
      items[i] = itemsListModel.getElementAt(indices[i]);
		}
		return items;
	}

  public Integer[] getSelectedNumbers() {
    int[] indices = numbersList.getSelectedIndices();
    Integer[] numbers = new Integer[indices.length];
    for (int i = 0; i < numbers.length; i++) {
      numbers[i] = numbersListModel.getElementAt(indices[i]);
    }
    return numbers;
	}

	public int open() {
		setLocation(getParent().getLocation().x + 50, getParent().getLocation().y + 50);
		pack();
		setVisible(true);
    return status;
	}

}
