package survfate.jobinfosearch.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.DefaultCaret;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;

import net.miginfocom.swing.MigLayout;
import survfate.jobinfosearch.JSONFileFilter;
import survfate.jobinfosearch.JTable2XLS;
import survfate.jobinfosearch.XLSFileFilter;
import survfate.jobinfosearch.obj.Job;
import survfate.jobinfosearch.obj.JobSite;
import survfate.jobinfosearch.obj.jobsite.CareerLinkVn;
import survfate.jobinfosearch.obj.jobsite.InternEduVn;
import survfate.jobinfosearch.obj.jobsite.ItViec;
import survfate.jobinfosearch.obj.jobsite.VNWorks;

public class MainPanel extends JPanel implements ActionListener {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private static String VERSION = "v0.0.1";

	private JPanel panel, controlPanel, siteSelect;
	private JTextField textFieldSearchQuery, textFieldFilterResult;
	private JButton btnSearch, btnFilterResult, btnClearFilter, btnExcel, btnJson;
	private JCheckBox checkBoxVNWorks, checkBoxInternEduVn, checkBoxItViec, checkBoxCareerLinkVn;
	private JComboBox<String> comboBoxLocation;

	private JTable table;
	private DefaultTableModel tableModel;
	private static TableRowSorter<DefaultTableModel> rowSorter;
	private TableColumnAdjuster adjuster;

	public static JTextArea logOutput;

	private List<Job> totalListJob;

	public MainPanel() {
		setLayout(new BorderLayout());
		tableModel = new DefaultTableModel(new String[] { "Nguồn dữ liệu", "Tên công việc", "Yêu cầu", "Ngày hết hạn",
				"Ngày đăng tin", "Địa điểm", "Nơi tuyển dụng", "Mức lương", "Đường dẫn chi tiết", "Mô tả" }, 0) {
			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Class<?> getColumnClass(int column) {
				switch (column) {
				case 3:
				case 4:
					return Date.class;
				case 8:
					return URL.class;
				default:
					return Object.class;
				}
			}

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
				// This causes all cells to be not editable
			}
		};

		table = new JTable(tableModel) {
			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean getScrollableTracksViewportWidth() {
				return getPreferredSize().width < getParent().getWidth();
			}
		};

		rowSorter = new TableRowSorter<DefaultTableModel>(tableModel);
		table.setRowSorter(rowSorter);

		table.setPreferredScrollableViewportSize(new Dimension(700, 300));
		table.setFillsViewportHeight(true);
		table.setAutoCreateRowSorter(false);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setRowHeight(25);

		adjuster = new TableColumnAdjuster(table);
		adjuster.adjustColumns();

		// Set Renderers to Columns
		table.getColumnModel().getColumn(0).setCellRenderer(new NameRenderer());
		table.getColumnModel().getColumn(1).setCellRenderer(new NameRenderer());
		table.getColumnModel().getColumn(2).setCellRenderer(new NameRenderer());
		table.getColumnModel().getColumn(5).setCellRenderer(new NameRenderer());
		table.getColumnModel().getColumn(6).setCellRenderer(new NameRenderer());
		table.getColumnModel().getColumn(7).setCellRenderer(new NameRenderer());
		table.getColumnModel().getColumn(9).setCellRenderer(new NameRenderer());

		table.getColumnModel().getColumn(3).setCellRenderer(new DateRenderer());
		table.getColumnModel().getColumn(4).setCellRenderer(new DateRenderer());

		URLRenderer urlRenderer = new URLRenderer();
		table.getColumnModel().getColumn(8).setCellRenderer(urlRenderer);
		table.addMouseListener(urlRenderer);
		table.addMouseMotionListener(urlRenderer);

		add(new JScrollPane(table), BorderLayout.CENTER);

		panel = new JPanel();
		panel.setLayout(new BorderLayout(5, 5));
		panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		add(panel, BorderLayout.NORTH);

		controlPanel = new JPanel();
		controlPanel.setLayout(new MigLayout("center", "[fill]15[fill]10[fill]20", ""));
		panel.add(controlPanel, BorderLayout.CENTER);

		controlPanel.add(new JLabel("Từ khóa tìm kiếm:"));
		textFieldSearchQuery = new JTextField(20);
		controlPanel.add(textFieldSearchQuery);
		btnSearch = new JButton("Tìm kiếm");
		btnSearch.addActionListener(this);
		controlPanel.add(btnSearch, "spany 2, growy, wrap");

		controlPanel.add(new JLabel("Địa điểm:"));
		comboBoxLocation = new JComboBox<String>(new String[] { "Tất cả", "Hồ Chí Minh", "Hà Nội", "Đà Nẵng" });
		comboBoxLocation.addActionListener(this);
		controlPanel.add(comboBoxLocation, "gaptop 6, wrap");

		controlPanel.add(new JSeparator(), "gaptop 9, gapbottom 5, span 3, wrap");

		controlPanel.add(new JLabel("Lọc kết quả:"));
		textFieldFilterResult = new JTextField(20);
		controlPanel.add(textFieldFilterResult, "gapbottom 6");
		btnFilterResult = new JButton("Lọc");
		btnFilterResult.addActionListener(this);
		controlPanel.add(btnFilterResult, "split 2");
		btnClearFilter = new JButton("Hủy");
		btnClearFilter.addActionListener(this);
		controlPanel.add(btnClearFilter, "wrap");

		controlPanel.add(new JLabel("Lưu kết quả thành:"));
		btnExcel = new JButton("File .XLS");
		btnExcel.addActionListener(this);
		controlPanel.add(btnExcel, "growx, span 2, split 3");
		btnJson = new JButton("File .JSON");
		btnJson.addActionListener(this);
		controlPanel.add(btnJson, "growx");

		siteSelect = new JPanel();
		siteSelect.setLayout(new MigLayout("center, gapy -2", "20[][center]", ""));
		controlPanel.add(siteSelect, "dock east");
		controlPanel.add(new JSeparator(SwingConstants.VERTICAL), "dock east");

		siteSelect.add(new JLabel("Chọn trang nguồn:"), "span 2, center, gapbottom 10, wrap");

		siteSelect.add(new JLabel("VietnamWorks.com"));
		checkBoxVNWorks = new JCheckBox();
		checkBoxVNWorks.addActionListener(this);
		siteSelect.add(checkBoxVNWorks, "wrap");

		siteSelect.add(new JLabel("Internship.edu.vn"));
		checkBoxInternEduVn = new JCheckBox();
		checkBoxInternEduVn.addActionListener(this);
		siteSelect.add(checkBoxInternEduVn, "wrap");

		siteSelect.add(new JLabel("ITviec.com"));
		checkBoxItViec = new JCheckBox();
		checkBoxItViec.addActionListener(this);
		siteSelect.add(checkBoxItViec, "wrap");

		siteSelect.add(new JLabel("CareerLink.vn"));
		checkBoxCareerLinkVn = new JCheckBox();
		checkBoxCareerLinkVn.addActionListener(this);
		siteSelect.add(checkBoxCareerLinkVn, "wrap");

		checkBoxCareerLinkVn.setSelected(true);
		checkBoxInternEduVn.setSelected(true);
		checkBoxItViec.setSelected(true);
		checkBoxVNWorks.setSelected(true);

		logOutput = new JTextArea(5, 40);
		DefaultCaret ouputCaret = (DefaultCaret) logOutput.getCaret();
		ouputCaret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		logOutput.setEditable(false);
		add(new JScrollPane(logOutput), BorderLayout.SOUTH);
	}

	public static void createAndShowGUI() {
		try {
			org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
			// UIManager.setLookAndFeel(org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.getBeautyEyeLNFCrossPlatform());

			UIManager.put("RootPane.setupButtonVisible", false);

			// UIManager.getDefaults().put("TextArea.font",
			// UIManager.getFont("TextField.font"));

			Font robotoFont = loadFont();

			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(robotoFont.deriveFont(13F));

			setUIFont(new FontUIResource(robotoFont.deriveFont(13F)));

		} catch (Exception e) {
			// If not available, you can set the GUI to another look
			// and feel.
		}
		// UIManager.put("swing.boldMetal", Boolean.FALSE);
		JFrame frame = new JFrame("JobInfoSearch " + VERSION);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		MainPanel newContentPane = new MainPanel();
		newContentPane.setOpaque(true);
		frame.setContentPane(newContentPane);

		frame.pack();
		frame.setVisible(true);
	}

	public static void setUIFont(javax.swing.plaf.FontUIResource f) {
		@SuppressWarnings("rawtypes")
		java.util.Enumeration keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value != null && value instanceof javax.swing.plaf.FontUIResource)
				UIManager.put(key, f);
		}
	}

	public static Font loadFont() throws FontFormatException, IOException {
		// Load Roboto Font
		URL robotoFontURL = new URL(
				"https://raw.githubusercontent.com/google/fonts/master/apache/roboto/Roboto-Regular.ttf");
		return Font.createFont(Font.TRUETYPE_FONT, robotoFontURL.openStream());
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == btnSearch) {
			for (int i = tableModel.getRowCount() - 1; i >= 0; i--) {
				tableModel.removeRow(i);
			}
			tableModel.fireTableDataChanged();

			if (!textFieldSearchQuery.getText().trim().equals("")) {
				if ((checkBoxCareerLinkVn.isSelected() == false) && (checkBoxInternEduVn.isSelected() == false)
						&& (checkBoxItViec.isSelected() == false) && (checkBoxVNWorks.isSelected() == false))
					JOptionPane.showMessageDialog(null, "Vui lòng chọn ít nhất một ô và thử lại.", "Lỗi", 0);
				else {
					LoadJobsWorker jWorker = new LoadJobsWorker();
					jWorker.execute();
				}
			} else
				JOptionPane.showMessageDialog(null, "Vui lòng nhập từ khóa tìm kiếm vào ô và thử lại.", "Lỗi", 0);
		}

		if (event.getSource() == btnFilterResult) {
			if (textFieldFilterResult.getText().trim().equals(""))
				JOptionPane.showMessageDialog(null, "Vui lòng nhập từ khóa lọc vào ô và thử lại.", "Lỗi", 0);
			else {
				rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + textFieldFilterResult.getText().trim()));
				logOutput.append(
						"Trạng thái: Đã lọc kết quả với từ khóa [" + textFieldFilterResult.getText().trim() + "]\n");
			}
		}

		if (event.getSource() == btnClearFilter) {
			textFieldFilterResult.setText("");
			rowSorter.setRowFilter(null);
			logOutput.append("Trạng thái: Đã hủy kết quả lọc\n");
		}

		if (event.getSource() == btnExcel) {
			if (table.getRowCount() > 0) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setSelectedFile(new File("C:\\JobInfoSearch_Result.xls"));
				fileChooser.setAcceptAllFileFilterUsed(false);
				fileChooser.addChoosableFileFilter(new XLSFileFilter());
				int option = fileChooser.showSaveDialog(this);
				if (option == JFileChooser.APPROVE_OPTION) {
					JTable2XLS.saveXLS(table, fileChooser.getSelectedFile());
				}
			} else
				JOptionPane.showMessageDialog(null, "Dữ liệu hoặc dữ liệu đã lọc trong bảng là rỗng.", "Lỗi", 0);

		}

		if (event.getSource() == btnJson) {
			if (table.getRowCount() > 0) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setSelectedFile(new File("C:\\JobInfoSearch_Result.json"));
				fileChooser.setAcceptAllFileFilterUsed(false);
				fileChooser.addChoosableFileFilter(new JSONFileFilter());
				int option = fileChooser.showSaveDialog(this);
				if (option == JFileChooser.APPROVE_OPTION) {
					Gson gson = new Gson();
					try {
						gson.toJson(totalListJob, new FileWriter(fileChooser.getSelectedFile()));
						JOptionPane.showMessageDialog(null, "Lưu thành công.", "Thông báo", 0);
					} catch (JsonIOException | IOException e) {
						e.printStackTrace();
						JOptionPane.showMessageDialog(null,
								"Không lưu thành công. Hãy đảm bảo bạn có quyền truy cập vào đường dẫn và thử lại.",
								"Lỗi", 0);
					}

				}
			} else
				JOptionPane.showMessageDialog(null, "Dữ liệu hoặc dữ liệu đã lọc trong bảng là rỗng.", "Lỗi", 0);
		}
	}

	public class LoadJobsWorker extends SwingWorker<Void, Void> {

		@Override
		protected Void doInBackground() throws IOException, ParseException {
			enableControls(false);
			logOutput.append("Trạng thái: Bắt đầu tìm kiếm với từ khóa [" + textFieldSearchQuery.getText().trim()
					+ "] và địa điểm [" + comboBoxLocation.getSelectedItem().toString() + "]...\n");

			totalListJob = new ArrayList<Job>();

			if (checkBoxVNWorks.isSelected()) {
				JobSite jVNWorks = new VNWorks(textFieldSearchQuery.getText().trim(),
						comboBoxLocation.getSelectedItem().toString());
				addJobs2Table(jVNWorks.getListJob(), tableModel);
				totalListJob.addAll(jVNWorks.getListJob());
				logOutput.append("Trạng thái: Đã thêm " + jVNWorks.getTotalJob() + " thông tin tuyển dụng từ "
						+ jVNWorks.getSiteName() + " vào danh sách\n");
			}

			if (checkBoxInternEduVn.isSelected()) {
				JobSite jInternEduVn = new InternEduVn(textFieldSearchQuery.getText().trim(),
						comboBoxLocation.getSelectedItem().toString());
				addJobs2Table(jInternEduVn.getListJob(), tableModel);
				totalListJob.addAll(jInternEduVn.getListJob());
				logOutput.append("Trạng thái: Đã thêm " + jInternEduVn.getTotalJob() + " thông tin tuyển dụng từ "
						+ jInternEduVn.getSiteName() + " vào danh sách\n");
			}

			if (checkBoxItViec.isSelected()) {
				JobSite jItViec = new ItViec(textFieldSearchQuery.getText().trim(),
						comboBoxLocation.getSelectedItem().toString());
				addJobs2Table(jItViec.getListJob(), tableModel);
				totalListJob.addAll(jItViec.getListJob());
				logOutput.append("Trạng thái: Đã thêm " + jItViec.getTotalJob() + " thông tin tuyển dụng từ "
						+ jItViec.getSiteName() + " vào danh sách\n");
			}

			if (checkBoxCareerLinkVn.isSelected()) {
				JobSite jCareerLinkVn = new CareerLinkVn(textFieldSearchQuery.getText().trim(),
						comboBoxLocation.getSelectedItem().toString());
				addJobs2Table(jCareerLinkVn.getListJob(), tableModel);
				totalListJob.addAll(jCareerLinkVn.getListJob());
				logOutput.append("Trạng thái: Đã thêm " + jCareerLinkVn.getTotalJob() + " thông tin tuyển dụng từ "
						+ jCareerLinkVn.getSiteName() + " vào danh sách\n");
			}
			enableControls(true);
			return null;
		}

		@Override
		protected void done() {
			try {
				get();
			} catch (ExecutionException e) {
				e.getCause().printStackTrace();
				// if(e.getCause().getClass().getSimpleName().equals("NullPointerException")
			} catch (InterruptedException e) {
				e.getCause().printStackTrace();
			}
		}
	}

	public static void addJobs2Table(List<Job> jobList, DefaultTableModel tableModel) {
		for (Job j : jobList) {
			tableModel.addRow(new Object[] { j.getJobSiteName(), j.getJobName(), j.getRequirements(), j.getExpDate(),
					j.getPubDate(), j.getLocation(), j.getCompanyName(), j.getSalaries(), j.getOriginalURL(),
					j.getDescription() });
			tableModel.fireTableDataChanged();
		}
	}

	public void enableControls(boolean bool) {
		textFieldSearchQuery.setEnabled(bool);
		textFieldFilterResult.setEnabled(bool);
		btnSearch.setEnabled(bool);
		btnFilterResult.setEnabled(bool);
		btnClearFilter.setEnabled(bool);
		btnExcel.setEnabled(bool);
		btnJson.setEnabled(bool);
		checkBoxVNWorks.setEnabled(bool);
		checkBoxInternEduVn.setEnabled(bool);
		checkBoxItViec.setEnabled(bool);
		checkBoxCareerLinkVn.setEnabled(bool);
		comboBoxLocation.setEnabled(bool);
	}
}
