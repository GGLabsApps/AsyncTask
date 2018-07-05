package com.gglabs.threads;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    TextView tvDisplayProgress, tvDisplayPercent;
    Button btnGo;
    EditText etNumFiles, etDelay;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        tvDisplayProgress = (TextView) findViewById(R.id.tv_display_progress);
        tvDisplayPercent = (TextView) findViewById(R.id.tv_display_percent);
        btnGo = (Button) findViewById(R.id.btn_go);
        etNumFiles = (EditText) findViewById(R.id.et_num_files);
        etDelay = (EditText) findViewById(R.id.et_delay);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        progressBar.setVisibility(View.INVISIBLE);
        tvDisplayProgress.setVisibility(View.INVISIBLE);
        tvDisplayPercent.setVisibility(View.INVISIBLE);
    }

    MyAsyncTask asyncTask;

    public void goClick(View view) {
        if (asyncTask == null || asyncTask.getStatus() == AsyncTask.Status.FINISHED) {
            asyncTask = new MyAsyncTask(tvDisplayProgress, btnGo);
            int numOfFiles = Integer.parseInt(etNumFiles.getText().toString());
            int delay = Integer.parseInt(etDelay.getText().toString());
            asyncTask.execute(numOfFiles, delay);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelTask();
    }

    public void cancelClick(View view) {
        cancelTask();
    }

    private void cancelTask() {
        if (asyncTask != null) asyncTask.cancel(true);
    }

    // Async Task way -------------------------<Params, Progress, Result>
    private class MyAsyncTask extends AsyncTask<Integer, Integer, String> {

        private final WeakReference weakTvDisplay;
        private final WeakReference weakBtn;

        public MyAsyncTask(TextView textView, View view) {
            weakTvDisplay = new WeakReference<TextView>(textView);
            weakBtn = new WeakReference<View>(view);
        }

        @Override
        protected void onPreExecute() {
            btnGo.setEnabled(false);
            tvDisplayPercent.setText("0 %");
            tvDisplayProgress.setText("Simulation has started...");
            progressBar.setProgress(0);
            progressBar.setMax(Integer.parseInt(etNumFiles.getText().toString()));

            tvDisplayPercent.setVisibility(View.VISIBLE);
            tvDisplayPercent.setVisibility(View.VISIBLE);
            tvDisplayProgress.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Integer... integers) {
            for (int i = 1; i < integers[0] + 1; i++) {
                if (isCancelled()) break;
                try {
                    Thread.sleep(integers[1]);
                    publishProgress(i, integers[0]);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return "Done processing " + integers[0] + " files";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if (weakTvDisplay != null) {
                final TextView txt = (TextView) weakTvDisplay.get();
                txt.setText("Simulation has started...");
            }

            float current = values[0];
            float target = values[1];
            float percents = (current / target * 100);
            int progress = (int) percents;

            progressBar.setProgress(progress);
            tvDisplayPercent.setText(progress + " %");
            tvDisplayProgress.setText(values[0] + " / " + values[1] + " files processed");
        }

        @Override
        protected void onCancelled() {
            if (weakTvDisplay != null) {
                final TextView txt = (TextView) weakTvDisplay.get();
                txt.setText("Canceled");
            }
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(String s) {
            progressBar.setVisibility(View.INVISIBLE);
            tvDisplayPercent.setVisibility(View.INVISIBLE);
            tvDisplayProgress.setText(s);
            btnGo.setEnabled(true);
        }
    }

    // Wrong way
    /*public void goClick(View view) {
        progressBar.setVisibility(View.VISIBLE);
        tvDisplayProgress.setVisibility(View.VISIBLE);
        int numFiles = Integer.parseInt(etNumFiles.getText().toString());

        tvDisplayProgress.setText("Simulation has started...");

        for (int i = 0; i < numFiles; i++) {
            try{
                Thread.sleep(2000);
                tvDisplayProgress.setText((i + 1) + " files processed");
            } catch (InterruptedException e) {
                // What a Terrible Failure
                Log.wtf("MainActivity", e.getMessage(), e);
            }
        }

        progressBar.setVisibility(View.INVISIBLE);
        tvDisplayProgress.setText("Done");
    }*/

}
