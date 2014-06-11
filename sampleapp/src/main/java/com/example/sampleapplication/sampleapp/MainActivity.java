package com.example.sampleapplication.sampleapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.circularoverlay.library.CutoutView;
import com.example.circularoverlay.library.OnCutoutButtonEventListener;
import com.example.circularoverlay.library.targets.ViewTarget;


public class MainActivity extends Activity implements View.OnClickListener,
        OnCutoutButtonEventListener {

    CutoutView cutoutView;
    ImageView imgPin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgPin = (ImageView) findViewById(R.id.imgPin);
        imgPin.setOnClickListener(this);

        RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lps.addRule(RelativeLayout.CENTER_HORIZONTAL);
        int margin = ((Number) (getResources().getDisplayMetrics().density * 12)).intValue();
        lps.setMargins(margin, margin, margin, margin);

        ViewTarget target = new ViewTarget(R.id.imgPin, this);
        cutoutView = new CutoutView.Builder(this, true)
                .setTarget(target)
                .setContentTitle(R.string.cutout_main_title)
                .setContentText(R.string.cutout_main_message)
                .setStyle(R.style.CustomOverlayTheme)
                .setCutoutEventListener(this)
                .setShdCenterText(false)
                .setOnClickListener(this)
                .build();
        cutoutView.setButtonText(getResources().getString(R.string.okay_got_it));
        cutoutView.setButtonPosition(lps);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {

        int viewId = view.getId();
        switch (viewId) {
            case R.id.imgPin:
                if (cutoutView.isShown()) {
//                    cutoutView.setStyle(R.style.CustomOverlayTheme);
                } else {
                    cutoutView.show();
                }
                break;

            case R.id.overlay_exit_button:
                cutoutView.hide();
                Toast.makeText(this, R.string.prompt, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onCutoutViewHide(CutoutView showcaseView) {

    }

    @Override
    public void onCutoutViewDidHide(CutoutView showcaseView) {

    }

    @Override
    public void onCutoutViewShow(CutoutView showcaseView) {

    }
}
