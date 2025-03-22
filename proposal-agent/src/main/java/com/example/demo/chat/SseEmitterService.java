package com.example.demo.chat;

import lombok.Getter;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

@Service
public class SseEmitterService {

    private final ExecutorService executorService;
    private final Semaphore semaphore;
    private final ConcurrentHashMap<String, SseEmitterReference> emitters = new ConcurrentHashMap<>();

    @Getter
    public static class SseEmitterReference {

        private SseEmitter emitter;
        private final String clientId;

        public SseEmitterReference(String clientId) {
            this.clientId = clientId;
        }

        public void create(Semaphore semaphore, ConcurrentHashMap<String, SseEmitterReference> concurrentHashMap){
            try {
                semaphore.acquire();
                this.emitter = new SseEmitter(30000L); // 缩短超时时间为30秒
                this.emitter.onTimeout(() -> {
                    this.emitter.complete();
                    semaphore.release(); // 释放信号量许可
                    concurrentHashMap.remove(clientId);
                });

                // 释放信号量许可
                this.emitter.onCompletion(() -> {
                    semaphore.release(); // 释放信号量许可
                    concurrentHashMap.remove(clientId);
                });

                this.emitter.onError((ex) -> {
                    semaphore.release(); // 释放信号量许可
                    concurrentHashMap.remove(clientId);
                });
            } catch (InterruptedException e) {
                throw new RuntimeException("Server is busy, please try again later");
            }
        }
    }

    public SseEmitterService() {
        this.executorService = Executors.newFixedThreadPool(50); // 创建固定大小的线程池
        this.semaphore = new Semaphore(100); // 限制最大并发连接数为100
    }

    public SseEmitterReference createEmitter(String clientId, SseEmitterTask task) {
        // 获取信号量许可
        SseEmitterReference reference = new SseEmitterReference(clientId);
        reference.create(semaphore, emitters);
        emitters.put(clientId, reference); // 将emitter存储在map中
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                task.apply(reference.getEmitter());
            }
        }); // 使用线程池提交任务
        return reference;
    }

    public void sendMessageToEmitter(String clientId, String message) {
        SseEmitterReference emitterReference = emitters.get(clientId);
        if (emitterReference != null) {
            try {
                emitterReference.emitter.send(SseEmitter.event().data(message));
            } catch (Exception e) {
                emitters.remove(clientId); // 移除失效的emitter
            }
        }
    }

}