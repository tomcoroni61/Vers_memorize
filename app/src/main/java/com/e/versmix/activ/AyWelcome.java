package com.e.versmix.activ;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;

import com.e.versmix.R;
import com.e.versmix.databinding.AyWelcomeBinding;
import com.e.versmix.utils.BaseSwipeActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class AyWelcome extends BaseSwipeActivity {
    private AyWelcomeBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AyWelcomeBinding.inflate(getLayoutInflater());

        setContentView(binding.container);

        CharSequence text = read(this, R.raw.welcome);
        binding.HtmlView.setMovementMethod(LinkMovementMethod.getInstance());
        binding.HtmlView.setText(Html.fromHtml(text.toString()));

    }

    public static CharSequence read(Context context, int resId)
    {
        StringBuilder text = new StringBuilder();

        try (BufferedReader buffer = new BufferedReader
                (new InputStreamReader
                        (context.getResources().openRawResource(resId))))
        {
            String line;
            while ((line = buffer.readLine()) != null)
                text.append(line).append(System.lineSeparator());
        }

        catch (Exception ignored) {}

        return text;
    }

    public void onSwipeLeft() {
        finish();
    }
    public void onSwipeRight() {
        finish();
    }

    public void wExitClick(View view) {
        finish();
    }

    public void wEineFueClick(View view) {//intruduce.html
        CharSequence text = read(this, R.raw.intruduce);
        binding.HtmlView.setText(Html.fromHtml(text.toString()));
        binding.wtvwell.setText(getString(R.string.introduction));
    }
}
