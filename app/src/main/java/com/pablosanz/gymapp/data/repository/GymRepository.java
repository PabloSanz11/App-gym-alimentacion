package com.pablosanz.gymapp.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.pablosanz.gymapp.data.db.AppDatabase;
import com.pablosanz.gymapp.data.db.BodyMeasurementDao;
import com.pablosanz.gymapp.data.db.ExerciseLogDao;
import com.pablosanz.gymapp.data.db.WorkoutSessionDao;
import com.pablosanz.gymapp.data.model.BodyMeasurement;
import com.pablosanz.gymapp.data.model.ExerciseLog;
import com.pablosanz.gymapp.data.model.WorkoutSession;

import java.util.List;
import java.util.concurrent.Future;

public class GymRepository {

    private final WorkoutSessionDao sessionDao;
    private final ExerciseLogDao exerciseLogDao;
    private final BodyMeasurementDao bodyMeasurementDao;
    private final LiveData<List<WorkoutSession>> allSessions;
    private final LiveData<List<BodyMeasurement>> allMeasurements;

    public GymRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        sessionDao = db.workoutSessionDao();
        exerciseLogDao = db.exerciseLogDao();
        bodyMeasurementDao = db.bodyMeasurementDao();
        allSessions = sessionDao.getAll();
        allMeasurements = bodyMeasurementDao.getAll();
    }

    public LiveData<List<WorkoutSession>> getAllSessions() {
        return allSessions;
    }

    public LiveData<List<BodyMeasurement>> getAllMeasurements() {
        return allMeasurements;
    }

    public void insertSession(WorkoutSession session, OnInsertCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            long id = sessionDao.insert(session);
            if (callback != null) callback.onInserted(id);
        });
    }

    public void insertExerciseLog(ExerciseLog log) {
        AppDatabase.databaseWriteExecutor.execute(() -> exerciseLogDao.insert(log));
    }

    public void insertMeasurement(BodyMeasurement measurement) {
        AppDatabase.databaseWriteExecutor.execute(() -> bodyMeasurementDao.insert(measurement));
    }

    public void getLastSession(OnSessionCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            WorkoutSession session = sessionDao.getLastSession();
            if (callback != null) callback.onResult(session);
        });
    }

    public void getRecentMeasurements(int limit, OnMeasurementsCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<BodyMeasurement> measurements = bodyMeasurementDao.getRecent(limit);
            if (callback != null) callback.onResult(measurements);
        });
    }

    public interface OnInsertCallback {
        void onInserted(long id);
    }

    public interface OnSessionCallback {
        void onResult(WorkoutSession session);
    }

    public interface OnMeasurementsCallback {
        void onResult(List<BodyMeasurement> measurements);
    }
}
