package test_utils.data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class BaseMapData {
    private final String path;
    private final String map;

    public BaseMapData(String path) throws IOException {
        this.path = path;
        map = Files.readString(Path.of(path));
    }

    public String path() {
        return path;
    }

    public String map() {
        return map;
    }
}
