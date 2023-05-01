package io.medness.simple2pc.job.domain;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(schema = "simple2pc")
@TypeDef(name = "json", typeClass = JsonType.class)
public class Job<T extends Serializable> {

    @Id
    private UUID id;

    @Type(type = "json")
    @Column(columnDefinition = "jsonb")
    private JobData<T> data;

    @Enumerated(EnumType.STRING)
    private JobState state;

    protected Job() {
    }

    public Job(T data) {
        this.id = UUID.randomUUID();
        this.state = JobState.PREPARED;
        this.data = new JobData<>(data);
    }

    public UUID getId() {
        return id;
    }

    public T getData() {
        return data.getData();
    }

    public JobState getState() {
        return state;
    }

    public boolean isNotFinalized() {
        return state == JobState.PREPARED;
    }

    public void commit() {
        state = JobState.COMMITTED;
    }

    public void abort() {
        state = JobState.ABORTED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Job<?> job = (Job<?>) o;
        return Objects.equals(id, job.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
