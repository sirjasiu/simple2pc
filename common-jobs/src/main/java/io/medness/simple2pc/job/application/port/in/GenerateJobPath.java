package io.medness.simple2pc.job.application.port.in;

import io.medness.simple2pc.job.domain.Job;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.Serializable;
import java.net.URI;

public interface GenerateJobPath {

    <T extends Serializable> URI generate(Job<T> job, UriComponentsBuilder builder);
}
