import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public final class OutputLoader {

    private final String outputPath;
    private Output outPutToWrite;

    public void setOutPutToWrite(final Output outPutToWrite) {
        this.outPutToWrite = outPutToWrite;
    }

    public OutputLoader(final String outputPath) {
        this.outputPath = outputPath;
    }

    public String getOutputPath() {
        return outputPath;
    }

    /**
     * Scrierea datelor in fisierul de output
     */
    public void writeData() {
        ObjectMapper objectMapper = new ObjectMapper();
        File outputFile = new File(outputPath);
        try {
            objectMapper.writeValue(outputFile, outPutToWrite);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}
