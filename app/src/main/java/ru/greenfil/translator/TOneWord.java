package ru.greenfil.translator;

/**
 * В классе зранится одно переведенное слово
 */

class TOneWord {
    private ILanguage sourceLang;
    private ILanguage targetLang;
    private String sourceText;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TOneWord tOneWord = (TOneWord) o;

        return sourceLang.equals(tOneWord.sourceLang) &&
                targetLang.equals(tOneWord.targetLang) &&
                sourceText.equals(tOneWord.sourceText);

    }

    @Override
    public int hashCode() {
        int result = sourceLang.hashCode();
        result = 31 * result + targetLang.hashCode();
        result = 31 * result + sourceText.hashCode();
        return result;
    }

    void setTargetText(String targetText) {
        this.targetText = targetText;
    }

    private String targetText;

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
