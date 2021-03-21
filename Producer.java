import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Producer {
    private static void writeFile(Path path, String content) throws Exception {
        Files.writeString(path, content);
    }

    private static void createDirs() throws IOException {
        if (!Files.isDirectory(Paths.get("unprocessed"))) {
            Files.createDirectory(Paths.get("unprocessed"));
        }
    }

    public static void main(String[] args) throws Exception {
        createDirs();
        List<List<String>> content = new ArrayList<>(3);
        content.add(Arrays.asList("Michael", "30", "blue"));
        content.add(Arrays.asList("John", "35", "red"));
        content.add(Arrays.asList("Margaret", "40", "white"));

        int index = 0;
        while (true) {
            String fileName = String.valueOf(System.currentTimeMillis());
            Path path = FileSystems.getDefault().getPath("unprocessed", fileName);
            List<String> data = content.get(index);
            writeFile(path, String.join(",", data));
            Thread.sleep(args.length > 0 ? Long.parseLong(args[0]) : 2500);
            index = ++index % content.size();
        }
    }
}