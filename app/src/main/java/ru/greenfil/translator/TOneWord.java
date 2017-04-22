package ru.greenfil.translator;

/**
 * В классе зранится одно переведенное слово
 */

class TOneWord {
    private ILanguage sourceLang;
    private ILanguage targetLang;
    private String sourceText;
    private String targetText;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TOneWord tOneWord = (TOneWord) o;

        return sourceLang.equals(tOneWord.sourceLang) &
                targetLang.equals(tOneWord.targetLang) &
                sourceText.trim().equals(tOneWord.sourceText.trim());

    }

    @Override
    public int hashCode() {
        int result = sourceLang.hashCode();
        result = 31 * result + targetLang.hashCode();
        result = 31 * result + sourceText.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s<%s-%s>%s",
                sourceText,
                getSourceLang().GetUI(), getTargetLang().GetUI(),
                targetText);
    }

    void setTargetText(String targetText) {
        this.targetText = targetText;
    }

    ILanguage getSourceLang() {
        return sourceLang;
    }

    ILanguage getTargetLang() {
        return targetLang;
    }

    String getSourceText() {
        return sourceText;
    }

    String getTargetText() {
        return targetText;
    }

    TOneWord(ILanguage sourceLang, ILanguage targetLang, String sourceText) {
        this.sourceLang = sourceLang;
        this.targetLang = targetLang;
        this.sourceText = sourceText;
        this.targetText = "";
    }
}
