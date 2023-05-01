package io.medness.simple2pc.job.adapter.out;

import java.io.Serializable;

public interface JobHandler<T extends Serializable> {

    boolean canHandle(Object data);

    void prepare(T data);

    void commit(T data);

    void abort(T data);
}
