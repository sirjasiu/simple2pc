package io.medness.simple2pc;

import io.medness.simple2pc.infrastructure.JobConfiguration;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Import;

@SpringBootConfiguration
@EnableAutoConfiguration
@Import(JobConfiguration.class)
public class TestConfig {
}
