当一个请求到达后台需要很长时间时,前台需要等待很长时间，用户体验不好，有的时候负载均衡超时会关闭请求（比如阿里云SLB超过一分钟会报504）。
这时候可以引入ActiveMQ之类的来解耦，但代码比较复杂，还需要部署MQ的服务器。
我的实现方式是，后台新起一个线程来处理，然后请求直接返回成功。后台再提供一个查询状态的入口，通过ajax长轮询来获取是否已经成功，如果成功通知用户。

后台Controller代码示例：
```
    // 后台处理入口
    @RequestMapping(value = "/example/doTask")
    @ResponseBody
    public AsyncResult doTask(final String params) {
        // 创建异步任务
        final AsyncWebTask asyncWebTask = AsyncWebTask.getInstance();
        String taskId = asyncWebTask.startTask(new AsyncProcess() {
            @Override
            public Object doSomething(AsyncResult asyncResult) {
                // 具体的处理逻辑放入service层中
                int flag = service.doTask(params);
                return flag > 0 ? "success" : "fail";
            }
        });

        return asyncWebTask.getTaskResult(taskId);
    }

    // 查询任务处理进度的入口
    @RequestMapping(value = "/example/getTaskStatus")
    @ResponseBody
    public AsyncResult getTaskStatus(String taskId) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return AsyncWebTask.getInstance().getTaskResult(taskId);
    }

```
然后加载JS封装类：
```
<script src="js/async_task.js" type="text/javascript"></script>
```

前台JS代码示例：
```
    asyncTask.startTask({
        taskUrl: "/example/doTask",
        statusUrl: "/example/getTaskStatus",
        data: {},
        success: function (data) {
            if (data == "success") {
                showInfo("更新成功！")
            }
        },
        error: function (e, status) {
            alert(e);
        }
    })

```

