package chrysostom.view.components;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class EditableList extends JTable
{
	private static final int defaultRowHeight = 20;
	private final DefaultTableModel tableModel = new DefaultTableModel();

	public EditableList() {
		initTableModel();
		initTable();
		addEmptyRow();
	}

	private static boolean isFromSpaces(String string) {
		for (char c : string.toCharArray()) {
			if (c != ' ') return false;
		}
		return true;
	}

	private void initTableModel() {
		tableModel.setColumnCount(1);
		tableModel.addTableModelListener(e -> {
			removeEmptyRows();
			if (getRowCount() == 0) {
				addEmptyRow();
			}
			Object lastValue = getValueAt(getRowCount() - 1, 0);
			if (lastValue != null) {
				addEmptyRow();
			}
		});
	}

	private void initTable() {
		setModel(tableModel);
		setTableHeader(null);
		setRowHeight(defaultRowHeight);
		setSelectionBackground(Color.LIGHT_GRAY);
		setSelectionForeground(Color.BLACK);
		setShowGrid(false);
		setFillsViewportHeight(true);
		addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_DELETE) {
					removeSelectedElements();
				}
			}
		});
	}

	private void removeEmptyRows() {
		int row = 0;
		while (row < getRowCount() - 1) {
			Object value = getValueAt(row, 0);
			if (value == null || value.equals("") || isFromSpaces(value.toString())) {
				tableModel.removeRow(row);
				continue;
			}
			row++;
		}
	}

	public void removeAllElements() {
		removeEditor();
		tableModel.setRowCount(0);
	}

	public void removeSelectedElements() {
		removeEditor();
		int row = 0;
		while (row < getRowCount()) {
			if (isRowSelected(row)) {
				tableModel.removeRow(row);
				continue;
			}
			row++;
		}
	}

	public String[] getElements() {
		String[] elements = new String[getRowCount() - 1];
		for (int row = 0; row < getRowCount() - 1; row++) {
			elements[row] = getValueAt(row, 0).toString();
		}
		return elements;
	}

	public void addAllElements(Object[] elements) {
		for (Object el : elements) {
			addElement(el);
		}
	}

	public void addElement(Object element) {
		addEmptyRow();
		setValueAt(element, getRowCount() - 1, 0);
	}

	public void addEmptyRow() {
		tableModel.setRowCount(getRowCount() + 1);
	}
}
