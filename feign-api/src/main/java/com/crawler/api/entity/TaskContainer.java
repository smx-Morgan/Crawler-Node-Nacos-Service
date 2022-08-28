package com.crawler.api.entity;

import com.crawler.api.excep.TaskException;
import lombok.NonNull;

import java.util.concurrent.ConcurrentHashMap;

public class TaskContainer {

    private final ConcurrentHashMap<TaskWrapper, Boolean> taskDoneMap = new ConcurrentHashMap<>();

    public boolean taskNotExist(TaskWrapper task) {
        return !taskDoneMap.containsKey(task);
    }

    public boolean taskIsDone(TaskWrapper task) {
        Boolean isDone = taskDoneMap.get(task);
        return isDone != null && isDone;
    }

    public void register(@NonNull TaskWrapper task) {
        if (taskNotExist(task)) {
            taskDoneMap.put(task, false);
            return;
        }

        throw new TaskException("任务被重复注册：" + task);
    }

    public void accomplish(@NonNull TaskWrapper task) {
        if (taskNotExist(task)) {
            throw new TaskException("未注册的任务被完成：" + task);
        }

        taskDoneMap.put(task, true);
    }

}
