package survfate.jobinfosearch.ui;

import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

public class DateRenderer extends DefaultTableCellRenderer {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	SimpleDateFormat dateFormat;

	public DateRenderer() {
		dateFormat = new SimpleDateFormat("dd/MM", Locale.ENGLISH);
		setHorizontalAlignment(SwingConstants.CENTER);
	}

	@Override
	public void setValue(Object value) {
		setText(value != null ? dateFormat.format(value) : "");
	}
}
