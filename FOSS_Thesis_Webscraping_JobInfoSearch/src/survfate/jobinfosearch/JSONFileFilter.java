package survfate.jobinfosearch;

import java.io.File;

public class JSONFileFilter extends javax.swing.filechooser.FileFilter {
	@Override
	public boolean accept(File file) {
		if (file.isDirectory())
			return true;
		String filename = file.getName();
		return filename.toUpperCase().endsWith(".JSON");
	}

	@Override
	public String getDescription() {
		return "JavaScript Object Notation (*.json)";
	}

}
