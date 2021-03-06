package ru.greenfil.translator;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

/**
 * Объект-переводчик, опирающийся на API Yandex
 */

class YaTranslator implements ITranslator {
    private final Integer some_error=1;
    private Integer fErrRes=some_error;

    /**
     * Получить ответ от сервера Яндекса
     * text - исходный текст
     * sourceLang - исходный язык
     * targetLang - целевой язык
     */
    private String GetWebAnswer(String text, String sourceLang, String targetLang) {
        final String
                APIkey="trnsl.1.1.20170408T105508Z.9e4323ceef72527b.6578d71fe0273638aa0c3cd75b9d5a334115404f";
        fErrRes=some_error;
        String yaURL="https://translate.yandex.net/api/v1.5/tr.json/translate ?";
        String params="";
        try {
            params = String.format(
                    "key=%s&text=%s&lang=%s-%s",
                    APIkey,
                    URLEncoder.encode(text, "UTF-8"),
                    sourceLang,
                    targetLang);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        String res="";

        try {
            URL url=new URL(yaURL);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.getOutputStream().write(params.getBytes("UTF-8"));
            connection.connect();

            int response = connection.getResponseCode();

            if (response == HttpsURLConnection.HTTP_OK) {

                InputStream in = connection.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    baos.write(buffer, 0, bytesRead);
                }
                byte[] data = baos.toByteArray();
                res = new String(data, "UTF-8");
            }
            connection.disconnect();
        }
        catch (Exception e) {
            res="";
        }

        return res;
    }

    /**
     * Получить текст перевода по JSON-строке
     */
    private String GetTextFROMJSON(String text){
        try {
            JSONObject resJSON = new JSONObject(text);
            JSONArray textAr = resJSON.getJSONArray("text");
            if (resJSON.getInt("code")==200)
            {
                fErrRes=0;
                return (String) textAr.get(0);
            }
            return "";

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return text;
    }

    /**
     * Перевести текст
     */
    @Override
    public String Translate(@NonNull String MyText,@NonNull String SourceLanguageUI,@NonNull String TargetLanguageUI) {
        if (!MyText.isEmpty()) {
            String WebAns = GetWebAnswer(MyText, SourceLanguageUI, TargetLanguageUI);
            return GetTextFROMJSON(WebAns);
            //Перенести формирование кодов ошибки сюда!
        }
        else
        {
            fErrRes=0;
            return "";
        }
    }

    @Override
    public int ErrCode() {
        return fErrRes;
    }
}
