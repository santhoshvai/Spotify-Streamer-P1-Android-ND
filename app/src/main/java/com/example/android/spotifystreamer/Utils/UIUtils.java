package com.example.android.spotifystreamer.Utils;

import android.support.design.widget.Snackbar;
import android.view.View;

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
}
