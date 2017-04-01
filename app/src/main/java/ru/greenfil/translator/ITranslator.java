package ru.greenfil.translator;
import android.widget.TextView;

import java.util.List;

import ru.greenfil.translator.ILanguage;
/**
 * Created by greenfil on 01.04.17.
 */


public interface ITranslator {
    void SetSourceLang(ILanguage _Lang);
    ILanguage GetSourceLang();
    void SetTargetLang(ILanguage _Lang);
    ILanguage GetTargetLang();
    void SwapLang();
    void SetText(String _Text);

    //Поменять на что-то более вменяемое
    void SetTextOut(TextView _TextOut);

    List<ILanguage> GetLanguageList();
    //Позже можно подумать о подгрузке списка языков
}
