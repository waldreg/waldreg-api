package org.waldreg.attendance.event;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.waldreg.attendance.exception.DoesNotStartedAttendanceException;
import org.waldreg.attendance.exception.WrongAttendanceIdentifyException;
import org.waldreg.attendance.valid.AttendanceIdentifyValidable;
import org.waldreg.attendance.event.publish.AttendanceStartEvent;
import org.waldreg.attendance.event.publish.AttendanceStopEvent;
import org.waldreg.attendance.event.subscribe.AttendanceStartedEvent;
import org.waldreg.attendance.event.subscribe.AttendanceLeftTimeEvent;

@Service
public final class AttendanceEvent implements AttendanceIdentifyValidable{

    @SuppressWarnings("all")
    private volatile int attendanceStarter;
    private volatile String attendanceIdentify;
    private final ExecutorService executorService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final Random random;
    private final ConcurrentMap<Integer, Future<?>> futureMap;
    private final AtomicInteger atomicInteger;

    @Autowired
    public AttendanceEvent(ApplicationEventPublisher applicationEventPublisher) throws NoSuchAlgorithmException{
        this.executorService = Executors.newFixedThreadPool(1);
        this.applicationEventPublisher = applicationEventPublisher;
        this.random = SecureRandom.getInstanceStrong();
        this.futureMap = new ConcurrentHashMap<>();
        this.atomicInteger = new AtomicInteger(1);
    }

    @Override
    public void valid(String identify){
        throwIfAttendanceDoesNotStarted();
        throwIfDoesNotMatchedAttendanceIdentify(identify);
    }

    private void throwIfAttendanceDoesNotStarted(){
        deleteDoneTask();
        if(futureMap.isEmpty()){
            throw new DoesNotStartedAttendanceException();
        }
    }

    private void deleteDoneTask(){
        for(Map.Entry<Integer, Future<?>> entry : futureMap.entrySet()){
            if(entry.getValue().isDone()){
                futureMap.remove(entry.getKey());
            }
        }
    }

    private void throwIfDoesNotMatchedAttendanceIdentify(String attendanceIdentify){
        if(!this.attendanceIdentify.equals(attendanceIdentify)){
            throw new WrongAttendanceIdentifyException(attendanceIdentify);
        }
    }

    @EventListener(AttendanceStartEvent.class)
    public void startAttendance(AttendanceStartEvent attendanceStartEvent){
        stopAttendance();
        attendanceStarter = attendanceStartEvent.getAttendanceStarterId();
        attendanceIdentify = String.valueOf(random.nextInt(8000) + 1000);
        futureMap.put(atomicInteger.getAndIncrement(), executorService.submit(attendanceRunner()));
        applicationEventPublisher.publishEvent(AttendanceStartedEvent.builder()
                .attendanceStarterId(attendanceStartEvent.getAttendanceStarterId())
                .attendanceIdentify(attendanceIdentify)
                .build());
    }

    @EventListener(AttendanceStopEvent.class)
    @SuppressWarnings("all")
    public void stopAttendance(){
        for(Map.Entry<Integer, Future<?>> entry : futureMap.entrySet()){
            entry.getValue().cancel(true);
            futureMap.remove(entry.getKey());
        }
    }

    private Callable<Boolean> attendanceRunner(){
        return () -> {
            try{
                for(int i = 0; i < 60 * 3; i++){
                    applicationEventPublisher.publishEvent(new AttendanceLeftTimeEvent(60 * 3 - i));
                    Thread.sleep(1000);
                }
            } catch (InterruptedException | CancellationException e){
                Thread.currentThread().interrupt();
            }
            return true;
        };
    }

}
