package io.medness.simple2pc.job.application.port.out;

import io.medness.simple2pc.job.domain.Job;

import java.io.Serializable;

public interface PersistJob {

    <T extends Serializable> Job<T> save(Job<T> job);
}
