package 

import java.util.ArrayList;
import java.util.List;

public class ExcelRow {
	private List<String> data = new ArrayList<String>();
	
	public ExcelRow() {
	}

	public ExcelRow(List<String> data) {
		this.data = data;
	}

	public void addColumn(String columnData) {
		data.add(columnData);
	}

	public List<String> getData() {
		return data;
	}

}
