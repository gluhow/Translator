package ru.greenfil.translator;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Адаптер для списка истории/избранного
 */

class tOneWordAdapter extends ArrayAdapter<TOneWord> {
    private List<TOneWord> favoriteList;
    tOneWordAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<TOneWord> objects, @NonNull List<TOneWord> favoriteList) {
        super(context, resource, objects);
        this.favoriteList=favoriteList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TOneWord word = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.word_item, null);
        }
        assert word != null;
        ((TextView) convertView.findViewById(R.id.sourceLang))
                .setText(word.getSourceLang().GetUI());
        ((TextView) convertView.findViewById(R.id.sourceText))
                .setText(word.getSourceText());
        ((TextView) convertView.findViewById(R.id.targetLang))
                .setText(word.getTargetLang().GetUI());
        ((TextView) convertView.findViewById(R.id.targetText))
                .setText(word.getTargetText());
        if (favoriteList.contains(word)) {
            ((ImageView) convertView.findViewById(R.id.favoriteImage)).
                    setImageResource(R.drawable.favorite);
        }
        else {
            ((ImageView) convertView.findViewById(R.id.favoriteImage)).
                    setImageResource(R.drawable.nonfavorite);
        }

        return convertView;
    }
}
