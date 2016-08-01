package cn.kkserver.demo;

import android.content.pm.ActivityInfo;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import cn.kkserver.app.App;
import cn.kkserver.view.style.StyleSheet;
import cn.kkserver.view.value.Unit;
import cn.kkserver.view.value.Value;

public class MainActivity extends FragmentActivity {


    private App _app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        DisplayMetrics metrics = getResources().getDisplayMetrics();

        Unit unit = new Unit();

        unit.displayScale = metrics.density;
        unit.dp = Math.min(metrics.widthPixels,metrics.heightPixels) / 320f;

        Value.push(unit);

        try {

            _app = App.load(R.xml.app,this);

            _app.set(R.id.Activity,this);

            StyleSheet styleSheet = new StyleSheet();

            styleSheet.loadCSS(R.raw.style,this);

            _app.set(R.id.StyleSheet,styleSheet);

            _app.run();

        } catch (Throwable e) {
            Log.d("app", e.getMessage(),e);
        }

    }
}
