package 

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class ExcelWriter {

	public static void writeQueryToExcel(ArrayList<String> columnNames, ResultSet rs, String ticketNumber, String fileType) {
		try {
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("sheet1");
			HSSFRow rowhead = sheet.createRow((short) 0);
			int col = 0;
			for (String column : columnNames) {
				rowhead.createCell((short) col).setCellValue(column);
				col++;
			}
			int i = 1;
			while (rs.next()) {
				HSSFRow row = sheet.createRow((short) i);
				int index = 0;
				for (String column : columnNames) {
					row.createCell((short) index).setCellValue(rs.getString(columnNames.get(index)));
					index++;
				}
				i++;
			}
			Path path = Paths.get(Settings.get("main.file.directory") + "/" + ticketNumber);
			String directory = Settings.get("main.file.directory") + "/" + ticketNumber
					+ "/" + fileType + ticketNumber.replaceAll("[^0-9]", "") + ".xls";
			
			if (!Files.exists(path)) {
				try {
					Files.createDirectories(path);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			FileOutputStream fileOut = new FileOutputStream(directory);
			workbook.write(fileOut);
			fileOut.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
