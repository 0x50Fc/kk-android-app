package cn.kkserver.demo.main;

import android.support.v4.app.Fragment;

import cn.kkserver.app.App;
import cn.kkserver.app.AppStartup;
import cn.kkserver.demo.R;
import cn.kkserver.view.value.Value;

/**
 * Created by zhanghailong on 16/7/28.
 */
public class BottomBar implements AppStartup {

    private final static String[] tabbars = new String[]{"match","news","market","portfolio","me"};

    @Override
    public void run(App app) {

        app.on(new String[]{"selected"})
                .to(app.parent(),new String[]{"bottombar","selected"})
                .to(new App.WeakInlet<App>(app) {
                    @Override
                    public boolean set(Object value) {
                        App app = object();
                        String name = Value.stringValue(value,tabbars[0]);
                        if(app != null) {
                            for(String tabbar : tabbars) {
                                if(name.equals(tabbar)) {
                                    app.set(new String[]{tabbar,"status"},"selected");
                                }
                                else {
                                    app.set(new String[]{tabbar,"status"},"");
                                }
                            }
                        }
                        return true;
                    }
                });

        app.set(new String[]{"selected"},tabbars[0]);

        Fragment fragment = app.getFragment();

        if(fragment != null) {
            app.parent().getFragmentManager()
                    .beginTransaction().replace(R.id.bottomBar,fragment).commit();
        }
    }
}
