package survfate.jobinfosearch.ui;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.ToolTipManager;
import javax.swing.table.DefaultTableCellRenderer;

public class NameRenderer extends DefaultTableCellRenderer {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		// ToolTip configure
		ToolTipManager.sharedInstance().registerComponent(this);
		ToolTipManager.sharedInstance().setInitialDelay(0);
		ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);

		// setIcon(...);
		setToolTipText(value != null ? String.valueOf(value) : null);
		return this;
	}

}