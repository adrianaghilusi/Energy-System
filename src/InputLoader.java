import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public final class InputLoader {

    private final String inputPath;

    public InputLoader(final String inputPath) {
        this.inputPath = inputPath;
    }

    /**
     * Citirea datelor din fisierul de input
     */
    public Input readData() {
        ObjectMapper objectMapper = new ObjectMapper();
        File inputFile = new File(inputPath);
        Input inputData = new Input();
        try {
            inputData = objectMapper.readValue(inputFile, Input.class);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return inputData;
    }

}
