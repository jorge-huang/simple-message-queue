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

public class Queue {
    private static Map<String, String> configMap;
    private static int counter = -1;

    private static void assignQueue(String filename) throws Exception {
        int totalConsumers = Integer.parseInt(configMap.get("totalConsumers"));
        int queueNum = ++counter % totalConsumers;
        Path source = Paths.get("unprocessed/" + filename);
        Path target = Paths.get("queue/" + queueNum + "/" + filename);
        Files.move(source, target);
        System.out.println(filename + " has been moved to queue #" + queueNum);
    }

    private static void processExisting() throws Exception {
        try (Stream<Path> walk = Files.walk(Paths.get("unprocessed"))) {

            List<String> result = walk.filter(Files::isRegularFile).map(x -> x.toString().split("/")[1])
                    .collect(Collectors.toList());

            for (String filename : result) {
                assignQueue(filename);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadConfig() throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("config"));
        configMap = new HashMap<>();
        for (String line : lines) {
            String[] config = line.split("=");
            configMap.put(config[0], config[1]);
        }
    }

    private static void createDirs() throws Exception {
        if (!Files.isDirectory(Paths.get("queue"))) {
            Files.createDirectory(Paths.get("queue"));
        }

        if (!Files.isDirectory(Paths.get("unprocessed"))) {
            Files.createDirectory(Paths.get("unprocessed"));
        }

        int totalConsumers = Integer.parseInt(configMap.get("totalConsumers"));
        for (int i = 0; i < totalConsumers; i++) {
            Path path = Paths.get("queue/" + i);
            if (!Files.isDirectory(path)) {
                Files.createDirectory(path);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        loadConfig();
        createDirs();
        processExisting();

        WatchService watchService = FileSystems.getDefault().newWatchService();
        Path path = Paths.get("unprocessed");
        path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

        WatchKey key;
        while ((key = watchService.take()) != null) {
            for (WatchEvent<?> event : key.pollEvents()) {
                assignQueue(event.context().toString());
            }
            key.reset();
        }
    }
}
