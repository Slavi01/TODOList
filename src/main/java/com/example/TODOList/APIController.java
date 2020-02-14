package com.example.TODOList;

import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class APIResponse extends HashMap<String, Object> {
    public APIResponse() {
        super();
    }
}

@RestController
@RequestMapping(path = "/api", produces = "application/json")
public class APIController {
    List<Task> tasks;

    public APIController() {
        this.tasks = TodoListApplication.getTasks();
    }
    protected void save() {
        TodoListApplication.storeTasks(tasks);
    }

    @GetMapping("/list")
    public ResponseEntity<APIResponse> list() {
        APIResponse res = new APIResponse();
        res.put("list", tasks);

        this.save();
        return new ResponseEntity<APIResponse>(res, HttpStatus.OK);
    }

    @PutMapping("/add")
    public ResponseEntity<APIResponse> add(@RequestBody Map<String, String> data) {
        APIResponse res = new APIResponse();
        int newId = 1;
        if (tasks.size() > 0)
            newId = tasks.get(tasks.size() - 1).id + 1;

        Task newTask = new Task();
        newTask.id = newId;
        newTask.content = data.getOrDefault("content", "");
        newTask.created_at = System.currentTimeMillis();
        tasks.add(newTask);
        res.put("id", newId);

        this.save();
        return new ResponseEntity<APIResponse>(res, HttpStatus.OK);
    }

    @PostMapping("/{id}")
    public ResponseEntity<APIResponse> setDone(@RequestBody Map<String, Boolean> data, @PathVariable int id) {
        APIResponse res = new APIResponse();
        for (Task t : tasks) {
            if (t.id != id) continue;

            t.done = data.getOrDefault("done", false);
            break;
        }

        this.save();
        return new ResponseEntity<APIResponse>(res, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse> delete(@PathVariable int id) {
        APIResponse res = new APIResponse();
        int foundIndex = -1;
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).id != id) continue;

            foundIndex = i;
            break;
        }

        if (foundIndex != -1) {
            tasks.remove(foundIndex);
        }

        this.save();
        return new ResponseEntity<APIResponse>(res, HttpStatus.OK);
    }

}
