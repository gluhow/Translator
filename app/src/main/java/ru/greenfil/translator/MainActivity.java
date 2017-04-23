package ru.greenfil.translator;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class tWordList extends ArrayList<TOneWord>{};
class tLangList extends ArrayList<ILanguage>{};

public class MainActivity extends AppCompatActivity {

    ITranslator mytranslator;

    Spinner sourceSpinner;
    Button buttonSwap;
    Spinner targetSpinner;
    EditText sourceText;
    TextView targetText;
    FloatingActionButton favoritesButton;
    BottomNavigationView navigation;
    TextView copyright;
    ListView wordListView;

    tLangList languageList;
    GetTranslate getTranslate = null;
    tWordList historyList;
    tWordList favoritList;
    DBHelper dbHelper;
    TOneWord fCurrentWord;
    public static final String APP_PREFERENCES = "translator.conf";
    public static final String APP_PREFERENCES_SOURCE_LANG = "sourceLang";
    public static final String APP_PREFERENCES_TARGET_LANG = "targetLang";
    private SharedPreferences mSettings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        targetText = (TextView) findViewById(R.id.outputText);
        targetText.setText("");
        sourceText = (EditText) findViewById(R.id.InputText);
        sourceText.setText("Hello World!!!");
        sourceText.addTextChangedListener(new sourceTextChange());

        languageList = new tLangList();
        LoadLangList(languageList);
        buttonSwap = (Button) findViewById(R.id.buttonSwap);

        mytranslator = new YaTranslator();
        ArrayAdapter<ILanguage> langAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                languageList);
        langAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        String sourceLangUI=mSettings.getString(APP_PREFERENCES_SOURCE_LANG, "en");
        String targetLangUI=mSettings.getString(APP_PREFERENCES_TARGET_LANG, "ru");

        OnLangChange langChange = new OnLangChange();
        sourceSpinner = (Spinner) findViewById(R.id.SourceSpinner);
        sourceSpinner.setAdapter(langAdapter);
        sourceSpinner.setSelection(languageList.indexOf(new TLanguage("", sourceLangUI)));
        sourceSpinner.setOnItemSelectedListener(langChange);

        targetSpinner = (Spinner) findViewById(R.id.TargetSpinner);
        targetSpinner.setAdapter(langAdapter);
        targetSpinner.setSelection(languageList.indexOf(new TLanguage("", targetLangUI)));
        targetSpinner.setOnItemSelectedListener(langChange);

        favoritesButton = (FloatingActionButton) findViewById(R.id.favoritesButton);
        copyright = (TextView) findViewById(R.id.copyright);

        historyList = new tWordList();
        favoritList = new tWordList();

        dbHelper = new DBHelper(this);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        wordListView = (ListView) findViewById(R.id.wordListView);
        wordListView.setOnItemClickListener(new wordListener());
        LoadData();
    }

    void LoadLangList(tLangList langList) {
        String[] langUI=getResources().getStringArray(R.array.LangUI);
        String[] langCaption=getResources().getStringArray(R.array.LangCaption);
        int ArrLength=Math.min(langUI.length, langCaption.length);
        for (int iLang=0; iLang<ArrLength; iLang++){
            langList.add(new TLanguage(langCaption[iLang], langUI[iLang]));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor=mSettings.edit();
        editor.putString(APP_PREFERENCES_SOURCE_LANG,
                ((ILanguage) sourceSpinner.getSelectedItem()).GetUI());
        editor.putString(APP_PREFERENCES_TARGET_LANG,
                ((ILanguage) targetSpinner.getSelectedItem()).GetUI());
        editor.apply();
    }

    TOneWord GetCurrentWord() {
        return fCurrentWord;
    }

    void SetCurrentWord(TOneWord word){
        fCurrentWord=word;
        if (word!=null){
            if (!word.getSourceText().equals(sourceText.getText().toString().trim()))            {
                sourceText.setText(word.getSourceText());
            }
            if (!sourceSpinner.getSelectedItem().equals(word.getSourceLang())){
                sourceSpinner.setSelection(languageList.indexOf(word.getSourceLang()));
            }
            if (!targetSpinner.getSelectedItem().equals(word.getTargetLang())){
                targetSpinner.setSelection(languageList.indexOf(word.getTargetLang()));
            }
            targetText.setText(word.getTargetText());
            UpdateFavoritButton();
        }
        else {
            targetText.setText("");
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
        sourceText.setVisibility(vis);
        targetText.setVisibility(vis);
        sourceSpinner.setVisibility(vis);
        targetSpinner.setVisibility(vis);
        buttonSwap.setVisibility(vis);
        favoritesButton.setVisibility(vis);
        copyright.setVisibility(vis);

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
            extends AsyncTask<ITranslator, Void, TOneWord> {

        TOneWord CurrentWord;
        final int some_error = 1;
        int errCode = some_error;


        @Override
        protected void onPreExecute() {
            SetCurrentWord(null);

            String text = sourceText.getText().toString().trim();
            if (text.equals("")){
                errCode=0;
                CurrentWord=null;
                return;
            }
            ILanguage sourceLang = (ILanguage) sourceSpinner.getSelectedItem();
            ILanguage targetLang = (ILanguage) targetSpinner.getSelectedItem();

            CurrentWord = new TOneWord(sourceLang, targetLang, text);

            int iWord = historyList.indexOf(CurrentWord);
            if (iWord >= 0) {
                CurrentWord = historyList.get(iWord);
                errCode=0;
            }
            else {
                iWord = favoritList.indexOf(CurrentWord);
                if (iWord >= 0){
                    CurrentWord = favoritList.get(iWord);
                    errCode=0;
                }
            }
        }

        @Override
        protected TOneWord doInBackground(ITranslator... params) {
            if (errCode!=0) {
                try {
                    Thread.sleep(1000); //Задержка перед переводом для сохранения траффика
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!isCancelled()) {
                    ITranslator translator=params[0];
                    CurrentWord.setTargetText(translator.Translate(
                            CurrentWord.getSourceText(),
                            CurrentWord.getSourceLang(),
                            CurrentWord.getTargetLang()));
                    errCode=translator.ErrCode();
                }
            }
            return CurrentWord;
        }

        @Override
        protected void onPostExecute(TOneWord word) {
            if (errCode==0)
            {
                SetCurrentWord(word);
                saveToHistory(word);
            }
            else
            {
                targetText.setText(/*GetErrorCaption(errcode)*/ getString(R.string.someError));
            }
        }
    }

    private void saveToHistory(TOneWord currentWord) {
        if (currentWord==null) return;
        if (!historyList.contains(currentWord)) {
            SQLiteDatabase database = dbHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(dbHelper.KEY_SOURCE_LANG, currentWord.getSourceLang().GetUI());
            contentValues.put(dbHelper.KEY_TARGET_LANG, currentWord.getTargetLang().GetUI());
            contentValues.put(dbHelper.KEY_SOURCE_TEXT, currentWord.getSourceText());
            contentValues.put(dbHelper.KEY_TARGET_TEXT, currentWord.getTargetText());

            database.insert(dbHelper.TABLE_HISTORY, null, contentValues);
            dbHelper.close();
        }
        else
        {
            historyList.remove(currentWord);
        }
        historyList.add(0, currentWord);
    }

    public void OnSwapLanguage(View view) {
        int tempLang = sourceSpinner.getSelectedItemPosition();
        sourceSpinner.setSelection(targetSpinner.getSelectedItemPosition());
        targetSpinner.setSelection(tempLang);
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
        final String recordLimit="1000";
        class tAsyncLoad extends AsyncTask<tLangList, Void, tWordList> {
            String tableName;

            @Override
            protected tWordList doInBackground(tLangList... params) {
                tWordList historyList = new tWordList();

                SQLiteDatabase database = dbHelper.getReadableDatabase();
                Cursor cursor =
                        database.query(tableName,
                                null, null, null, null, null,
                                dbHelper.KEY_ID+ " DESC",
                                recordLimit);

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
                    Collections.sort(favoritList, new Comparator<TOneWord>() {
                        @Override
                        public int compare(TOneWord o1, TOneWord o2) {
                            return o1.toString().toUpperCase().compareTo(o2.toString().toUpperCase());
                        }
                    });
                    wordListView.setAdapter(GetAdapter(favoritList));
                    return true;
            }
            return false;
        }
    };
}
