package 

import lombok.Cleanup;
import lombok.Data;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ExcelFileWriter extends AbstractFileWriter {

    @Override
    public Object write(ReportingFileRequest request) {
        ExcelFileRequest eRequest = (ExcelFileRequest) request;
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(eRequest.getFileName());

        AtomicInteger rowIndex = new AtomicInteger(0);
        for (List<String> rowList : eRequest.getExcelRows()) {
            HSSFRow sheetRow = sheet.createRow((short) rowIndex.get());
            AtomicInteger columnIndex = new AtomicInteger(0);
            for (String cellValue : rowList) {
                sheetRow.createCell(columnIndex.get()).setCellValue(cellValue);
                columnIndex.getAndIncrement();
            }
            rowIndex.getAndIncrement();
        }

        try {
            @Cleanup FileOutputStream fileOut = new FileOutputStream(generatedReportDirectory + eRequest.getFileName());
            workbook.write(fileOut);
        } catch ( Exception ex ) {
            System.out.println(ex);
        }

        return workbook;
    }

    @Override
    public Object writeBlankFile(String qualifiedFileName) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(qualifiedFileName);
        HSSFRow sheetRow0 = sheet.createRow((short) 0);
        sheetRow0.createCell(0).setCellValue("Data was not found. Please check and make sure your search is correct");
        HSSFRow sheetRow1 = sheet.createRow((short) 2);
        sheetRow1.createCell(0).setCellValue("This blank return could be a result of a couple things:");
        HSSFRow sheetRow2 = sheet.createRow((short) 3);
        sheetRow2.createCell(0).setCellValue("1. You have a unimplemented/invalid ticket number");
        HSSFRow sheetRow3 = sheet.createRow((short) 4);
        sheetRow3.createCell(0).setCellValue("2. The campaign names you provided are not in the database or are incorrect");

        try {
            @Cleanup FileOutputStream fileOut = new FileOutputStream(generatedReportDirectory + qualifiedFileName);
            workbook.write(fileOut);
        } catch ( Exception ex ) {
            System.out.println(ex);
        }

        return workbook;
    }

}
