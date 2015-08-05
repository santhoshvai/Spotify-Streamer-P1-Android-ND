package com.example.android.spotifystreamer.Utils;

import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

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
    public static void setAndTruncateTextOneLineTxtView(CharSequence text, TextView tv){
        final int MAX_ELLIPSIZE_LINES = 100;
        int mMaxLines = 1;
        // CharSequence text = tv.getText();
        CharSequence newText = TextUtils.ellipsize(text, tv.getPaint(), tv.getWidth() * mMaxLines,
                        TextUtils.TruncateAt.END, false, null);
        tv.setText(newText);
    }
}
