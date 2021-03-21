import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Consumer {
    private static String pathStr;

    private static void processQueue(String filename) throws Exception {
        Path source = Paths.get(pathStr + "/" + filename);
        Files.lines(source).map(data -> {
            String[] list = data.split(",");
            Map<String, String> hashMap = new HashMap<>();
            hashMap.put("id", filename);
            hashMap.put("name", list[0]);
            hashMap.put("age", list[1]);
            hashMap.put("color", list[2]);
            return hashMap;
        }).forEach(System.out::println);
        Files.delete(source);
    }

    private static void createDirs() throws IOException {
        if (!Files.isDirectory(Paths.get("queue"))) {
            Files.createDirectory(Paths.get("queue"));
        }

        Path path = Paths.get(pathStr);
        if (!Files.isDirectory(path)) {
            Files.createDirectory(path);
        }
    }

    private static void processExisting() throws Exception {
        try (Stream<Path> walk = Files.walk(Paths.get(pathStr))) {
            List<String> result = walk.filter(Files::isRegularFile).map(x -> x.toString().split("/")[2])
                    .collect(Collectors.toList());

            for (String filename : result) {
                processQueue(filename);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        pathStr = "queue/" + args[0];
        createDirs();
        processExisting();

        WatchService watchService = FileSystems.getDefault().newWatchService();
        Path path = Paths.get(pathStr);
        path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

        WatchKey key;
        while ((key = watchService.take()) != null) {
            for (WatchEvent<?> event : key.pollEvents()) {
                processQueue(event.context().toString());
            }
            key.reset();
        }
    }
}
