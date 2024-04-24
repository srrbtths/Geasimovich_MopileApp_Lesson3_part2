package ru.mirea.egerasimovich.mireaproject;


import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.concurrent.TimeUnit;

public class NewWorker extends Worker {
    private static final String TAG = "BackWorker";

    public NewWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }
    @NonNull
    @Override
    public Result doWork() {
        try {
            Thread.sleep(3000);
            Log.d(TAG, "Work is done successfully.");
        } catch (InterruptedException e) {
            Log.e(TAG, "Error during work execution", e);
            return Result.failure();
        }
        return Result.success();
    }
}