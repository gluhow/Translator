package ru.greenfil.translator;

import android.widget.TextView;

import java.util.List;

/**
 * Created by greenfil on 01.04.17.
 */

public class YaTranslator implements ITranslator {
    TextView textOut;
    ILanguage sourceLang;
    ILanguage targetLang;
    String transText;

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
        return null;
    }

    @Override
    public List<ILanguage> GetTargetLanguageList() {
        return null;
    }


}
