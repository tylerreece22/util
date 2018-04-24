package 

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

@Data
public abstract class AbstractFileWriter implements ReportingFileWriter {
    @Value("${generated.report.directory}")
    public String generatedReportDirectory;
}
