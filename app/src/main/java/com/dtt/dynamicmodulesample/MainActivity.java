package com.dtt.dynamicmodulesample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.play.core.splitcompat.SplitCompat;
import com.google.android.play.core.splitinstall.SplitInstallException;
import com.google.android.play.core.splitinstall.SplitInstallManager;
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory;
import com.google.android.play.core.splitinstall.SplitInstallRequest;
import com.google.android.play.core.splitinstall.SplitInstallSessionState;
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener;
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus;
import com.google.android.play.core.tasks.OnFailureListener;
import com.google.android.play.core.tasks.OnSuccessListener;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Button download;
    private Button start;
    private int mySessionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        download = findViewById(R.id.download);
        start = findViewById(R.id.start);

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadDynamicModule();

            }
        });
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClassName("com.dtt.dynamicmodulesample", "com.dtt.dynamicfeature.DynamicActivity");
                startActivity(intent);
            }
        });
    }

    private void downloadDynamicModule() {
        SplitInstallManager splitInstallManager =
                SplitInstallManagerFactory.create(this);

        SplitInstallRequest request =
                SplitInstallRequest
                        .newBuilder()
                        .addModule("dynamicfeature")
                        .build();

        SplitInstallStateUpdatedListener listener = new SplitInstallStateUpdatedListener() {
            @Override
            public void onStateUpdate(SplitInstallSessionState splitInstallSessionState) {
                if (splitInstallSessionState.sessionId() == mySessionId) {
                    if (splitInstallSessionState.status() == SplitInstallSessionStatus.INSTALLED) {
                        Log.d(TAG, "Dynamic Module downloaded");
                        Toast.makeText(MainActivity.this, "Dynamic Module downloaded", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };

        splitInstallManager.registerListener(listener);
        splitInstallManager.startInstall(request)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Installation successful, check for installed modules.
                        boolean isEnabled = splitInstallManager.getInstalledModules().contains("dynamicfeature");
                        start.setEnabled(isEnabled);
                    } else {
                        // Handle installation failure.
                        Exception exception = task.getException();
                        if (exception instanceof SplitInstallException) {
                            SplitInstallException splitInstallException = (SplitInstallException) exception;
                            // Handle specific split install exception
                            Toast.makeText(this, ""+splitInstallException.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.d(TAG, "Exception: " + e);
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Integer>() {
                    @Override
                    public void onSuccess(Integer sessionId) {

                        mySessionId = sessionId;
                    }
                });



    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        SplitCompat.install(this);
    }
}