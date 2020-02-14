package com.example.TODOList;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Database {
    private String path;
    private File file;
    private Map<String, Object> data;
    private ObjectMapper mapper;

    public Database(String path) {
        this.mapper = new ObjectMapper();
        this.file = new File(path);
        this.path = path;
        this.data = new HashMap<String, Object>();
        this.load();
    }

    public boolean load() {
        if (!this.file.exists())
            return false;

        try {
            this.data = this.mapper.readValue(this.file, this.data.getClass());
            return true;
        } catch (Exception e) {}
        return false;
    }

    public boolean save() {
        try {
            this.mapper.writerWithDefaultPrettyPrinter().writeValue(this.file, this.data);
            return true;
        } catch (Exception e) {}
        return false;
    }

    public void set(String key, Object value) {
        this.data.put(key, value);
    }
    public Object get(String key) {
        return this.data.get(key);
    }
}
