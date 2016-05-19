package edu.uw.group2.locationtagger;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.vuforia.Vuforia;

public class AugmentedReality extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_augmented_reality);

        new InitVuforiaTask();
    }


    /**
     * Created by tbauer516 on 5/18/2016.
     * Class used to initialize the Vuforia object when using AR
     */
    public class InitVuforiaTask extends AsyncTask<Void, Integer, Boolean> {

        private int mProgressValue = -1;

        @Override
        protected Boolean doInBackground(Void... params) {

            // set initial params, including the API key for param 3
            Vuforia.setInitParameters(AugmentedReality.this, /*Vuforia flags here*/0, "ASdjR9n/////AAAAAawQe95/6kEQpA+5QJEC0X8LjbGOvMtiCzQYC/tbAmdn7owMoYy6GMszZ7N+uhEm+FjO1D8sDlBhAqURPfrRl14TGkoRWOclNVXvnpQIZ7rwJF7870/xZTTl/wl/ek5mV9jWCHqrbOoDb5d24O13Bc3bWmQW9IJ7z+FncZ1GjujfOYyuye1XpfTZ0VaPCrvnJPoOuNYqV0KONCYTw07NnoW9dQDvDWjcucZXJik+GDs9HBP7PGFXXEFBIpZsgneARk/V6vg3wotNCbJ5BxgV8RFBWnljaE0iKAYMQT3ELUBqLNT3A+XRTL4FNm/E1EDNZ3yh4ZH/JjqD1XiQgszLVa37/7CoO6weYh0KBTb9lI3w");


            do {
                mProgressValue = Vuforia.init();
                publishProgress(mProgressValue);
            } while (!isCancelled() && mProgressValue >= 0 && mProgressValue < 100);

            return mProgressValue > 0;
        }

        //    TODO: Implement this method to update to the UI thread
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }
}