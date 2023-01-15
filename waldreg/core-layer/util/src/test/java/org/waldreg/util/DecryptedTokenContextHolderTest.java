package org.waldreg.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DecryptedTokenContextHolder.class})
public class DecryptedTokenContextHolderTest{

    @Autowired
    private DecryptedTokenContextHolder decryptedTokenContextHolder;

    @Test
    @DisplayName("UserId를 같은 스레드에서 전달 하는 테스트")
    public void RECEIVE_USER_ID_ON_SAME_THREAD_TEST(){
        // given
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Callable<Integer> callable1 = () -> {
            decryptedTokenContextHolder.hold(1);
            Thread.sleep(500);
            int result = decryptedTokenContextHolder.get();
            decryptedTokenContextHolder.resolve();
            return result;
        };

        Callable<Integer> callable2 = () -> {
            decryptedTokenContextHolder.hold(2);
            int result = decryptedTokenContextHolder.get();
            decryptedTokenContextHolder.resolve();
            return result;
        };

        // when
        Future<Integer> expected1 = executorService.submit(callable1);
        Future<Integer> expected2 = executorService.submit(callable2);

        // then
        Assertions.assertAll(
                () -> Assertions.assertEquals(1, expected1.get()),
                () -> Assertions.assertEquals(2, expected2.get())
        );
    }

    @Test
    @DisplayName("UserId를 같은 스레드에서 전달하는 테스트 - 스레드 안정성 테스트")
    public void RECEIVE_USER_ID_ON_SAME_THREAD_THREAD_SAFETY_TEST() throws Exception{
        ExecutorService executorService = Executors.newCachedThreadPool();
        List<Callable<Integer>> runnableList = new ArrayList<>();
        for (int i = 0; i < 100; i++){
            int finalI = i;
            runnableList.add(() -> {
                decryptedTokenContextHolder.hold(finalI);
                Thread.sleep((int) (Math.random() * 1000));
                Assertions.assertEquals(finalI, decryptedTokenContextHolder.get());
                decryptedTokenContextHolder.resolve();
                return 0;
            });
        }
        List<Future<Integer>> futureList = executorService.invokeAll(runnableList);
        waitUntilAllFutureDone(futureList);
    }

    private void waitUntilAllFutureDone(List<Future<Integer>> futureList) throws Exception{
        for(Future<Integer> future : futureList){
            future.get();
        }
    }

}
