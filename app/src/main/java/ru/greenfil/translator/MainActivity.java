package ru.greenfil.translator;

import android.icu.text.SelectFormat;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView textOut;
    ITranslator mytranslator;
    MultiAutoCompleteTextView textIn;
    Spinner SourceSpinner;
    ArrayAdapter<ILanguage> sourceAdapter;
    Spinner TargetSpinner;
    ArrayAdapter<ILanguage> targetAdapter;
    ArrayList<ILanguage> languageList;
    Button translateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textOut=(TextView)findViewById(R.id.outputText);
        textOut.setText("");
        textIn=(MultiAutoCompleteTextView)findViewById(R.id.InputText);
        textIn.setText("Hello World!!!");
        languageList=new ArrayList<ILanguage>();
        languageList.add(new TLanguage("English","en"));
        languageList.add(new TLanguage("Русский","ru"));
        mytranslator = new YaTranslator();
        SourceSpinner=(Spinner)findViewById(R.id.SourceSpinner);
        sourceAdapter = new ArrayAdapter<ILanguage>(this, android.R.layout.simple_spinner_item,
                languageList);
        sourceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        SourceSpinner.setAdapter(sourceAdapter);

        TargetSpinner=(Spinner)findViewById(R.id.TargetSpinner);
        targetAdapter = new ArrayAdapter<ILanguage>(this, android.R.layout.simple_spinner_item,
                languageList);
        targetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        TargetSpinner.setAdapter(targetAdapter);

        translateButton=(Button)findViewById(R.id.translateButton);
        translateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetTranslate getTranslate=new GetTranslate();
                getTranslate.execute(mytranslator);
            /*
            textOut.setText(mytranslator.Translate());*/
            }
        });
        FormUpdate();
    }

    private class GetTranslate
        extends AsyncTask<ITranslator, Void, String>{

        private ILanguage sourceLang;
        private ILanguage targetLang;
        private String text;

        @Override
        protected void onPreExecute() {
            sourceLang=languageList.get(0);
            targetLang=languageList.get(1);
            text=textIn.getText().toString();
        }

        @Override
        protected String doInBackground(ITranslator... params) {
            return params[0].Translate(text, sourceLang, targetLang);
        }

        @Override
        protected void onPostExecute(String s) {
            textOut.setText(s);
        }
    }

    protected void FormUpdate(){
        /*SourceSpinner.setSelection(sourceAdapter.getPosition(curTranslator.GetSourceLang()));
        TargetSpinner.setSelection(targetAdapter.getPosition(curTranslator.GetTargetLang()));*/
    }

    public void OnSwapLanguage(View view) {
        FormUpdate();
    }
}
