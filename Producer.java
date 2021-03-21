import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Producer {
    private static void processCallbacks(boolean existing) {
        try (Stream<Path> walk = Files.walk(Paths.get("processed"))) {
            List<String> files = walk.filter(Files::isRegularFile).map(x -> x.toString().split("/")[1])
                    .collect(Collectors.toList());

            for (String file : files) {
                Files.delete(Paths.get("processed/" + file));
                System.out.println(file + " has been processed.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeFile(Path path, String content) throws Exception {
        Files.writeString(path, content);
    }

    private static void createDirs() throws IOException {
        if (!Files.isDirectory(Paths.get("unprocessed"))) {
            Files.createDirectory(Paths.get("unprocessed"));
        }

        if (!Files.isDirectory(Paths.get("processed"))) {
            Files.createDirectory(Paths.get("processed"));
        }
    }

    public static void main(String[] args) throws Exception {
        createDirs();
        processCallbacks(true);
        List<List<String>> content = new ArrayList<>(3);
        content.add(Arrays.asList("Michael", "30", "blue"));
        content.add(Arrays.asList("John", "35", "red"));
        content.add(Arrays.asList("Margaret", "40", "white"));

        int index = 0;
        while (true) {
            processCallbacks(false);
            String fileName = String.valueOf(System.currentTimeMillis());
            Path path = FileSystems.getDefault().getPath("unprocessed", fileName);
            List<String> data = content.get(index);
            writeFile(path, String.join(",", data));
            Thread.sleep(args.length > 0 ? Long.parseLong(args[0]) : 2500);
            index = ++index % content.size();
        }
    }
}