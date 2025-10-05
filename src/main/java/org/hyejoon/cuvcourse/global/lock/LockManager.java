package org.hyejoon.cuvcourse.global.lock;

import java.util.concurrent.Callable;

import org.hyejoon.cuvcourse.global.exception.BusinessException;
import org.hyejoon.cuvcourse.global.exception.GlobalExceptionEnum;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class LockManager {

    private static final int LOCK_ACQUIRE_RETRY_DELAY_MS = 1000;
    private static final int LOCK_ACQUIRE_MAX_RETRY = 3;

    public <T> T executeWithLock(DistributedLock distributedLock, String key,
        Callable<T> callable) {
        boolean acquired = false;
        int attempts = 0;

        while (!acquired && attempts < LOCK_ACQUIRE_MAX_RETRY) {
            acquired = distributedLock.acquireLock(key);
            if (!acquired) {
                try {
                    Thread.sleep(LOCK_ACQUIRE_RETRY_DELAY_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new BusinessException(GlobalExceptionEnum.LOCK_ACQUIRE_FAILED);
                }
            }
            attempts++;
        }

        if (acquired) {
            try {
                return callable.call();
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                log.warn("Exception occured: {}", e.getMessage());
                throw new RuntimeException("Lock Acquire Failed", e);
            } finally {
                distributedLock.releaseLock(key);
            }
        } else {
            throw new BusinessException(GlobalExceptionEnum.LOCK_ACQUIRE_FAILED);
        }
    }

    public void executeWithLock(DistributedLock distributedLock, String key,
        Runnable runnable) {
        this.executeWithLock(distributedLock, key, () -> {
            runnable.run();
            return null;
        });
    }
}
