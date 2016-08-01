package cn.kkserver.demo.main;

import android.support.v4.app.Fragment;

import cn.kkserver.app.App;
import cn.kkserver.app.AppStartup;
import cn.kkserver.demo.R;
import cn.kkserver.view.value.Value;

/**
 * Created by zhanghailong on 16/7/28.
 */
public class Content implements AppStartup {

    @Override
    public void run(App app) {

        app.parent().on(new String[]{"bottombar","selected"})
                .to(new App.WeakInlet<App>(app) {
            @Override
            public boolean set(Object value) {

                App app = object();

                if(app != null) {

                    String name = Value.stringValue(value, "match");

                    if(app.name().equals(name)) {
                        Fragment fragment = app.getFragment();
                        if(fragment != null && !fragment.isAdded()) {
                            app.parent().getFragmentManager()
                                    .beginTransaction().replace(R.id.contentView,fragment).commit();
                        }
                    }

                }

                return true;
            }
        });

    }
}
