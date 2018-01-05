package 

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tools.ant.DirectoryScanner;
import org.codehaus.jettison.json.JSONException;

import com.kobie.tr1.util.Settings;

public class SpecsReader {
	public static Sheet getSheet(String ticketNumber)
			throws IOException, JSONException, EncryptedDocumentException, InvalidFormatException {
		DirectoryScanner scanner = new DirectoryScanner();
		scanner.setIncludes(new String[] { "*Specs*" });
		scanner.setBasedir(Settings.get("main.file.directory") + ticketNumber + "/");
		scanner.setCaseSensitive(false);
		scanner.scan();
		String[] files = scanner.getIncludedFiles();

		FileInputStream inputStream = new FileInputStream(
				new File(Settings.get("main.file.directory") + ticketNumber + "/" + files[0].toString()));
		Workbook workbook = null;
		
		workbook = WorkbookFactory.create(inputStream);	
		
		Sheet sheet = workbook.getSheetAt(0);
		
		workbook.close();
		inputStream.close();
		return sheet;
	}
	
	public static void readAmcSpecs(Sheet sheet) {
		int headerRow = 4;
		int headerColumn = 0;
		Grid columnGrid = new Grid().createColumnGrid(sheet, headerColumn, headerRow);
		
	}
	
}
