package ru.greenfil.translator;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TextView textOut;
    ITranslator mytranslator;
    EditText textIn;
    Spinner SourceSpinner;
    ArrayAdapter<ILanguage> langAdapter;
    Spinner TargetSpinner;
    ArrayList<ILanguage> languageList;
    GetTranslate getTranslate=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textOut=(TextView)findViewById(R.id.outputText);
        textOut.setText("");
        textIn=(EditText)findViewById(R.id.InputText);
        textIn.setText("Hello World!!!");
        textIn.addTextChangedListener(new sourceTextChange());

        languageList=new ArrayList<ILanguage>();
        languageList.add(new TLanguage("English","en"));
        languageList.add(new TLanguage("Русский","ru"));

        mytranslator = new YaTranslator();
        langAdapter = new ArrayAdapter<ILanguage>(this, android.R.layout.simple_spinner_item,
                languageList);
        langAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);

        SourceSpinner=(Spinner)findViewById(R.id.SourceSpinner);
        SourceSpinner.setAdapter(langAdapter);

        TargetSpinner=(Spinner)findViewById(R.id.TargetSpinner);
        TargetSpinner.setAdapter(langAdapter);
        TargetSpinner.setSelection(1);
    }

    private class sourceTextChange implements TextWatcher{
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (getTranslate!=null) {
                getTranslate.cancel(true);
            };
            getTranslate=new GetTranslate();
            getTranslate.execute(mytranslator);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    private class GetTranslate
        extends AsyncTask<ITranslator, Void, String>{

        private ILanguage sourceLang;
        private ILanguage targetLang;
        private String text;

        @Override
        protected void onPreExecute() {
            sourceLang= (ILanguage) SourceSpinner.getSelectedItem();
            targetLang= (ILanguage) TargetSpinner.getSelectedItem();
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

    public void OnSwapLanguage(View view) {
        int tempLang=SourceSpinner.getSelectedItemPosition();
        SourceSpinner.setSelection(TargetSpinner.getSelectedItemPosition());
        TargetSpinner.setSelection(tempLang);
    }
}
