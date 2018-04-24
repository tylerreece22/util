package 

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Data
public class ExcelFileRequest implements ReportingFileRequest {
    @NotNull
    private List<List<String>> excelRows;

    @NotNull
    private String fileName;

    public ExcelFileRequest(List<? extends ReportingObject> reportingObjects, @NotNull String fileName) {
        this.excelRows = createExcelMap(reportingObjects);
        this.fileName = fileName;
    }

    public List<List<String>> createExcelMap(List<? extends ReportingObject> objects) {
        int headerRow = 0;
        int bodyRowStart = 1;
        Class aClass = objects.get(0).getClass();

        List<List<String>> excelMap = new LinkedList<>();

        for (Object object : objects) {
            excelMap.add(new LinkedList<>());
        }

        excelMap.add(new LinkedList<>()); // One extra for header row
        for (Field field : aClass.getDeclaredFields()) {
            excelMap.get(headerRow).add(field.getName());
        }

        AtomicInteger index = new AtomicInteger(bodyRowStart);
        objects.stream().forEach(rule -> {
            for (Field field : aClass.getDeclaredFields()) {
                String fieldValue = null;
                String methodName = "get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
                try {
                    fieldValue = (String) aClass.getMethod(methodName).invoke(rule);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                excelMap.get(index.get()).add(fieldValue);
            }
            index.getAndIncrement();
        });

        return excelMap;
    }
}
