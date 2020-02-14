package com.example.TODOList;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class TodoListApplication {
	public static Database database;

	public static void main(String[] args) {
		String path = System.getProperty("user.dir") + "/todos.db";
		database = new Database(path);

		SpringApplication.run(TodoListApplication.class, args);
	}

	public static void storeTasks(List<Task> tasks) {
		database.set("tasks", tasks);
		database.save();
	}
	public static List<Task> getTasks() {
		Object rawTasks = database.get("tasks");
		if (rawTasks == null) {
			return new ArrayList<Task>();
		}

		List<Task> tasks = new ArrayList<Task>();
		if (rawTasks instanceof ArrayList) {
			ObjectMapper mapper = new ObjectMapper();
			for (int i = 0; i < ((ArrayList)rawTasks).size(); i++) {
				Task t = mapper.convertValue(((ArrayList)rawTasks).get(i), Task.class);
				tasks.add(t);
			}
		}
		return tasks;
	}

}
