package com.example.studentcoursemanager;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CourseDetailActivity extends AppCompatActivity {
    private static final String EXTRA_COURSE = "extra_course";

    private Course course;

    public static Intent newIntent(Context context, Course course) {
        return new Intent(context, CourseDetailActivity.class).putExtra(EXTRA_COURSE, course);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

        course = resolveCourse();
        if (course == null) {
            finish();
            return;
        }

        setSupportActionBar(findViewById(R.id.detailToolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(course.getName());
        }

        ((TextView) findViewById(R.id.textDetailName)).setText(course.getName());
        ((TextView) findViewById(R.id.textDetailCode)).setText(course.getCode());
        ((TextView) findViewById(R.id.textDetailInstructor)).setText(course.getInstructor());
        ((TextView) findViewById(R.id.textDetailCreditHours)).setText(String.valueOf(course.getCreditHours()));
        ((TextView) findViewById(R.id.textDetailSchedule)).setText(course.getSchedule().isEmpty() ? "Not provided" : course.getSchedule());
        ((TextView) findViewById(R.id.textDetailRoom)).setText(course.getRoom().isEmpty() ? "Not provided" : course.getRoom());
        ((TextView) findViewById(R.id.textDetailSemester)).setText(course.getSemester().isEmpty() ? "Not provided" : course.getSemester());

        FloatingActionButton fabEditCourse = findViewById(R.id.fabEditCourse);
        fabEditCourse.setOnClickListener(view -> startActivity(EditCourseActivity.newIntent(this, course)));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private Course resolveCourse() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return getIntent().getSerializableExtra(EXTRA_COURSE, Course.class);
        }
        @SuppressWarnings("deprecation")
        Course extraCourse = (Course) getIntent().getSerializableExtra(EXTRA_COURSE);
        return extraCourse;
    }
}