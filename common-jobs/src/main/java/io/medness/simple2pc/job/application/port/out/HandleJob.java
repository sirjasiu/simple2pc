package io.medness.simple2pc.job.application.port.out;

import io.medness.simple2pc.job.domain.Job;

import java.io.Serializable;

public interface HandleJob {

    <T extends Serializable> void prepare(Job<T> job);

    <T extends Serializable> void commit(Job<T> job);

    <T extends Serializable> void abort(Job<T> job);
}
