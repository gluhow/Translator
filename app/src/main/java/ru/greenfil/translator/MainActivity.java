package ru.greenfil.translator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView textOut;
    ITranslator curTranslator;
    MultiAutoCompleteTextView inputText;
    Spinner SourceSpinner;
    ArrayAdapter<ILanguage> sourceAdapter;
    Spinner TargetSpinner;
    ArrayAdapter<ILanguage> targetAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textOut=(TextView)findViewById(R.id.textView);
        textOut.setText("");
        inputText=(MultiAutoCompleteTextView)findViewById(R.id.InputText);
        inputText.setText("");
        curTranslator = new YaTranslator();
        SourceSpinner=(Spinner)findViewById(R.id.SourceSpinner);
        sourceAdapter = new ArrayAdapter<ILanguage>(this, android.R.layout.simple_spinner_item,
                curTranslator.GetSourceLanguageList());
        sourceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        SourceSpinner.setAdapter(sourceAdapter);

        TargetSpinner=(Spinner)findViewById(R.id.TargetSpinner);
        targetAdapter = new ArrayAdapter<ILanguage>(this, android.R.layout.simple_spinner_item,
                curTranslator.GetTargetLanguageList());
        targetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        TargetSpinner.setAdapter(targetAdapter);
        FormUpdate();
    }

    protected void FormUpdate(){
        SourceSpinner.setSelection(sourceAdapter.getPosition(curTranslator.GetSourceLang()));
        TargetSpinner.setSelection(targetAdapter.getPosition(curTranslator.GetTargetLang()));
    }

    public void OnSwapLanguage(View view) {
        curTranslator.SwapLang();
        FormUpdate();
    }
}
