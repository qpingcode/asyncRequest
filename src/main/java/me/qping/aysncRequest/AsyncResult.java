package me.qping.aysncRequest;

/**
 * Created by qping on 2018/5/2.
 */
public class AsyncResult {

    public static final int ASYNC_STATUS_SUCCESS = 99;  // 成功
    public static final int ASYNC_STATUS_PENDING = 0;   // 等待
    public static final int ASYNC_STATUS_NOTASK = 1;    // 无此任务错误
    public static final int ASYNC_STATUS_ERROR = 2;     // 其他错误
    private int status = 0;
    private String taskId;
    private Object msg;
    private int percent = 0;                            // 完成百分比

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Object getMsg() {
        return msg;
    }

    public void setMsg(Object msg) {
        this.msg = msg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public static AsyncResult success(String taskId, Object msg) {
        AsyncResult res = new AsyncResult();
        res.setTaskId(taskId);
        res.setStatus(ASYNC_STATUS_SUCCESS);
        res.setMsg(msg);
        res.setPercent(100);
        return res;
    }

    public static AsyncResult fail(String taskId, Exception e) {
        AsyncResult res = new AsyncResult();
        res.setTaskId(taskId);
        res.setStatus(ASYNC_STATUS_ERROR);
        res.setMsg(e.getMessage());
        res.setPercent(100);
        return res;
    }

    public static AsyncResult noTask(String taskId) {
        AsyncResult res = new AsyncResult();
        res.setTaskId(taskId);
        res.setStatus(ASYNC_STATUS_NOTASK);
        res.setMsg("错误：无此任务");
        res.setPercent(100);
        return res;
    }


    public static AsyncResult pending(String taskId) {
        AsyncResult res = new AsyncResult();
        res.setTaskId(taskId);
        res.setStatus(ASYNC_STATUS_PENDING);
        return res;
    }

}
