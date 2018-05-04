package me.qping.aysncRequest;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static me.qping.aysncRequest.AsyncResult.ASYNC_STATUS_PENDING;


/**
 * Created by qping on 2018/5/2.
 */
public class AsyncWebTask {

    private static ConcurrentHashMap<String, AsyncResult> TaskRefMap = new ConcurrentHashMap<>();
    private static ExecutorService pool = Executors.newFixedThreadPool(20);

    public String startTask(final AsyncProcess asyncProcess) {
        final String taskId = generateKey();
        AsyncResult result = AsyncResult.pending(taskId);
        TaskRefMap.put(taskId, result);

        pool.execute(new Runnable() {
            @Override
            public void run() {
                AsyncResult asyncResult = TaskRefMap.get(taskId);
                try{
                    Object result = asyncProcess.doSomething(asyncResult);
                    TaskRefMap.put(taskId, AsyncResult.success(taskId, result));
                }catch (Exception ex){
                    TaskRefMap.put(taskId, AsyncResult.fail(taskId, ex));
                }
            }
        });

        return taskId;
    }

    public AsyncResult getTaskResult(String taskId){
        AsyncResult res = TaskRefMap.get(taskId);
        if(res == null){
            return AsyncResult.noTask(taskId);
        }
        // 如果是已经成功或异常，返回结果时清空记录，防止内存溢出
        if(res != null && res.getStatus() > ASYNC_STATUS_PENDING){
            TaskRefMap.remove(taskId);
        }
        return res;
    }

    private String generateKey() {
        return UUID.randomUUID().toString();
    }

    private static class AsyncWebTaskHolder{
        public static AsyncWebTask instance = new AsyncWebTask();
    }

    private AsyncWebTask(){

    }

    public static AsyncWebTask getInstance(){
        return AsyncWebTaskHolder.instance;
    }

}
