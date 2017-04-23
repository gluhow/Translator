package ru.greenfil.translator;

/**
 * Описание интерфейса переводчика без привязки к сервису
 */


interface ITranslator {
    //**Получить перевод
    String Translate(String MyText, //Текст, который надо перевести
                     ILanguage SourceLanguage, //Исходный язык
                     ILanguage TargetLanguage   //Целевой язык
    );
    int ErrCode(); //Код ошибки. 0 - ошибок нет
}