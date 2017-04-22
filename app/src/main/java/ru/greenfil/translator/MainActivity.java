package ru.greenfil.translator;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

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
    GetTranslate getTranslate = null;
    Button buttonSwap;
    tWordList historyList;
    tWordList favoritList;
    FloatingActionButton favoritesButton;
    DBHelper dbHelper;
    BottomNavigationView navigation;
    ListView wordListView;
    TOneWord fCurrentWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textOut = (TextView) findViewById(R.id.outputText);
        textOut.setText("");
        textIn = (EditText) findViewById(R.id.InputText);
        textIn.setText("Hello World!!!");
        textIn.addTextChangedListener(new sourceTextChange());

        languageList = new tLangList();
        languageList.add(new TLanguage("English", "en"));
        languageList.add(new TLanguage("Русский", "ru"));
        buttonSwap = (Button) findViewById(R.id.buttonSwap);

        mytranslator = new YaTranslator();
        langAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                languageList);
        langAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);

        OnLangChange langChange = new OnLangChange();
        SourceSpinner = (Spinner) findViewById(R.id.SourceSpinner);
        SourceSpinner.setAdapter(langAdapter);
        SourceSpinner.setOnItemSelectedListener(langChange);

        TargetSpinner = (Spinner) findViewById(R.id.TargetSpinner);
        TargetSpinner.setAdapter(langAdapter);
        TargetSpinner.setSelection(1);
        TargetSpinner.setOnItemSelectedListener(langChange);

        favoritesButton = (FloatingActionButton) findViewById(R.id.favoritesButton);

        historyList = new tWordList();
        favoritList = new tWordList();

        dbHelper = new DBHelper(this);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        wordListView = (ListView) findViewById(R.id.wordListView);
        //wordListView.setOnItemSelectedListener(new wordListener());]
        wordListView.setOnItemClickListener(new wordListener());
        LoadData();
    }

    TOneWord GetCurrentWord() {
        return fCurrentWord;
    }

    void SetCurrentWord(TOneWord word){
        fCurrentWord=word;
        if (word!=null){
            if (!word.getSourceText().equals(textIn.getText().toString().trim()))            {
                textIn.setText(word.getSourceText());
            }
            if (!SourceSpinner.getSelectedItem().equals(word.getSourceLang())){
                SourceSpinner.setSelection(languageList.indexOf(word.getSourceLang()));
            }
            if (!TargetSpinner.getSelectedItem().equals(word.getTargetLang())){
                TargetSpinner.setSelection(languageList.indexOf(word.getTargetLang()));
            }
            textOut.setText(word.getTargetText());
            UpdateFavoritButton();
        }
    }

    void UpdateFavoritButton() {
        TOneWord CurrentWord = GetCurrentWord();
        if (favoritList.contains(CurrentWord)) {
            favoritesButton.setImageResource(R.drawable.favorite);
        } else favoritesButton.setImageResource(R.drawable.nonfavorite);
    }

    public void addFavorites(View view) {
        TOneWord CurrentWord = GetCurrentWord();
        if (CurrentWord!=null)
        {
            if (favoritList.contains(CurrentWord)) {
                RemoveFromFavorite(CurrentWord);
            } else {
                SaveToFavorite(CurrentWord);
            }
            UpdateFavoritButton();
        }
    }

    private void SaveToFavorite(TOneWord currentWord) {
        if (!favoritList.contains(currentWord)) {
            favoritList.add(currentWord);
            SQLiteDatabase database = dbHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(dbHelper.KEY_SOURCE_LANG, currentWord.getSourceLang().GetUI());
            contentValues.put(dbHelper.KEY_TARGET_LANG, currentWord.getTargetLang().GetUI());
            contentValues.put(dbHelper.KEY_SOURCE_TEXT, currentWord.getSourceText());
            contentValues.put(dbHelper.KEY_TARGET_TEXT, currentWord.getTargetText());

            database.insert(dbHelper.TABLE_FAVORITES, null, contentValues);
            dbHelper.close();
        }
    }

    private void RemoveFromFavorite(TOneWord currentWord) {
        if (favoritList.contains(currentWord)) {
            favoritList.remove(currentWord);
            SQLiteDatabase database = dbHelper.getWritableDatabase();
            database.execSQL(
                    String.format(
                            "DELETE FROM %s WHERE %s='%s' AND %s='%s' AND %s='%s'",
                            dbHelper.TABLE_FAVORITES,
                            dbHelper.KEY_SOURCE_LANG, currentWord.getSourceLang().GetUI(),
                            dbHelper.KEY_TARGET_LANG, currentWord.getTargetLang().GetUI(),
                            dbHelper.KEY_SOURCE_TEXT, currentWord.getSourceText())
            );
            dbHelper.close();
        }
    }

    void SetVisibleAll(int vis) {
        //Не надо сюда смотреть! Просто я не разобрался с фрагментами и передачей данных между ними
        //поэтому я тупо скрываю и показываю нужные элементы
        textIn.setVisibility(vis);
        textOut.setVisibility(vis);
        SourceSpinner.setVisibility(vis);
        TargetSpinner.setVisibility(vis);
        buttonSwap.setVisibility(vis);
        favoritesButton.setVisibility(vis);

        wordListView.setVisibility(-vis);
    }

    private class sourceTextChange implements TextWatcher {
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

    private void translateNow() {
        if (getTranslate != null) {
            getTranslate.cancel(true);
        }
        getTranslate = new GetTranslate();
        getTranslate.execute(mytranslator);
    }


    private class GetTranslate
            extends AsyncTask<ITranslator, Void, String> {

        TOneWord CurrentWord;
        final int some_error = 1;
        int errCode = some_error;


        @Override
        protected void onPreExecute() {
            ILanguage sourceLang = (ILanguage) SourceSpinner.getSelectedItem();
            ILanguage targetLang = (ILanguage) TargetSpinner.getSelectedItem();
            String text = textIn.getText().toString().trim();

            CurrentWord = new TOneWord(sourceLang, targetLang, text);

            int iWord = historyList.indexOf(CurrentWord);
            if (iWord > 0) {
                CurrentWord = historyList.get(iWord);
            }
            else {
                iWord = favoritList.indexOf(CurrentWord);
                if (iWord > 0)
                    CurrentWord = favoritList.get(iWord);
            }
            if (CurrentWord.getTargetText() != null) {
                errCode = 0;
            }
            SetCurrentWord(null);
        }

        @Override
        protected String doInBackground(ITranslator... params) {
            if (CurrentWord.getTargetText().equals("")) {
                try {
                    Thread.sleep(1000); //Задержка перед переводом для сохранения траффика
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!isCancelled()) {
                    ITranslator translator=params[0];
                    String res = translator.Translate(
                            CurrentWord.getSourceText(),
                            CurrentWord.getSourceLang(),
                            CurrentWord.getTargetLang());
                    if (translator.ErrCode() == 0) {
                        errCode = 0;
                        return res;
                    } else return "";
                } else return "";
            } else return CurrentWord.getTargetText();
        }

        @Override
        protected void onPostExecute(String s) {
            if (errCode==0)
            {
                CurrentWord.setTargetText(s);
                SetCurrentWord(CurrentWord);
                saveToHistory(CurrentWord);
            }
            else
            {
                textOut.setText(/*GetErrorCaption(errcode)*/ getString(R.string.someError));
            }
        }
    }

    private void saveToHistory(TOneWord currentWord) {
        if (!historyList.contains(currentWord)) {
            historyList.add(currentWord);
            SQLiteDatabase database = dbHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(dbHelper.KEY_SOURCE_LANG, currentWord.getSourceLang().GetUI());
            contentValues.put(dbHelper.KEY_TARGET_LANG, currentWord.getTargetLang().GetUI());
            contentValues.put(dbHelper.KEY_SOURCE_TEXT, currentWord.getSourceText());
            contentValues.put(dbHelper.KEY_TARGET_TEXT, currentWord.getTargetText());

            database.insert(dbHelper.TABLE_HISTORY, null, contentValues);
            dbHelper.close();
        }
    }

    public void OnSwapLanguage(View view) {
        int tempLang = SourceSpinner.getSelectedItemPosition();
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
    private class wordListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            SetCurrentWord((TOneWord)parent.getItemAtPosition(position));
            navigation.setSelectedItemId(R.id.action_home);
        }
    }

    void LoadData() {
        class tAsyncLoad extends AsyncTask<tLangList, Void, tWordList> {
            String tableName;

            @Override
            protected tWordList doInBackground(tLangList... params) {
                tWordList historyList = new tWordList();

                SQLiteDatabase database = dbHelper.getReadableDatabase();
                Cursor cursor = database.query(tableName, null, null, null, null, null, null);

                if (cursor.moveToFirst()) {
                    int slIndex = cursor.getColumnIndex(DBHelper.KEY_SOURCE_LANG);
                    int tlIndex = cursor.getColumnIndex(DBHelper.KEY_TARGET_LANG);
                    int stIndex = cursor.getColumnIndex(DBHelper.KEY_SOURCE_TEXT);
                    int ttIndex = cursor.getColumnIndex(DBHelper.KEY_TARGET_TEXT);

                    do {
                        int langIndex = params[0].indexOf(new TLanguage("", cursor.getString(slIndex)));
                        if (langIndex >= 0) {
                            ILanguage sourceLang = params[0].get(langIndex);
                            langIndex = params[0].indexOf(new TLanguage("", cursor.getString(tlIndex)));
                            if (langIndex >= 0) {
                                ILanguage targetLang = params[0].get(langIndex);
                                if ((sourceLang != null) & (targetLang != null)) {
                                    TOneWord NextWord = new TOneWord(sourceLang, targetLang,
                                            cursor.getString(stIndex));
                                    NextWord.setTargetText(cursor.getString(ttIndex));
                                    historyList.add(NextWord);
                                }
                            }

                        }
                    } while (cursor.moveToNext());

                }
                dbHelper.close();
                return historyList;
            }
        }
        class tAsyncHistoryLoad extends tAsyncLoad {
            @Override
            protected void onPreExecute() {
                tableName = dbHelper.TABLE_HISTORY;
            }

            @Override
            protected void onPostExecute(tWordList LoadList) {
                historyList = LoadList;
            }
        }
        class tAsyncFavoriteLoad extends tAsyncLoad {
            @Override
            protected void onPreExecute() {
                tableName = dbHelper.TABLE_FAVORITES;
            }

            @Override
            protected void onPostExecute(tWordList tOneWords) {
                favoritList = tOneWords;
            }
        }

        tAsyncLoad asyncLoad = new tAsyncHistoryLoad();
        asyncLoad.execute(languageList);

        asyncLoad = new tAsyncFavoriteLoad();
        asyncLoad.execute(languageList);

    }

    tOneWordAdapter GetAdapter(@NonNull List<TOneWord> list){
        return (new tOneWordAdapter(this, R.layout.word_item, list, favoritList));
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_home:
                    SetVisibleAll(1);
                    return true;
                case R.id.action_history:
                    SetVisibleAll(-1);
                    wordListView.setAdapter(GetAdapter(historyList));
                    return true;
                case R.id.action_favorites:
                    SetVisibleAll(-1);
                    wordListView.setAdapter(GetAdapter(favoritList));
                    return true;
            }
            return false;
        }
    };
}
