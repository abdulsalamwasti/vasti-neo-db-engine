package com.example.neoEngine;

import com.example.neoEngine.processor.ConstraintProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NeoDataEngine {
    public static void main(String[] args) {
        //SpringApplication.run(NeoDataEngine.class, args);

        ConstraintProcessor processor = new ConstraintProcessor(
                "schema/schema.yml",
                "output/constraints",
                true
        );
        processor.process();
    }
}
