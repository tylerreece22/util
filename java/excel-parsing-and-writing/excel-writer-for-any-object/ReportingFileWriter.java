package 

public interface ReportingFileWriter<F, R extends ReportingFileRequest> {
    F write(R request);
    F writeBlankFile(String qualifiedName);
}
