package io.medness.simple2pc.job.adapter.in.web;

public record JobUpdateRequest(State state) {
    enum State {
        committed,
        aborted
    }
}
