package org.hyejoon.cuvcourse.domain.lecture;

import org.hyejoon.cuvcourse.domain.lecture.cache.LectureCacheProvider;
import org.hyejoon.cuvcourse.domain.lecture.repository.LectureJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class LecturePerformanceTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private LectureCacheProvider lectureCacheProvider;

    @Autowired
    private LectureJpaRepository lectureJpaRepository;

    @Autowired
    private RedisCacheManager cacheManager;

    @BeforeEach
    void setUp() {
        for (String cacheName : cacheManager.getCacheNames()) {
            Optional.ofNullable(cacheManager.getCache(cacheName)).ifPresent(Cache::clear);
        }
        List<Object[]> batchArgs = new ArrayList<>();
        for (int i = 1; i <= 10000; i++) {
            batchArgs.add(new Object[]{
                "Lecture " + i,
                "Professor " + (i % 100),
                i % 5 + 1,
                30 + (i % 20)
            });
        }
        jdbcTemplate.batchUpdate(
            "INSERT INTO lectures (lecture_title, professor_name, credits, capacity, total, created_at) VALUES (?, ?, ?, ?, 0, NOW())",
            batchArgs
        );
    }

    @AfterEach
    void teardown() {
        lectureJpaRepository.deleteAllInBatch();
    }

    @Test
    void 캐시가_더_빠르다() throws InterruptedException, BrokenBarrierException {
        Pageable pageable = PageRequest.of(0, 10);

        // warm up
        lectureCacheProvider.getLecturesByPageable(pageable);

        long startWithCache = System.currentTimeMillis();
        runConcurrentCalls(() -> lectureCacheProvider.getLecturesByPageable(pageable), 1000);
        long endWithCache = System.currentTimeMillis();
        long timeWithCache = endWithCache - startWithCache;

        long startWithoutCache = System.currentTimeMillis();
        runConcurrentCalls(() -> lectureCacheProvider.getLecturesByPageableWithoutCache(pageable), 1000);
        long endWithoutCache = System.currentTimeMillis();
        long timeWithoutCache = endWithoutCache - startWithoutCache;

        System.out.println("캐시 On: " + timeWithCache + " ms");
        System.out.println("캐시 Off: " + timeWithoutCache + " ms");
        System.out.println("캐시 승 : " + (timeWithoutCache - timeWithCache) + " ms");
    }

    private void runConcurrentCalls(Runnable task, int numberOfCalls) throws InterruptedException, BrokenBarrierException {
        CyclicBarrier barrier = new CyclicBarrier(numberOfCalls);
        ExecutorService executor = Executors.newFixedThreadPool(numberOfCalls);
        for (int i = 0; i < numberOfCalls; i++) {
            executor.submit(() -> {
                try {
                    barrier.await();
                    task.run();
                } catch (InterruptedException | BrokenBarrierException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
    }
}