package 

import java.util.List;

public abstract class AbstractFileRequest implements ReportingFileRequest {
    public List<Object> rows = null;
    public String fileName = "";

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
