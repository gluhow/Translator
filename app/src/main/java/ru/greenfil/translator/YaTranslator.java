package ru.greenfil.translator;

import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Объект-переводчик, опирающийся на API Yandex
 */

public class YaTranslator implements ITranslator {
    protected TextView textOut;
    protected ILanguage sourceLang;
    protected ILanguage targetLang;
    protected ArrayList<ILanguage> langList;
    protected String transText;

    public YaTranslator() {
        langList=new ArrayList<ILanguage>();
        TLanguage curLang;

        langList.add(new TLanguage("русский", "ru"));
        langList.add(new TLanguage("английский", "en"));

        sourceLang=langList.get(0);
        targetLang=langList.get(1);
    }


    @Override
    public void SetSourceLang(ILanguage _Lang) {
        sourceLang=_Lang;
    }

    @Override
    public ILanguage GetSourceLang() {
        return sourceLang;
    }

    @Override
    public void SetTargetLang(ILanguage _Lang) {
        targetLang=_Lang;
    }

    @Override
    public ILanguage GetTargetLang() {
        return targetLang;
    }

    @Override
    public void SwapLang() {
        final ILanguage temp=GetSourceLang();
        SetSourceLang(GetTargetLang());
        SetTargetLang(temp);
    }

    @Override
    public void SetText(String _Text) {
        transText=_Text;
    }

    @Override
    public void SetTextOut(TextView _TextOut) {
        textOut=_TextOut;
    }

    @Override
    public List<ILanguage> GetSourceLanguageList() {
        return langList;
    }

    @Override
    public List<ILanguage> GetTargetLanguageList() {
        return langList;
    }


}
