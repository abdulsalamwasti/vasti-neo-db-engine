package com.example.neoEngine.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class ConstraintProcessor {

    private final String inputFilePath;
    private final String outputBaseDir;
    private final boolean generateJson;

    public ConstraintProcessor(String inputFilePath, String outputBaseDir, boolean generateJson) {
        this.inputFilePath = inputFilePath;
        this.outputBaseDir = outputBaseDir;
        this.generateJson = generateJson;
    }

    public void process() {
        try {
            // Create output directories
            Path jsonDir = createDirectory(outputBaseDir + "/json");
            Path cypherDir = createDirectory(outputBaseDir + "/cypher");

            // Read YAML file
            Map<String, Object> schema = readYamlFile(inputFilePath);

            // Process constraints
            List<Map<String, String>> constraints = extractConstraints(schema);

            // Generate outputs
            if (generateJson) {
                generateJsonOutput(constraints, jsonDir);
            }
            generateCypherOutput(constraints, cypherDir);

            System.out.println("Processing completed successfully");

        } catch (Exception e) {
            System.err.println("Error processing constraints: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Map<String, Object> readYamlFile(String filePath) throws IOException {
        ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
        return yamlMapper.readValue(new File(filePath), Map.class);
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, String>> extractConstraints(Map<String, Object> schema) {
        Map<String, Object> schemaSection = (Map<String, Object>) schema.get("schema");
        return (List<Map<String, String>>) schemaSection.get("constraints");
    }

    private Path createDirectory(String path) throws IOException {
        Path dirPath = Paths.get(path);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
            System.out.println("Created directory: " + dirPath.toAbsolutePath());
        }
        return dirPath;
    }

    private void generateJsonOutput(List<Map<String, String>> constraints, Path outputDir) throws IOException {
        ObjectMapper jsonMapper = new ObjectMapper();
        File outputFile = outputDir.resolve("constraints.json").toFile();

        jsonMapper.writerWithDefaultPrettyPrinter().writeValue(outputFile, Map.of("constraints", constraints));

        System.out.println("JSON constraints generated at: " + outputFile.getAbsolutePath());
    }

    private void generateCypherOutput(List<Map<String, String>> constraints, Path outputDir) throws IOException {
        StringBuilder cypherBuilder = new StringBuilder();
        cypherBuilder.append("-- AUTO-GENERATED CONSTRAINT QUERIES\n\n");

        for (Map<String, String> constraint : constraints) {
            cypherBuilder.append(String.format("CREATE CONSTRAINT IF NOT EXISTS FOR (n:%s) REQUIRE n.%s IS %s;\n", constraint.get("label"), constraint.get("property"), constraint.get("type")));
        }

        File outputFile = outputDir.resolve("constraints.cypher").toFile();
        Files.write(outputFile.toPath(), cypherBuilder.toString().getBytes());

        System.out.println("Cypher queries generated at: " + outputFile.getAbsolutePath());
    }

    public static void main(String[] args) {
        // Configuration - could be moved to properties file
        String inputFile = "schema.yml";
        String outputDir = "generated";
        boolean shouldGenerateJson = true; // Configurable

        new ConstraintProcessor(inputFile, outputDir, shouldGenerateJson).process();
    }
}
