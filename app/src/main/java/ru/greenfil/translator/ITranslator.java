package ru.greenfil.translator;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import ru.greenfil.translator.ILanguage;
/**
 * Created by greenfil on 01.04.17.
 */


public interface ITranslator {
    String Translate(String MyText, ILanguage SourceLanguage, ILanguage TargetLanguage);
//Позже можно подумать о подгрузке списка языков
}
