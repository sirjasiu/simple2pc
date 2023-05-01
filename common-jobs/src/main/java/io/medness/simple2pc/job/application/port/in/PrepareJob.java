package io.medness.simple2pc.job.application.port.in;

import io.medness.simple2pc.job.domain.Job;

import java.io.Serializable;

public interface PrepareJob {

    <T extends Serializable> Job<T> prepare(T jobData);
}
