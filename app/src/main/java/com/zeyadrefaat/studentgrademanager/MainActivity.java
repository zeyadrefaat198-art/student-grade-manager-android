
package com.zeyadrefaat.studentgrademanager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private boolean addingStudent = false;
    private boolean viewingStudents = false;
    private boolean searchingStudent = false;
    private boolean deletingStudent = false;
    private boolean updatingStudent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showHomeScreen();
    }

    @Override
    public void onBackPressed() {
        if (addingStudent || viewingStudents || searchingStudent
                || deletingStudent || updatingStudent) {
            showHomeScreen();
        } else {
            super.onBackPressed();
        }
    }

    private void showHomeScreen() {
        addingStudent = false;
        viewingStudents = false;
        searchingStudent = false;
        deletingStudent = false;
        updatingStudent = false;

        setContentView(R.layout.activity_main);

        TextView studentCount = findViewById(R.id.tvStudentCount);

        SharedPreferences prefs =
                getSharedPreferences("StudentData", MODE_PRIVATE);

        try {
            JSONArray students = new JSONArray(
                    prefs.getString("students", "[]")
            );

            studentCount.setText("Total Students: " + students.length());

        } catch (Exception e) {
            studentCount.setText("Total Students: 0");
        }

        Button addButton = findViewById(R.id.btnAdd);
        Button viewButton = findViewById(R.id.btnView);
        Button searchButton = findViewById(R.id.btnSearch);
        Button deleteButton = findViewById(R.id.btnDelete);
        Button updateButton = findViewById(R.id.btnUpdate);
        Button averageButton = findViewById(R.id.btnAverage);
        Button aboutButton = findViewById(R.id.btnAbout);

        addButton.setOnClickListener(v -> showAddStudentScreen());
        viewButton.setOnClickListener(v -> showStudentsScreen());
        searchButton.setOnClickListener(v -> showSearchScreen());
        deleteButton.setOnClickListener(v -> showDeleteScreen());
        updateButton.setOnClickListener(v -> showUpdateScreen());
        averageButton.setOnClickListener(v -> calculateAverage());
        aboutButton.setOnClickListener(v -> showAboutScreen());
    }

    private void showAddStudentScreen() {
        addingStudent = true;
        viewingStudents = false;
        searchingStudent = false;
        deletingStudent = false;
        updatingStudent = false;

        setContentView(R.layout.activity_add_student);

        Button saveButton = findViewById(R.id.btnSaveStudent);
        saveButton.setOnClickListener(v -> saveStudent());
    }

    private void saveStudent() {
        EditText nameInput = findViewById(R.id.etStudentName);
        EditText idInput = findViewById(R.id.etStudentId);
        EditText gradeInput = findViewById(R.id.etStudentGrade);

        String name = nameInput.getText().toString().trim();
        String id = idInput.getText().toString().trim();
        String grade = gradeInput.getText().toString().trim();

        if (name.isEmpty() || id.isEmpty() || grade.isEmpty()) {
            Toast.makeText(this, "Please fill all fields",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double studentGrade = Double.parseDouble(grade);

            if (studentGrade < 0 || studentGrade > 100) {
                Toast.makeText(this,
                        "Grade must be between 0 and 100",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences prefs =
                    getSharedPreferences("StudentData", MODE_PRIVATE);

            JSONArray students = new JSONArray(
                    prefs.getString("students", "[]")
            );

            for (int i = 0; i < students.length(); i++) {
                if (students.getJSONObject(i).getString("id").equals(id)) {
                    Toast.makeText(this, "Student ID already exists",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            JSONObject student = new JSONObject();
            student.put("name", name);
            student.put("id", id);
            student.put("grade", studentGrade);

            students.put(student);

            prefs.edit()
                    .putString("students", students.toString())
                    .apply();

            Toast.makeText(this, "Student saved successfully!",
                    Toast.LENGTH_SHORT).show();

            showHomeScreen();

        } catch (Exception e) {
            Toast.makeText(this, "Invalid grade",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void showStudentsScreen() {
        addingStudent = false;
        viewingStudents = true;
        searchingStudent = false;
        deletingStudent = false;
        updatingStudent = false;

        setContentView(R.layout.activity_view_students);

        android.widget.LinearLayout studentsContainer =
                findViewById(R.id.studentsContainer);

        studentsContainer.removeAllViews();

        SharedPreferences prefs =
                getSharedPreferences("StudentData", MODE_PRIVATE);

        try {
            JSONArray students = new JSONArray(
                    prefs.getString("students", "[]")
            );

            if (students.length() == 0) {
                TextView emptyMessage = new TextView(this);
                emptyMessage.setText("No students found");
                emptyMessage.setTextSize(16);
                emptyMessage.setTextColor(android.graphics.Color.GRAY);
                emptyMessage.setPadding(12, 20, 12, 20);

                studentsContainer.addView(emptyMessage);
                return;
            }

            for (int i = 0; i < students.length(); i++) {
                JSONObject student = students.getJSONObject(i);

                android.widget.LinearLayout card =
                        new android.widget.LinearLayout(this);

                card.setOrientation(
                        android.widget.LinearLayout.VERTICAL
                );

                card.setPadding(20, 20, 20, 20);

                android.graphics.drawable.GradientDrawable shape =
                        new android.graphics.drawable.GradientDrawable();

                shape.setColor(android.graphics.Color.WHITE);
                shape.setCornerRadius(24);

                card.setBackground(shape);
                card.setElevation(4);

                android.widget.LinearLayout.LayoutParams params =
                        new android.widget.LinearLayout.LayoutParams(
                                -1, -2
                        );

                params.setMargins(0, 0, 0, 20);

                TextView title = new TextView(this);
                title.setText("STUDENT " + (i + 1));
                title.setTextSize(17);
                title.setTextColor(
                        android.graphics.Color.parseColor("#2563EB")
                );
                title.setTypeface(null, android.graphics.Typeface.BOLD);

                TextView name = new TextView(this);
                name.setText("Name: " + student.getString("name"));
                name.setTextSize(16);
                name.setTextColor(
                        android.graphics.Color.parseColor("#334155")
                );
                name.setPadding(0, 12, 0, 0);

                TextView id = new TextView(this);
                id.setText("ID: " + student.getString("id"));
                id.setTextSize(16);
                id.setTextColor(
                        android.graphics.Color.parseColor("#334155")
                );
                id.setPadding(0, 8, 0, 0);

                TextView grade = new TextView(this);
                grade.setText("Grade: " + student.getDouble("grade"));
                grade.setTextSize(16);
                grade.setTextColor(
                        android.graphics.Color.parseColor("#059669")
                );
                grade.setTypeface(null, android.graphics.Typeface.BOLD);
                grade.setPadding(0, 8, 0, 0);

                card.addView(title);
                card.addView(name);
                card.addView(id);
                card.addView(grade);

                studentsContainer.addView(card, params);
            }

        } catch (Exception e) {
            TextView errorMessage = new TextView(this);
            errorMessage.setText("Error loading students");
            errorMessage.setTextColor(android.graphics.Color.RED);

            studentsContainer.removeAllViews();
            studentsContainer.addView(errorMessage);
        }
    }

    private void showSearchScreen() {
        addingStudent = false;
        viewingStudents = false;
        searchingStudent = true;
        deletingStudent = false;
        updatingStudent = false;

        setContentView(R.layout.activity_search_student);

        Button searchButton = findViewById(R.id.btnSearchStudent);
        searchButton.setOnClickListener(v -> searchStudent());
    }

    private void searchStudent() {
        EditText idInput = findViewById(R.id.etSearchId);
        TextView resultView = findViewById(R.id.tvSearchResult);

        String searchId = idInput.getText().toString().trim();

        if (searchId.isEmpty()) {
            resultView.setText("Please enter Student ID");
            return;
        }

        SharedPreferences prefs =
                getSharedPreferences("StudentData", MODE_PRIVATE);

        try {
            JSONArray students = new JSONArray(
                    prefs.getString("students", "[]")
            );

            for (int i = 0; i < students.length(); i++) {
                JSONObject student = students.getJSONObject(i);

                if (student.getString("id").equals(searchId)) {
                    resultView.setText(
                            "STUDENT FOUND\n\n" +
                            "Name: " + student.getString("name") + "\n\n" +
                            "ID: " + student.getString("id") + "\n\n" +
                            "Grade: " + student.getDouble("grade")
                    );

                    resultView.setBackgroundColor(
                            android.graphics.Color.parseColor("#ECFDF5")
                    );

                    resultView.setTextColor(
                            android.graphics.Color.parseColor("#047857")
                    );

                    return;
                }
            }

            resultView.setText("Student not found");

            resultView.setBackgroundColor(
                    android.graphics.Color.parseColor("#FEF2F2")
            );

            resultView.setTextColor(
                    android.graphics.Color.parseColor("#DC2626")
            );

        } catch (Exception e) {
            resultView.setText("Error searching student");
        }
    }

    private void showDeleteScreen() {
        addingStudent = false;
        viewingStudents = false;
        searchingStudent = false;
        deletingStudent = true;
        updatingStudent = false;

        setContentView(R.layout.activity_delete_student);

        Button deleteButton = findViewById(R.id.btnConfirmDelete);
        deleteButton.setOnClickListener(v -> deleteStudent());
    }

    private void deleteStudent() {
        EditText idInput = findViewById(R.id.etDeleteId);
        String deleteId = idInput.getText().toString().trim();

        if (deleteId.isEmpty()) {
            Toast.makeText(this, "Please enter Student ID",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs =
                getSharedPreferences("StudentData", MODE_PRIVATE);

        try {
            JSONArray students = new JSONArray(
                    prefs.getString("students", "[]")
            );

            for (int i = 0; i < students.length(); i++) {
                JSONObject student = students.getJSONObject(i);

                if (student.getString("id").equals(deleteId)) {
                    students.remove(i);

                    prefs.edit()
                            .putString("students", students.toString())
                            .apply();

                    Toast.makeText(this, "Student deleted successfully!",
                            Toast.LENGTH_SHORT).show();

                    showHomeScreen();
                    return;
                }
            }

            Toast.makeText(this, "Student not found",
                    Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(this, "Error deleting student",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void showUpdateScreen() {
        addingStudent = false;
        viewingStudents = false;
        searchingStudent = false;
        deletingStudent = false;
        updatingStudent = true;

        setContentView(R.layout.activity_update_student);

        Button updateButton = findViewById(R.id.btnUpdateStudent);
        updateButton.setOnClickListener(v -> updateStudent());
    }

    private void updateStudent() {
        EditText idInput = findViewById(R.id.etUpdateId);
        EditText nameInput = findViewById(R.id.etUpdateName);
        EditText gradeInput = findViewById(R.id.etUpdateGrade);

        String id = idInput.getText().toString().trim();
        String name = nameInput.getText().toString().trim();
        String grade = gradeInput.getText().toString().trim();

        if (id.isEmpty() || name.isEmpty() || grade.isEmpty()) {
            Toast.makeText(this, "Please fill all fields",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double newGrade = Double.parseDouble(grade);

            if (newGrade < 0 || newGrade > 100) {
                Toast.makeText(this,
                        "Grade must be between 0 and 100",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences prefs =
                    getSharedPreferences("StudentData", MODE_PRIVATE);

            JSONArray students = new JSONArray(
                    prefs.getString("students", "[]")
            );

            for (int i = 0; i < students.length(); i++) {
                JSONObject student = students.getJSONObject(i);

                if (student.getString("id").equals(id)) {
                    student.put("name", name);
                    student.put("grade", newGrade);

                    prefs.edit()
                            .putString("students", students.toString())
                            .apply();

                    Toast.makeText(this, "Student updated successfully!",
                            Toast.LENGTH_SHORT).show();

                    showHomeScreen();
                    return;
                }
            }

            Toast.makeText(this, "Student not found",
                    Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(this, "Invalid grade",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void showAboutScreen() {
        addingStudent = false;
        viewingStudents = false;
        searchingStudent = false;
        deletingStudent = false;
        updatingStudent = false;

        setContentView(R.layout.activity_about);

        Button backButton = findViewById(R.id.btnAboutBack);
        backButton.setOnClickListener(v -> showHomeScreen());
    }

    private void calculateAverage() {
        SharedPreferences prefs =
                getSharedPreferences("StudentData", MODE_PRIVATE);

        try {
            JSONArray students = new JSONArray(
                    prefs.getString("students", "[]")
            );

            if (students.length() == 0) {
                Toast.makeText(this, "No students found",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            double total = 0;

            for (int i = 0; i < students.length(); i++) {
                JSONObject student = students.getJSONObject(i);
                total += student.getDouble("grade");
            }

            double average = total / students.length();

            new android.app.AlertDialog.Builder(this)
                    .setTitle("Students Average")
                    .setMessage(
                            "Total Students: " + students.length() +
                            "\n\nAverage Grade: " +
                            String.format(
                                    java.util.Locale.US,
                                    "%.2f",
                                    average
                            )
                    )
                    .setPositiveButton("OK", null)
                    .show();

        } catch (Exception e) {
            Toast.makeText(this, "Error calculating average",
                    Toast.LENGTH_SHORT).show();
        }
    }
}