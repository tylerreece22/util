package 

import java.util.ArrayList;
import java.util.List;

public class ExcelColumn {
	private List<String> data = new ArrayList<String>();
	
	public ExcelColumn() {
	}

	public ExcelColumn(List<String> data) {
		this.data = data;
	}

	public void addRow(String rowData) {
		data.add(rowData);
	}

	public List<String> getData() {
		return data;
	}
}
