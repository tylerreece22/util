package 

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class Grid {

	private ExcelRow headerColumns;
	private List<ExcelRow> dataRows;
	private ExcelColumn headerRows;
	private List<ExcelColumn> dataColumns;

	public Grid() {
		dataRows = new ArrayList<ExcelRow>();
		dataColumns = new ArrayList<ExcelColumn>();
	}

	@SuppressWarnings("deprecation")
	public Grid createColumnGrid(Sheet sheet, int headerColumn, int firstRow) {
		for (int columnNumber = headerColumn; columnNumber < sheet.getRow(firstRow).getLastCellNum(); columnNumber++) {
			ExcelColumn myColumn = new ExcelColumn();
			for (int rowNumber = firstRow; rowNumber < sheet.getLastRowNum(); rowNumber++) {
				Row row = sheet.getRow(rowNumber);
				if (row == null)
					continue;

				if (row.getCell(columnNumber) == null)
					continue;

				if (row.getCell(columnNumber).getCellTypeEnum() == CellType.STRING) {
					if (row.getCell(columnNumber).getStringCellValue() == null)
						continue;

					myColumn.addRow(row.getCell(columnNumber).getStringCellValue());
				} else if (row.getCell(columnNumber).getCellTypeEnum() == CellType.NUMERIC) {
					if (row.getCell(columnNumber).getDateCellValue().toString() == null)
						continue;

					myColumn.addRow(row.getCell(columnNumber).getDateCellValue().toString());
				} else if (row.getCell(columnNumber).getCellTypeEnum() == CellType.BLANK) {
					continue;
				}
			}
			if (myColumn.getData().isEmpty())
				continue;
			
			if (columnNumber == headerColumn) {
				this.addHeaderColumn(myColumn.getData());
				continue;
			}
			this.addDataColumn(myColumn.getData());
		}
		return this;
	}

	@SuppressWarnings("deprecation")
	public Grid createRowGrid(Sheet sheet, int headerRow) {
		for (int rowNumber = headerRow; rowNumber < sheet.getLastRowNum(); rowNumber++) {
			ExcelRow myRow = new ExcelRow();
			Row row = sheet.getRow(rowNumber);
			Date beforeDate = new Date(99,01,01);

			if (row == null)
				continue;

			for (int cellNumber = 0; cellNumber < row.getLastCellNum(); cellNumber++) {
				Cell cell = row.getCell(cellNumber);

				if (cell == null)
					continue;

				if (cell.getCellTypeEnum() == CellType.STRING) {
					if (cell.getStringCellValue() == null)
						continue;

					myRow.addColumn(cell.getStringCellValue());
				} else if (cell.getCellTypeEnum() == CellType.NUMERIC) {
					if (cell.getDateCellValue().toString() == null)
						continue;
					
					if (cell.getDateCellValue().before(beforeDate)) {
						myRow.addColumn(Double.toString(cell.getNumericCellValue()));
					} else {
						myRow.addColumn(cell.getDateCellValue().toString());
					}

				} else if (cell.getCellTypeEnum() == CellType.BLANK) {
					continue;
				}
			}
			if (rowNumber == headerRow) {
				this.addHeaderRow(myRow.getData());
				continue;
			}

			this.addDataRow(myRow.getData());
		}
		return this;
	}

	public void addHeaderRow(List<String> headers) {
		this.headerColumns = new ExcelRow(headers);
	}

	public void addHeaderColumn(List<String> headers) {
		this.headerRows = new ExcelColumn(headers);
	}

	public void addDataRow(List<String> data) {
		if (data.isEmpty())
			return;

		this.dataRows.add(new ExcelRow(data));
	}
	
	public void addDataColumn(List<String> data) {
		if (data.isEmpty())
			return;

		this.dataColumns.add(new ExcelColumn(data));
	}

	public List<ExcelRow> getAllDataRows() {
		List<ExcelRow> data = new ArrayList<ExcelRow>(1 + dataRows.size());
		data.add(this.headerColumns);
		data.addAll(dataRows);
		return data;
	}
	
	public List<ExcelColumn> getAllDataColumns() {
		List<ExcelColumn> data = new ArrayList<ExcelColumn>(1 + dataColumns.size());
		data.add(this.headerRows);
		data.addAll(dataColumns);
		return data;
	}

	public ExcelRow getHeaderColumns() {
		return headerColumns;
	}

	public ExcelColumn getHeaderRows() {
		return headerRows;
	}

	public List<ExcelRow> getDataRows() {
		return dataRows;
	}
	
	public List<ExcelColumn> getDataColumns() {
		return dataColumns;
	}
}
