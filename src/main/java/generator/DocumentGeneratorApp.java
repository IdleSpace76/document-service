package generator;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * @author a.zharov
 */
public class DocumentGeneratorApp {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static void main(String[] args) throws Exception {
        Path configPath = resolveConfigPath(args);
        Properties props = loadProperties(configPath);

        int count = Integer.parseInt(props.getProperty("count", "10"));
        String baseUrl = props.getProperty("baseUrl", "http://localhost:8080");
        String author = props.getProperty("author", "generator");

        System.out.printf("Генерируется %d документов по адресу %s%n", count, baseUrl);

        try (HttpClient client = HttpClient.newHttpClient()) {

            for (int i = 1; i <= count; i++) {
                ObjectNode body = OBJECT_MAPPER.createObjectNode();
                body.put("author", author);
                body.put("title", "Сгенерированный документ #" + i);

                String json = OBJECT_MAPPER.writeValueAsString(body);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + "/api/documents"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.printf("Request #%d: HTTP %d, body=%s%n",
                        i, response.statusCode(), response.body());
            }
        }

        System.out.println("Генерация завершена.");
    }

    private static Path resolveConfigPath(String[] args) {
        if (args.length > 0) {
            return Path.of(args[0]);
        }
        return Path.of("generator", "generator.properties");
    }

    private static Properties loadProperties(Path path) throws IOException {
        Properties props = new Properties();
        try (InputStream in = Files.newInputStream(path)) {
            props.load(in);
        }
        return props;
    }
}
