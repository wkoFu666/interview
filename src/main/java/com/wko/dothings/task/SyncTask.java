package com.wko.dothings.task;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class SyncTask {

    @Async
    public void doTransferAccount() {
    }
}
