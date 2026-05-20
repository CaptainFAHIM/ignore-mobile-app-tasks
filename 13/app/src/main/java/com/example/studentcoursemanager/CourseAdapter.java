package com.example.studentcoursemanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {
    public interface CourseCallback {
        void onCourse(Course course);
    }

    private final CourseCallback onCourseClick;
    private final CourseCallback onEditClick;
    private final CourseCallback onDeleteClick;
    private final List<Course> items = new ArrayList<>();

    public CourseAdapter(CourseCallback onCourseClick, CourseCallback onEditClick, CourseCallback onDeleteClick) {
        this.onCourseClick = onCourseClick;
        this.onEditClick = onEditClick;
        this.onDeleteClick = onDeleteClick;
    }

    public void submitList(List<Course> courses) {
        items.clear();
        items.addAll(courses);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class CourseViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cardView;
        private final TextView nameView;
        private final TextView codeView;
        private final TextView instructorView;
        private final TextView creditView;
        private final TextView scheduleView;
        private final ImageButton editButton;
        private final ImageButton deleteButton;

        CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.courseCard);
            nameView = itemView.findViewById(R.id.textCourseName);
            codeView = itemView.findViewById(R.id.textCourseCode);
            instructorView = itemView.findViewById(R.id.textInstructor);
            creditView = itemView.findViewById(R.id.textCreditHours);
            scheduleView = itemView.findViewById(R.id.textSchedule);
            editButton = itemView.findViewById(R.id.buttonEditCourse);
            deleteButton = itemView.findViewById(R.id.buttonDeleteCourse);
        }

        void bind(Course course) {
            nameView.setText(course.getName());
            codeView.setText(course.getCode());
            instructorView.setText(course.getInstructor());
            creditView.setText(course.getCreditHours() + " credit hour(s)");
            scheduleView.setText(course.getSchedule().isBlank() ? "No schedule set" : course.getSchedule());

            cardView.setOnClickListener(view -> onCourseClick.onCourse(course));
            editButton.setOnClickListener(view -> onEditClick.onCourse(course));
            deleteButton.setOnClickListener(view -> onDeleteClick.onCourse(course));
        }
    }
}