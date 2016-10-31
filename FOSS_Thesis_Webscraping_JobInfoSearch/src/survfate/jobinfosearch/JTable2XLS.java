package survfate.jobinfosearch;

import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class JTable2XLS {
	private static boolean success = true;

	public static void toXLS(JTable table, File file) {
		File xlsFile = file;
		WritableWorkbook writableWorkbook;
		try {
			writableWorkbook = Workbook.createWorkbook(xlsFile);
			WritableSheet writableSheet = writableWorkbook.createSheet("Job List", 0);

			for (int i = 0; i < table.getColumnCount(); i++) {
				for (int j = 0; j < table.getRowCount(); j++) {
					Object object = table.getValueAt(j, i);
					writableSheet.addCell(new Label(i, j, String.valueOf(object)));
				}
			}
			writableWorkbook.write();
			writableWorkbook.close();
			success = true;
		} catch (WriteException | IOException e) {
			success = false;
			e.printStackTrace();
		}
	}

	public static void saveXLS(JTable table, File file) {
		SwingWorker<Void, Void> saveWorker = new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				toXLS(table, file);
				return null;
			}

			@Override
			public void done() {
				if (success == true)
					JOptionPane.showMessageDialog(null, "Lưu thành công.", "Thông báo", 0);
				else
					JOptionPane.showMessageDialog(null,
							"Không lưu thành công. Hãy đảm bảo bạn có quyền truy cập vào đường dẫn và thử lại.", "Lỗi",
							0);
			}
		};
		saveWorker.execute();
	}
}
