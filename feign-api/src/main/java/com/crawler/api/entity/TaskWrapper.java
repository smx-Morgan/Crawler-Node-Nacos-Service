package com.crawler.api.entity;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.UUID;

@Data
@EqualsAndHashCode
@ToString
@Accessors(chain = true)
public class TaskWrapper {
    private static final Gson G = new Gson();
    final String metaData;
    final String uuid = UUID.randomUUID().toString();

    @Expose
    @ToString.Exclude
    String thisJson; // 缓存json数据

    public TaskWrapper(String urlOrJson) {
        this.metaData = urlOrJson;
    }

    public static TaskWrapper newTask(String json) {
        return new TaskWrapper(json);
    }

    public static TaskWrapper fromJson(String json) {
        return G.fromJson(json, TaskWrapper.class);
    }

    public String toJson() {
        if (thisJson == null) {
            thisJson = G.toJson(this);
        }
        return thisJson;
    }

}
