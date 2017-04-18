package ru.greenfil.translator;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

class tWordList extends ArrayList<TOneWord>{};
class tLangList extends ArrayList<ILanguage>{};

public class MainActivity extends AppCompatActivity {

    TextView textOut;
    ITranslator mytranslator;
    EditText textIn;
    Spinner SourceSpinner;
    ArrayAdapter<ILanguage> langAdapter;
    Spinner TargetSpinner;
    tLangList languageList;
    GetTranslate getTranslate=null;
    Button buttonSwap;
    tWordList historyList;
    tWordList favoritList;
    FloatingActionButton favoritesButton;
    DBHelper dbHelper;

    //OnLangChange LangChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textOut=(TextView)findViewById(R.id.outputText);
        textOut.setText("");
        textIn=(EditText)findViewById(R.id.InputText);
        textIn.setText("Hello World!!!");
        textIn.addTextChangedListener(new sourceTextChange());

        languageList=new tLangList();
        languageList.add(new TLanguage("English","en"));
        languageList.add(new TLanguage("Русский","ru"));
        buttonSwap=(Button)findViewById(R.id.buttonSwap);

        mytranslator = new YaTranslator();
        langAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                languageList);
        langAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);

        OnLangChange langChange = new OnLangChange();
        SourceSpinner=(Spinner)findViewById(R.id.SourceSpinner);
        SourceSpinner.setAdapter(langAdapter);
        SourceSpinner.setOnItemSelectedListener(langChange);

        TargetSpinner=(Spinner)findViewById(R.id.TargetSpinner);
        TargetSpinner.setAdapter(langAdapter);
        TargetSpinner.setSelection(1);
        TargetSpinner.setOnItemSelectedListener(langChange);

        favoritesButton=(FloatingActionButton)findViewById(R.id.favoritesButton);

        historyList=new tWordList();
        favoritList=new tWordList();

        dbHelper=new DBHelper(this);
        LoadData();
    }

    TOneWord GetCurrentWord(){
        ILanguage sourceLang= (ILanguage) SourceSpinner.getSelectedItem();
        ILanguage targetLang= (ILanguage) TargetSpinner.getSelectedItem();
        String text=textIn.getText().toString();
        return new TOneWord(sourceLang, targetLang, text);
    }

    void UpdateFavoritButton(){
        TOneWord CurrentWord=GetCurrentWord();
        if (favoritList.contains(CurrentWord)) {
            favoritesButton.setImageResource(R.drawable.favorite);
        }
        else favoritesButton.setImageResource(R.drawable.nonfavorite);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void addFavorites(View view) {
        TOneWord CurrentWord=GetCurrentWord();
        if (favoritList.contains(CurrentWord)) {
            favoritList.remove(CurrentWord);
            favoritesButton.setImageResource(R.drawable.nonfavorite);
        }
        else {
            CurrentWord.setTargetText(textOut.getText().toString());
            favoritList.add(CurrentWord);
            favoritesButton.setImageResource(R.drawable.favorite);
        }
    }

    public void historyClick(MenuItem item) {
        Intent history=new Intent(MainActivity.this, History.class);
        startActivity(history);
    }

    private class sourceTextChange implements TextWatcher{
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            translateNow();
            UpdateFavoritButton();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
    private void translateNow(){
        if (getTranslate!=null) {
            getTranslate.cancel(true);
        }
        getTranslate=new GetTranslate();
        getTranslate.execute(mytranslator);
    }



    private class GetTranslate
        extends AsyncTask<ITranslator, Void, String>{

        TOneWord CurrentWord;


        @Override
        protected void onPreExecute() {
            CurrentWord=GetCurrentWord();
            int iWord=historyList.indexOf(CurrentWord);
            if (iWord>0)
                CurrentWord=historyList.get(iWord);
            else
            {
                iWord=favoritList.indexOf(CurrentWord);
                if (iWord>0)
                    CurrentWord=favoritList.get(iWord);
            }

        }

        @Override
        protected String doInBackground(ITranslator... params) {
            if (CurrentWord.getTargetText().equals(""))
            {
                try {
                    Thread.sleep(1000); //Задержка перед переводом для сохранения траффика
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!isCancelled()) {
                String res=params[0].Translate(
                        CurrentWord.getSourceText(),
                        CurrentWord.getSourceLang(),
                        CurrentWord.getTargetLang());
                    if (params[0].ErrCode()==0) return res;
                        else return "";
                }
                else return "";
            }
            else return CurrentWord.getTargetText();
        }

        @Override
        protected void onPostExecute(String s) {
            textOut.setText(s);
            if ((CurrentWord.getTargetText().equals("")) & (s!=""))
            {
                CurrentWord.setTargetText(s);
                historyList.add(CurrentWord);
                saveToHistory(CurrentWord);
            }
        }
    }

    private void saveToHistory(TOneWord currentWord) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(dbHelper.KEY_SOURCE_LANG, currentWord.getSourceLang().GetUI());
        contentValues.put(dbHelper.KEY_TARGET_LANG, currentWord.getTargetLang().GetUI());
        contentValues.put(dbHelper.KEY_SOURCE_TEXT, currentWord.getSourceText());
        contentValues.put(dbHelper.KEY_TARGET_TEXT, currentWord.getTargetText());
        database.insert(dbHelper.TABLE_HISTORY, null, contentValues);
    }

    public void OnSwapLanguage(View view) {
        int tempLang=SourceSpinner.getSelectedItemPosition();
        SourceSpinner.setSelection(TargetSpinner.getSelectedItemPosition());
        TargetSpinner.setSelection(tempLang);
    }

    private class OnLangChange implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            translateNow();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    void LoadData(){
         tAsyncLoad asyncLoad=new tAsyncLoad();
         asyncLoad.execute(languageList);
    }
    class tAsyncLoad extends AsyncTask<tLangList, Void, tWordList> {

        @Override
        protected tWordList doInBackground(tLangList... params) {
            SQLiteDatabase database= dbHelper.getWritableDatabase();
            tWordList historyList=new tWordList();
            Cursor cursor = database.query(DBHelper.TABLE_HISTORY, null, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                     int slIndex = cursor.getColumnIndex(DBHelper.KEY_SOURCE_LANG);
                     int tlIndex = cursor.getColumnIndex(DBHelper.KEY_TARGET_LANG);
                     int stIndex = cursor.getColumnIndex(DBHelper.KEY_SOURCE_TEXT);
                     int ttIndex = cursor.getColumnIndex(DBHelper.KEY_TARGET_TEXT);
                     do {
                         int langIndex=params[0].indexOf(new TLanguage("", cursor.getString(slIndex)));
                         if (langIndex>=0) {
                             ILanguage sourceLang = params[0].get(langIndex);
                             langIndex = params[0].indexOf(new TLanguage("", cursor.getString(tlIndex)));
                             if (langIndex >= 0) {
                                 ILanguage targetLang = params[0].get(langIndex);
                                 if ((sourceLang!=null)&&(targetLang!=null))
                                 {
                                     TOneWord NextWord = new TOneWord(sourceLang, targetLang,
                                             cursor.getString(stIndex));
                                     NextWord.setTargetText(cursor.getString(ttIndex));
                                     historyList.add(NextWord);
                                 }
                             }

                         }
                     } while (cursor.moveToNext());

            }
            return historyList;
        }

        @Override
        protected void onPostExecute(tWordList LoadList) {
            historyList =LoadList;
        }
    }
}
