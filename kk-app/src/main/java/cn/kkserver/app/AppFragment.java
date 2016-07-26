package cn.kkserver.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.lang.ref.WeakReference;
import cn.kkserver.view.ElementView;
import cn.kkserver.view.ScrollView;
import cn.kkserver.view.document.Document;
import cn.kkserver.view.value.Value;

/**
 * Created by zhanghailong on 16/7/26.
 */
public class AppFragment  extends Fragment {

    private WeakReference<App> _app;
    private ViewLayout _viewLayout;

    public App app() {
        return _app.get();
    }

    public void setApp(App app) {
        _app = new WeakReference<App>(app);
        String view = Value.stringValue(app.get(new String[]{"attributes","view"}),null);
        if(view != null) {
            _viewLayout = new ViewLayout(view);
        }
        else {
            _viewLayout = null;
        }
    }
    public AppFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v;

        ElementView documentView;

        if(_viewLayout != null) {
            v = _viewLayout.getView(inflater,container);
            documentView = (ElementView) v.findViewById(R.id.DocumentView);
        }
        else {
            if(container instanceof ElementView) {
                documentView = (ElementView) container;
                v = null;
            }
            else {
                documentView = new ScrollView(getContext());
                documentView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                v = documentView;
            }
        }

        App app ;

        if(_app != null && (app = _app.get()) != null) {

            Document document = app.getDocument();

            if(document != null) {
                app.getElementObserver();
                documentView.setElement(document.rootElement());
            }

        }

        return v;
    }

    @Override
    public void onDestroy(){

        App app ;

        if(_app != null && (app = _app.get()) != null) {
            app.cancelElementObserver();
        }

        super.onDestroy();
    }

}
