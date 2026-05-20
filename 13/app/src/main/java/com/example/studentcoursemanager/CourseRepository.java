package com.example.studentcoursemanager;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class CourseRepository {
    private static final String PREFS_NAME = "student_course_manager_prefs";
    private static final String KEY_COURSES = "courses_json";

    private static final Set<Runnable> LISTENERS = new LinkedHashSet<>();

    private CourseRepository() {
    }

    public static void addListener(Runnable listener) {
        LISTENERS.add(listener);
        listener.run();
    }

    public static void removeListener(Runnable listener) {
        LISTENERS.remove(listener);
    }

    public static List<Course> getCourses(Context context) {
        return loadCourses(context);
    }

    public static Course upsertCourse(Context context, Course course) {
        Course savedCourse = course.getId().isBlank()
                ? course.withId(UUID.randomUUID().toString())
                : course;

        List<Course> courses = new ArrayList<>(loadCourses(context));
        int existingIndex = -1;
        for (int index = 0; index < courses.size(); index++) {
            if (courses.get(index).getId().equals(savedCourse.getId())) {
                existingIndex = index;
                break;
            }
        }
        if (existingIndex >= 0) {
            courses.set(existingIndex, savedCourse);
        } else {
            courses.add(savedCourse);
        }

        persistCourses(context, courses);
        notifyListeners();
        return savedCourse;
    }

    public static void deleteCourse(Context context, String courseId) {
        List<Course> filteredCourses = new ArrayList<>();
        for (Course course : loadCourses(context)) {
            if (!course.getId().equals(courseId)) {
                filteredCourses.add(course);
            }
        }
        persistCourses(context, filteredCourses);
        notifyListeners();
    }

    private static List<Course> loadCourses(Context context) {
        SharedPreferences preferences = preferences(context);
        String storedValue = preferences.getString(KEY_COURSES, "[]");
        List<Course> courses = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(storedValue == null ? "[]" : storedValue);
            for (int index = 0; index < array.length(); index++) {
                courses.add(toCourse(array.optJSONObject(index)));
            }
        } catch (Exception ignored) {
        }
        return courses;
    }

    private static void persistCourses(Context context, List<Course> courses) {
        JSONArray array = new JSONArray();
        for (Course course : courses) {
            array.put(toJson(course));
        }
        preferences(context).edit().putString(KEY_COURSES, array.toString()).apply();
    }

    private static SharedPreferences preferences(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    private static void notifyListeners() {
        for (Runnable listener : LISTENERS) {
            listener.run();
        }
    }

    private static JSONObject toJson(Course course) {
        JSONObject object = new JSONObject();
        try {
            object.put("id", course.getId());
            object.put("name", course.getName());
            object.put("code", course.getCode());
            object.put("instructor", course.getInstructor());
            object.put("creditHours", course.getCreditHours());
            object.put("schedule", course.getSchedule());
            object.put("room", course.getRoom());
            object.put("semester", course.getSemester());
        } catch (Exception ignored) {
        }
        return object;
    }

    private static Course toCourse(JSONObject object) {
        if (object == null) {
            return new Course();
        }
        return new Course(
                object.optString("id"),
                object.optString("name"),
                object.optString("code"),
                object.optString("instructor"),
                object.optInt("creditHours", 1),
                object.optString("schedule"),
                object.optString("room"),
                object.optString("semester")
        );
    }
}