package com.ronghe.common.idwork;

public interface WorkerIdStrategy {
    void initialize();

    long availableWorkerId();

    void release();
}
