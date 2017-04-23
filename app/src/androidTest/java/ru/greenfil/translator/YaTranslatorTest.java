package ru.greenfil.translator;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Тесты яндекс-переводчика
 */
public class YaTranslatorTest {
    private YaTranslator translator;
    @Before
    public void setUp() throws Exception {
        translator=new YaTranslator();
    }

    @Test
    public void translate_hello_en_ru_привет() throws Exception {
        assertEquals("привет", translator.Translate("hello", "en", "ru"));
        assertEquals(0, translator.ErrCode());
    }

    @Test
    public void errCode() {
        assertNotEquals(0, translator.ErrCode());
    }

    @Test
    public void translate_empty_empty(){
        assertEquals("", translator.Translate("", "", ""));
        assertEquals(0, translator.ErrCode());
    }

    @Test
    public void translate_hello_empty_error(){
        translator.Translate("hello", "", "");
        assertNotEquals(0, translator.ErrCode());
    }

    //Как отрубить интернет не ведаю

}