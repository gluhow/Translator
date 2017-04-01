package ru.greenfil.translator;

/**
 * Created by greenfil on 01.04.17.
 */

public class TLanguage implements ILanguage {
    String caption;
    String ui;
    @Override
    public String GetCaption() {
        return caption;
    }

    public void SetCaption(String AValue) {
        caption=AValue;
    }

    @Override
    public String GetUI() {
        return ui;
    }

    public void SetUI(String Avalue) {
        ui=Avalue;
    }
}
