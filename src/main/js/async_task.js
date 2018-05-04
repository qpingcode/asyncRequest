var asyncTask = {
    constant : {
        ASYNC_STATUS_SUCCESS : 99, // 成功
        ASYNC_STATUS_PENDING : 0 , // 等待
        ASYNC_STATUS_NOTASK : 1 ,  // 无此任务错误
        ASYNC_STATUS_ERROR : 2 ,   // 其他错误
        ASYNC_STATUS_AJAX_ERROR : -1
    },

    startTask : function(params) {
        var that = this;

        var taskUrl = params.taskUrl;
        var statusUrl = params.statusUrl;
        var successCallback = params.success;
        var errorCallback = params.error;
        var data = params.data;

        $.ajax({
            url : taskUrl,
            cache : false,
            type : "POST",
            async : true,
            data : data,
            contentType : "application/json; charset=utf-8",
            success : function(data) {
                var data = eval("(" + data + ")");
                that.getAsyncStatus(statusUrl, data, successCallback, errorCallback);
            },
            error : function(x, e) {
                errorCallback(e, that.ASYNC_STATUS_AJAX_ERROR)
            }
        });
    },

    getAsyncStatus : function (statusUrl, asyncResult, successCallback, errorCallback) {
        var that = this;
        var taskId = asyncResult.taskId;
        var status = asyncResult.status;

        if(status == that.constant.ASYNC_STATUS_SUCCESS){
            successCallback(asyncResult.msg)
        }

        if(status == that.constant.ASYNC_STATUS_ERROR || status == that.constant.ASYNC_STATUS_NOTASK){
            errorCallback(asyncResult.msg, status)
        }

        if(status == that.constant.ASYNC_STATUS_PENDING){
            $.ajax({
                url : statusUrl + "?taskId="+taskId,
                cache : false,
                type : "POST",
                async : true,
                contentType : "application/json; charset=utf-8",
                success : function(data) {
                    var data = eval("(" + data + ")");
                    that.getAsyncStatus(statusUrl, data, successCallback, errorCallback);
                },
                error : function(x, e) {
                    errorCallback(e, that.ASYNC_STATUS_AJAX_ERROR)
                }
            });

        }
    }


}

