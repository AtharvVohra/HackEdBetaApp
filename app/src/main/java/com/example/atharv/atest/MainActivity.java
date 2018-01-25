package com.example.atharv.atest;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.PermissionRequest;
import android.widget.Button;
import android.widget.EditText;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import android.speech.RecognizerIntent;
import android.widget.TextView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.single.PermissionListener;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private TextToSpeech tts;
    private Button speak;
    public String text;
    private TextView display;
    private String jsonresult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Dexter.withActivity(this) //Permissions
                .withPermission(Manifest.permission.RECORD_AUDIO)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(com.karumi.dexter.listener.PermissionRequest permission, PermissionToken token) {

                    }
                }).check();


        tts = new TextToSpeech(this, this);

        display = (TextView)findViewById((R.id.display));



        //TTS AND STT ON START OF ACTIVITY HANDLER AHHAHAHAH
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                speakOutNow();  //speak after 1000ms
            }
        }, 1000);
        final Handler handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                startVInputv2(); //INITIAL CALL
            }
        }, 3000);

        new HttpAsyncTask().execute("http://food2fork.com/api/get.json");  //IMPLICITLY CALLING

    }

    //TESTING JSON
    public static String GET(String url){
        InputStream inputStream = null;
        String urlresult = "";
        /*List<NameValuePair> params = new LinkedList<NameValuePair>();
        params.add(new BasicNameValuePair("key", "bef34a812e45a1aa2f63a3f52aebfb67"));
        params.add(new BasicNameValuePair("rId", "chicken stir fry"));
        String paramString = URLEncodedUtils.format(params, "utf-8");
        url +=paramString;*/
        try {

            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if(inputStream != null)
                urlresult = convertInputStreamToString(inputStream);
            else
                urlresult = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return urlresult;
        //is the result returned a pointer to a string array or a solid character array? Could br used for detailed string manipulation

    }

    // convert inputstream to String
    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String urlresult = ""; //why init? why not init?
        //how to handle error upon deletion?
        while((line = bufferedReader.readLine()) != null)
            //set string to readable?
            urlresult += line;

        inputStream.close();

        return urlresult;
    }
    //END TEST

    //is there any =other data that needs to be parsed?
    //function doesn't work with pointer even after dereference
    //REAL HIDDEN VOICE REC WOOOOOOOOOOOHOOOOOOOOO... fixed by creating new section header for function, works in it's own namespace, needs global parse
    private void startVInputv2() {
        SpeechRecognizer speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle data) {
                //RETRIEVED INFO FROM SPEECH
                ArrayList<String> result = data.getStringArrayList("results_recognition");
                display.setText(result.get(0));

                //COMPARISONS THAT'LL CHANGE WITH API


                if (result.contains("temp")) { //TODO fix ocmparison string
                                                //TODO UPDATE - need to convert back to array form because java strings are messing with the source modifications

                    Intent intent = new Intent(MainActivity.this, DirectoryActivity.class);
                    startActivity(intent);
                    //onCraate public Bundle bundle(this, ASyncTask2.Task.task);
                } else {
                    speakOutNow();
                    final Handler handler2 = new Handler();
                    handler2.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startVInputv2(); //RECURSION
                        }
                    }, 3000);
                }
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        }); //TODO create new method for secondary parse protocols
        //onNewIntent(Intent intent);
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        speechRecognizer.startListening(intent); //START LISTENING - maybe start the parallel string task here as well (async)
                                                    // would that disrupt RAM flow for intensive API processing - disjoint threads?
    }
    //Test cases >> results?
    //TODO try various string parses that are out of bounds or simply just too weird. Report to new version post VC update
    @Override
    public void onInit(int text) { //TTS Errorchecking and initialization
        if (text ==TextToSpeech.SUCCESS) {
            int language = tts.setLanguage(Locale.ENGLISH); //YOU CAN CHANGE YOUR LANGUAGE
            if (language ==TextToSpeech.LANG_MISSING_DATA || language ==TextToSpeech.LANG_NOT_SUPPORTED){
                speak.setEnabled(true);
                //TODO format issues, new array for list or formatted file recall?
                speakOutNow(); //Remove this?
            }
            else {
            }
        }
        else{
        }
    }
    // create new set list for the speak function ... perhaps it'll work for some advance form dictionary, but how to replicate?
    private void speakOutNow (){ //TTS speak function
        //STRING SETTING, CHANGE WITH API
       //String text = getTextToSpeak.getText().toString();
        text = "What would you like to cook today?";
        tts.setSpeechRate((float) 0.7);
        tts.setPitch((float) 0.7);
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    //MORE JSON STUFF
    //Also what the fuck is happening with the async threading, what kind of round robin tasks is it creating, if round robin at all?

    private class HttpAsyncTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... urls) {
            return GET(urls[0]);
        } //OOPS
        @Override
        protected void onPostExecute(String urlresult){
            try{
                JSONObject json = new JSONObject(urlresult);
                display.setText(json.toString(1));
            }catch (JSONException e){
                Log.e("APP", "Something is wrong");
            }

        }
    }
}

