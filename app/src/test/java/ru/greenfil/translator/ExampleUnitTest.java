package ru.greenfil.translator;

import android.os.Bundle;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Попытка написать хоть какие-то юнит-тесты
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

class TranslatorUnitTest implements ITranslator{

    @Override
    public String Translate(String MyText, ILanguage SourceLanguage, ILanguage TargetLanguage) {
        return null;
    }

    @Override
    public int ErrCode() {
        return 0;
    }
}

class ActivityUnitTest extends MainActivity {
    @Override
    void LoadData() {

    }

    @Override
    protected ITranslator GetTranslator() {
        return super.GetTranslator();//  new TranslatorUnitTest();
    }
}

public class ExampleUnitTest {
    private MainActivity activity;
    @Before
    public void SetUp(){
       activity=new MainActivity();
    }

    @Test
    public void activityNotNull(){
        assertNotNull(activity);
    }

    @Test
    public void translatorNotNull(){
        assertNotNull(activity.mytranslator);
    }


}