package cn.kkserver.demo;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
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

        Unit unit = new Unit();

        unit.displayScale = getResources().getDisplayMetrics().density;
        unit.dp = 1;

        Value.push(unit);

        try {

            _app = App.load(R.xml.app,this);

            _app.set(R.id.Activity,this);

            StyleSheet styleSheet = new StyleSheet();

            styleSheet.loadCSS(R.raw.style,this);

            _app.set(R.id.StyleSheet,styleSheet);

            FragmentTransaction trans = getSupportFragmentManager().beginTransaction();

            App p = _app.find("bottombar");

            if(p != null) {
                Fragment fragment = p.getFragment();
                trans.replace(R.id.bottomBar,fragment);
            }

            p = _app.firstChild();

            while(p != null) {

                if(!"bottombar".equals(p.name())) {

                    Fragment fragment = p.getFragment();
                    trans.replace(R.id.contentView,fragment);

                    break;
                }

                p = p.nextSibling();
            }

            trans.commit();

        } catch (Throwable e) {
            Log.d("app", e.getMessage(),e);
        }

    }
}
