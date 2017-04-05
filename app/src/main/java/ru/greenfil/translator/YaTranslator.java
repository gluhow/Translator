package ru.greenfil.translator;

import android.widget.TextView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Объект-переводчик, опирающийся на API Yandex
 */

public class YaTranslator implements ITranslator {
    @Override
    public String Translate(String MyText, ILanguage SourceLanguage, ILanguage TargetLanguage) throws IOException {
        HttpURLConnection connection;
        URL url=new URL("https://translate.yandex.net/api/v1.5/tr.json/translate ?");
        connection = (HttpURLConnection) url.openConnection();
        int response = connection.getResponseCode();
        if (response==HttpURLConnection.HTTP_OK)
        {
            return "Ok";
        }
        else
        {
            return "not Ok";
        }


    }
}
