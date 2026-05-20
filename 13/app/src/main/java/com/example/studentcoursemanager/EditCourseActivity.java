package com.example.studentcoursemanager;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class EditCourseActivity extends AppCompatActivity {
    private static final String EXTRA_COURSE = "extra_course";

    private ProgressBar progressBar;
    private TextInputEditText nameField;
    private TextInputEditText codeField;
    private TextInputEditText instructorField;
    private TextInputEditText scheduleField;
    private TextInputEditText roomField;
    private Spinner creditSpinner;
    private Spinner semesterSpinner;
    private Button buttonPrimary;
    private Button buttonDelete;
    private Course course;

    public static Intent newIntent(Context context, Course course) {
        return new Intent(context, EditCourseActivity.class).putExtra(EXTRA_COURSE, course);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_form);

        course = resolveCourse();
        if (course == null) {
            finish();
            return;
        }

        setSupportActionBar(findViewById(R.id.formToolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Edit Course");
        }

        bindViews();
        setupSpinners();
        fillFields();

        buttonPrimary.setText("Update Course");
        buttonPrimary.setOnClickListener(view -> updateCourse());
        Button secondaryButton = findViewById(R.id.buttonSecondary);
        secondaryButton.setOnClickListener(view -> finish());

        buttonDelete.setVisibility(View.VISIBLE);
        buttonDelete.setEnabled(true);
        buttonDelete.setAlpha(1f);
        buttonDelete.setOnClickListener(view -> confirmDelete());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void bindViews() {
        progressBar = findViewById(R.id.progressSaving);
        nameField = findViewById(R.id.inputCourseName);
        codeField = findViewById(R.id.inputCourseCode);
        instructorField = findViewById(R.id.inputInstructor);
        scheduleField = findViewById(R.id.inputSchedule);
        roomField = findViewById(R.id.inputRoom);
        creditSpinner = findViewById(R.id.spinnerCreditHours);
        semesterSpinner = findViewById(R.id.spinnerSemester);
        buttonPrimary = findViewById(R.id.buttonPrimary);
        buttonDelete = findViewById(R.id.buttonDelete);
    }

    private void setupSpinners() {
        List<String> creditOptions = List.of("1", "2", "3", "4");
        List<String> semesterOptions = List.of("Spring 2025", "Summer 2025", "Fall 2025");
        creditSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, creditOptions));
        semesterSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, semesterOptions));
    }

    private void fillFields() {
        nameField.setText(course.getName());
        codeField.setText(course.getCode());
        instructorField.setText(course.getInstructor());
        scheduleField.setText(course.getSchedule());
        roomField.setText(course.getRoom());
        creditSpinner.setSelection(Math.max(0, Math.min(3, course.getCreditHours() - 1)));
        int semesterIndex = List.of("Spring 2025", "Summer 2025", "Fall 2025").indexOf(course.getSemester());
        semesterSpinner.setSelection(Math.max(0, semesterIndex));
    }

    private void updateCourse() {
        Course updatedCourse = collectCourse();
        if (updatedCourse == null) {
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        CourseRepository.upsertCourse(this, updatedCourse.withId(course.getId()));
        progressBar.setVisibility(View.GONE);
        Toast.makeText(this, "Course updated successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void confirmDelete() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Delete course")
                .setMessage("Delete " + course.getName() + " from your list?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) -> {
                    CourseRepository.deleteCourse(this, course.getId());
                    Toast.makeText(this, "Course deleted", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .show();
    }

    private Course collectCourse() {
        String name = textOf(nameField).trim();
        String code = textOf(codeField).trim();
        String instructor = textOf(instructorField).trim();

        if (name.isEmpty()) {
            nameField.setError("Required");
            return null;
        }
        if (code.isEmpty()) {
            codeField.setError("Required");
            return null;
        }
        if (instructor.isEmpty()) {
            instructorField.setError("Required");
            return null;
        }

        int creditHours = Integer.parseInt(String.valueOf(creditSpinner.getSelectedItem()));
        return new Course(
                name,
                code,
                instructor,
                creditHours,
                textOf(scheduleField).trim(),
                textOf(roomField).trim(),
                String.valueOf(semesterSpinner.getSelectedItem())
        );
    }

    private Course resolveCourse() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return getIntent().getSerializableExtra(EXTRA_COURSE, Course.class);
        }
        @SuppressWarnings("deprecation")
        Course extraCourse = (Course) getIntent().getSerializableExtra(EXTRA_COURSE);
        return extraCourse;
    }

    private String textOf(TextInputEditText field) {
        return field.getText() == null ? "" : field.getText().toString();
    }
}