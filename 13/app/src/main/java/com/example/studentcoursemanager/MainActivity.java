package com.example.studentcoursemanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private CourseAdapter adapter;
    private SearchView searchView;
    private String currentQuery = "";

    private final Runnable courseListener = this::renderCourses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar(findViewById(R.id.topAppBar));

        adapter = new CourseAdapter(
                this::openDetail,
                this::openEdit,
                this::confirmDelete
        );

        RecyclerView recyclerView = findViewById(R.id.recyclerCourses);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        FloatingActionButton fabAddCourse = findViewById(R.id.fabAddCourse);
        fabAddCourse.setOnClickListener(view -> openAddCourse());

        CourseRepository.addListener(courseListener);
        renderCourses();
    }

    @Override
    protected void onDestroy() {
        CourseRepository.removeListener(courseListener);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search by name or code");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currentQuery = query == null ? "" : query;
                renderCourses();
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                currentQuery = newText == null ? "" : newText;
                renderCourses();
                return true;
            }
        });
        searchView.setOnCloseListener(() -> {
            currentQuery = "";
            renderCourses();
            return false;
        });
        return true;
    }

    private void renderCourses() {
        List<Course> allCourses = CourseRepository.getCourses(this);
        List<Course> filteredCourses;
        if (currentQuery.trim().isEmpty()) {
            filteredCourses = allCourses;
        } else {
            String query = currentQuery.trim().toLowerCase(Locale.ROOT);
            filteredCourses = allCourses.stream()
                    .filter(course -> course.getName().toLowerCase(Locale.ROOT).contains(query)
                            || course.getCode().toLowerCase(Locale.ROOT).contains(query))
                    .toList();
        }

        adapter.submitList(filteredCourses);

        RecyclerView recyclerView = findViewById(R.id.recyclerCourses);
        ConstraintLayout emptyStateView = findViewById(R.id.emptyStateView);
        TextView emptyTitle = findViewById(R.id.textEmptyTitle);

        boolean hasCourses = !allCourses.isEmpty();
        boolean hasFilterResult = !filteredCourses.isEmpty();

        recyclerView.setVisibility(hasCourses && hasFilterResult ? android.view.View.VISIBLE : android.view.View.GONE);
        emptyStateView.setVisibility(!hasCourses || !hasFilterResult ? android.view.View.VISIBLE : android.view.View.GONE);
        emptyTitle.setText(hasCourses ? "No matching courses found" : "No courses added yet");
    }

    private void openAddCourse() {
        startActivity(new Intent(this, AddCourseActivity.class));
    }

    private void openEdit(Course course) {
        startActivity(EditCourseActivity.newIntent(this, course));
    }

    private void openDetail(Course course) {
        startActivity(CourseDetailActivity.newIntent(this, course));
    }

    private void confirmDelete(Course course) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Delete course")
                .setMessage("Delete " + course.getName() + " from your list?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) -> CourseRepository.deleteCourse(this, course.getId()))
                .show();
    }
}