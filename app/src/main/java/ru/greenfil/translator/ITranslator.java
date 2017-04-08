package ru.greenfil.translator;

/**
 * Описание класса "Переводчик" без привязки к сервису
 */


interface ITranslator {
    String Translate(String MyText, ILanguage SourceLanguage, ILanguage TargetLanguage);
//Позже можно подумать о подгрузке списка языков
}