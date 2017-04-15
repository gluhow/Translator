package ru.greenfil.translator;

/**
 * Объект, отвечающий за 1 язык
 */

class TLanguage implements ILanguage {
    private String caption;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TLanguage tLanguage = (TLanguage) o;

        return ui.equals(tLanguage.ui);

    }

    @Override
    public int hashCode() {
        return ui.hashCode();
    }

    private String ui;

    TLanguage(String caption, String ui) {
        this.caption = caption;
        this.ui = ui;
    }

    @Override
    public String GetCaption() {
        return caption;
    }


    @Override
    public String GetUI() {
        return ui;
    }

    @Override
    public String toString() {
        return GetCaption();
    }
}
