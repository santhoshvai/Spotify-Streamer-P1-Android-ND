package com.example.android.spotifystreamer.Utils;

import android.support.design.widget.Snackbar;
import android.support.v7.graphics.Palette;
import android.view.View;

import java.util.Collections;
import java.util.Comparator;

public class UIUtils {
    private static void showShortSnackBar(View view, CharSequence msg, CharSequence actionText){
        Snackbar
                .make(view, msg, Snackbar.LENGTH_SHORT)
                .setAction(actionText, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                })
                .show();
    }
    public static void InternetAccessibilityAlert(View view){
        showShortSnackBar(view, "Check your Internet connectivity.", "Ok");
    }

    public static void ArtistNotFoundAlert(View view){
        showShortSnackBar(view, "Artist not found. Please refine your search.", "Ok");
    }
    /* Get the color with highest population from a palette */
    public static Palette.Swatch getDominantSwatch(Palette palette) {
        // find most-represented swatch based on population
        return Collections.max(palette.getSwatches(), new Comparator<Palette.Swatch>() {
            @Override
            public int compare(Palette.Swatch sw1, Palette.Swatch sw2) {
                return Integer.valueOf(sw1.getPopulation())
                        .compareTo(Integer.valueOf(sw2.getPopulation()));
            }
        });
    }
}
