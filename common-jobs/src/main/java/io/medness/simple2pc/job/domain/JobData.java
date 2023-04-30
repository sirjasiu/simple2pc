package io.medness.simple2pc.job.domain;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

public class JobData<T> {

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@dataClass")
    private T data;

    protected JobData() {
    }

    public JobData(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }
}
