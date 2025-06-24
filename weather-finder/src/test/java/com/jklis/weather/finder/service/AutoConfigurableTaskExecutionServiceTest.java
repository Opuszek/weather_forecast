package com.jklis.weather.finder.service;

import com.jklis.database.utilities.Utilities;
import com.jklis.utilities.Either;
import static com.jklis.weather.finder.service.AutoConfigurableTaskExecutionService.MULTI_THREAD_NUMBER_PROP;
import static com.jklis.weather.finder.service.AutoConfigurableTaskExecutionService.SINGLE_THREAD_BOOL_PROP;
import static com.jklis.weather.finder.service.AutoConfigurableTaskExecutionService.SINGLE_THREAD_DELAY_PROP;
import java.lang.reflect.Field;
import org.hamcrest.core.IsInstanceOf;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AutoConfigurableTaskExecutionServiceTest {
    
    static final long DELAY = 50;
    static final int THREAD_NUMBER = 200;
    static final String TASK_RESULT_1 = "task_result_1";
    static final String TASK_RESULT_2 = "task_result_2";

    @Test
    public void initializingServiceWithoutSettingPropertiesThrowsRuntimeException() throws Exception {
        try (MockedStatic<Utilities> utilities = Mockito.mockStatic(Utilities.class)) {
            utilities.when(() -> Utilities.getProperty(SINGLE_THREAD_BOOL_PROP)).thenReturn(null);
            assertThrows(
                    RuntimeException.class,
                    () -> new AutoConfigurableTaskExecutionService()
            );
        }
    }
    
    
    @Test
    public void initServiceWithPropsSetToSingleThreadDelayResultInSchedExecServRunWithFixedDelay() throws Exception {
        try (MockedStatic<Utilities> utilities = Mockito.mockStatic(Utilities.class)) {
            utilities.when(() -> Utilities.getProperty(SINGLE_THREAD_BOOL_PROP)).thenReturn("true");
            utilities.when(() -> Utilities.getProperty(SINGLE_THREAD_DELAY_PROP)).thenReturn(String.valueOf(DELAY));
            AutoConfigurableTaskExecutionService service = new AutoConfigurableTaskExecutionService();
            ExecutorService spy = getInternalExecutorServiceSpy(service);
            assertThat(spy, IsInstanceOf.instanceOf(ScheduledExecutorService.class));
            List<String>results = service.execute(tasks());
            verify((ScheduledExecutorService) spy, times(1))
                    .scheduleWithFixedDelay(any(Runnable.class), any(Long.class), 
                            eq(DELAY), eq(TimeUnit.MILLISECONDS));
            assertThat(results, containsInAnyOrder(TASK_RESULT_1, TASK_RESULT_2));
        }
    }
    
    @Test
    public void initServiceWithPropsSetToMultiThreadResultInExecServNotBeingScheduled() throws Exception {
        try (MockedStatic<Utilities> utilities = Mockito.mockStatic(Utilities.class)) {
            utilities.when(() -> Utilities.getProperty(SINGLE_THREAD_BOOL_PROP)).thenReturn("false");
            utilities.when(() -> Utilities.getProperty(MULTI_THREAD_NUMBER_PROP)).thenReturn(String.valueOf(THREAD_NUMBER));
            AutoConfigurableTaskExecutionService service = new AutoConfigurableTaskExecutionService();
            ExecutorService internalExecutorService = getInternalExecutorService(service);
            setInternalExecutorService(service, spy(internalExecutorService));
            List<String>results = service.execute(tasks());
            assertThat(internalExecutorService, not(IsInstanceOf.instanceOf(ScheduledExecutorService.class)));
            assertThat(results, containsInAnyOrder(TASK_RESULT_1, TASK_RESULT_2));
        }
    }
    
    private ExecutorService getInternalExecutorServiceSpy(AutoConfigurableTaskExecutionService service) 
            throws Exception {
        ExecutorService spy = spy(getInternalExecutorService(service));
        setInternalExecutorService(service, spy);
        return spy;
    }
    
    private ExecutorService getInternalExecutorService(
            AutoConfigurableTaskExecutionService service) throws Exception {
        Method method = service.getClass().getDeclaredMethod("getActiveService");
        method.setAccessible(true);
        return (ExecutorService) method.invoke(service);
    }
    
    private void setInternalExecutorService(AutoConfigurableTaskExecutionService service, 
            ExecutorService intExecutor) throws Exception {
        Field field = service.getClass().getDeclaredField("exEith");
        field.setAccessible(true);
        field.set(service, isSingleThread(service) ?
                Either.withLeft(intExecutor) : Either.withRight(intExecutor));
    }
        
    private boolean isSingleThread(
            AutoConfigurableTaskExecutionService service) throws Exception {
        Field field = service.getClass().getDeclaredField("singleThread");
        field.setAccessible(true);
        return field.getBoolean(service);
    }
 
    private Queue<Callable<String>> tasks() {
        return new LinkedList<>(
                Arrays.asList(
                        () -> TASK_RESULT_1,
                        () -> TASK_RESULT_2
                )
        );
    }
}
