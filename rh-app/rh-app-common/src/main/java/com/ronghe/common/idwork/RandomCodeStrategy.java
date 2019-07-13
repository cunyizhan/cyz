package com.ronghe.common.idwork;

public interface RandomCodeStrategy {
    void init();

    int prefix();

    int next();

    void release();
}
