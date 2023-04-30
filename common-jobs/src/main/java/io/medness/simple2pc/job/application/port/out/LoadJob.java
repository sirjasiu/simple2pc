package io.medness.simple2pc.job.application.port.out;

import io.medness.simple2pc.job.domain.Job;

import java.io.Serializable;
import java.util.Optional;
import java.util.UUID;

public interface LoadJob {

    <T extends Serializable> Optional<Job<T>> findById(UUID jobId);
}
