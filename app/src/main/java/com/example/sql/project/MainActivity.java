package com.example.sql.project;

        import java.util.Timer;
        import java.util.TimerTask;

        import android.app.Activity;
        import android.hardware.Sensor;
        import android.hardware.SensorEvent;
        import android.hardware.SensorEventListener;
        import android.hardware.SensorManager;
        import android.os.Bundle;
        import android.widget.TextView;

public class MainActivity extends Activity {
    final double delta = 0.1072664626;
    TextView tvSteady;
    boolean first = true;
    TextView tvText;
    SensorManager sensorManager;
    Sensor sensorAccel;
    Sensor sensorLinAccel;
    Sensor sensorRotation;

    StringBuilder sb = new StringBuilder();

    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvText = (TextView) findViewById(R.id.tvText);
        tvSteady = (TextView) findViewById(R.id.steady_position);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorRotation = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);


    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(listener, sensorRotation,
                SensorManager.SENSOR_DELAY_NORMAL);

        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showInfo();
                    }
                });
            }
        };
        timer.schedule(task, 0, 400);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(listener);
        timer.cancel();
    }

    String format(float values[]) {
        return String.format("%1$.4f\t\t%2$.4f\t\t%3$.4f", values[0], values[1],
                values[2]);
    }

    void showInfo() {
        sb.setLength(0);
        sb.append("Accelerometer: " + format(valuesAccel))
                .append("\n\nAccel motion: " + format(valuesAccelMotion))
                .append("\nAccel gravity : " + format(valuesAccelGravity))
                .append("\n\nLin accel : " + format(valuesLinAccel))
                .append("\nGravity : " + format(valuesGravity));
        tvText.setText(sb+"\n");
        if (!isClose(steady, valuesGravity)) {
            tvSteady.setText(" " + steady[0] + steady[1] + steady[2]);
        }

    }

    private boolean isClose(float[] a, float[] b){
        boolean f = true;
        for (int i = 0; i < 3; i++){
            f = f && (Math.abs(a[i] - b[i]) < delta);
        }
        if (!f) {
            steady = valuesGravity;
            return false;
        } else {
            return true;
        }
    }

    float[] steady = new float[3];
    float[] valuesAccel = new float[3];
    float[] valuesAccelMotion = new float[3];
    float[] valuesAccelGravity = new float[3];
    float[] valuesLinAccel = new float[3];
    float[] valuesGravity = new float[3];

    SensorEventListener listener = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {

            switch (event.sensor.getType()) {

                case Sensor.TYPE_ROTATION_VECTOR:
//                    if (first){
//                        steady = event.values;
//                        first = false;
//                    }
                    for (int i = 0; i < 3; i++) {
                        valuesGravity[i] = event.values[i];
                    }
                    break;
            }

        }

    };

}