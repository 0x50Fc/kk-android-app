package cn.kkserver.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.util.SparseArray;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import cn.kkserver.view.ElementObserver;
import cn.kkserver.view.document.Document;
import cn.kkserver.view.document.Element;
import cn.kkserver.view.document.TouchElement;
import cn.kkserver.view.document.XMLReader;
import cn.kkserver.view.event.Event;
import cn.kkserver.view.obs.IObject;
import cn.kkserver.view.obs.IObserver;
import cn.kkserver.view.obs.IWithObject;
import cn.kkserver.view.obs.Listener;
import cn.kkserver.view.obs.ObsObject;
import cn.kkserver.view.obs.Observer;
import cn.kkserver.view.style.StyleSheet;
import cn.kkserver.view.value.Value;

/**
 * Created by zhanghailong on 16/7/26.
 */
public class App extends Observer {

    private WeakReference<App> _parent;
    private App _firstChild;
    private App _lastChild;
    private App _nextSibling;
    private WeakReference<App> _prevSibling;
    private final String _name;

    public App(String name) {
        super();
        _name = name;
    }

    public String name() {
        return _name;
    }

    void setParent(App v) {
        if(v == null) {
            _parent = null;
        }
        else {
            _parent = new WeakReference<App>(v);
        }
    }

    public App parent() {
        return _parent == null ? null : _parent.get();
    }

    /**
     * 首子节点
     * @return
     */
    public App firstChild() {
        return _firstChild;
    }

    void setFirstChild(App v) {
        _firstChild = v;
    }
    /**
     * 未子节点
     * @return
     */
    public App lastChild() {
        return _lastChild;
    }

    void setLastChild(App v) {
        _lastChild = v;
    }

    /**
     * 下一个兄弟节点
     * @return
     */
    public App nextSibling() {
        return _nextSibling;
    }

    void setNextSibling(App v) {
        _nextSibling = v;
    }

    /**
     * 上一个兄弟节点
     * @return
     */
    public App prevSibling() {
        if(_prevSibling != null) {
            App v = _prevSibling.get();
            if(v == null) {
                _prevSibling = null;
            }
            return v;
        }
        return null;
    }

    void setPrevSibling(App v) {
        if(v == null) {
            _prevSibling = null;
        }
        else {
            _prevSibling = new WeakReference<App>(v);
        }
    }

    /**
     * 追加子节点
     * @param app
     * @return
     */
    public App append(App app) {

        app.remove();

        if(_lastChild == null) {
            _lastChild = app;
            _firstChild = app;
            app.setParent(this);
        }
        else {
            _lastChild._nextSibling = app;
            app.setPrevSibling(_lastChild);
            _lastChild = app;
            app.setParent(this);
        }

        return this;
    }

    /**
     * 追加到父级节点
     * @param app
     * @return
     */
    public App appendTo(App app) {
        app.append(this);
        return this;
    }

    /**
     * 前面插入节点
     * @param app
     * @return
     */
    public App before(App app) {

        app.remove();

        App v = prevSibling();

        if(v != null) {
            v.setNextSibling(app);
            app.setPrevSibling(v);
            app.setNextSibling(this);
            setPrevSibling(app);
            app.setParent(parent());
        }
        else if((v = parent()) != null) {
            v.setFirstChild(app);
            app.setNextSibling(this);
            setPrevSibling(app);
            app.setParent(v);
        }

        return this;
    }

    /**
     * 插入到节点前
     * @param app
     * @return
     */
    public App beforeTo(App app) {
        app.before(this);
        return this;
    }

    /**
     * 后面插入节点
     * @param app
     * @return
     */
    public App after(App app) {

        app.remove();

        App v = parent();

        if(v != null) {
            app.setParent(v);
            app.setNextSibling(_nextSibling);

            if(_nextSibling != null) {
                _nextSibling.setPrevSibling(app);
            }
            else {
                v.setLastChild(app);
            }
            setNextSibling(app);
            app.setPrevSibling(this);
        }

        return this;
    }

    /**
     * 插入到节点后面
     * @param app
     * @return
     */
    public App afterTo(App app) {
        app.after(this);
        return this;
    }

    /**
     * 从父级节点移除
     * @return
     */
    public App remove() {

        App p = parent();

        if(p != null) {

            App v = prevSibling();

            if (v != null) {
                v.setNextSibling(_nextSibling);
                if (_nextSibling != null) {
                    _nextSibling.setPrevSibling(v);
                } else {
                    parent().setLastChild(v);
                }
            } else if ((v = parent()) != null) {
                v.setFirstChild( _nextSibling);
                if (_nextSibling != null) {
                    _nextSibling.setPrevSibling(null);
                } else {
                    v.setLastChild(null);
                }
            }

            _parent = null;
            _prevSibling = null;
            _nextSibling = null;

        }

        return this;
    }

    public Outlet on(String[] keys) {
        Outlet v = new Outlet(keys,this);
        on(v,keys);
        return v;
    }

    private final SparseArray<Object> _values = new SparseArray<>();

    public Object get(int id) {
        return _values.get(id);
    }

    public App set(int id,Object value) {
        _values.put(id,value);
        return this;
    }

    public App remove(int id) {
        _values.remove(id);
        return this;
    }

    public Context getContext() {

        App app = this;
        Context v = null;

        while(app != null
                && (v = (Context) app.get(R.id.Context)) == null
                && (v = (Context) app.get(R.id.Activity)) == null
                && (v = (Context) app.get(R.id.Application)) == null) {
            app = app.parent();
        }

        return v;
    }

    public Activity getActivity() {

        App app = this;
        Activity v = null;

        while(app != null
                && (v = (Activity) app.get(R.id.Activity)) == null) {
            app = app.parent();
        }

        return v;
    }

    public Application getApplication() {

        App app = this;
        Application v = null;
        Activity act = null;

        while(app != null
                && (v = (Application) app.get(R.id.Application)) == null
                && (act = (Activity) app.get(R.id.Activity)) != null) {
            app = app.parent();
        }

        if(v == null && act != null) {
            v = act.getApplication();
        }

        return v;

    }

    public StyleSheet getStyleSheet() {

        App app = this;
        StyleSheet v = null;

        while(app != null
                && (v = (StyleSheet) app.get(R.id.StyleSheet)) == null) {
            app = app.parent();
        }

        return v;
    }

    private static class ElementEventCallback extends Event.WeakCallback<IObject> {

        public ElementEventCallback(IObject object) {
            super(object);
        }

        @Override
        public boolean onEvent(Event event) {
            IObject object = object();
            if(object != null) {
                if(event instanceof TouchElement.ElementTouchActionEvent) {
                    String key = ((TouchElement.ElementTouchActionEvent) event).action;
                    String value =  ((TouchElement.ElementTouchActionEvent) event).element.attr("value");
                    object.set(ObsObject.keys(key),value);
                }
            }
            return true;
        }
    }

    private ElementEventCallback _eventCallback = new ElementEventCallback(this);

    public Document getDocument() {

        Document document = (Document) get(R.id.Document);

        if(document == null) {
            document = new Document();
            document.setStyleSheet(getStyleSheet());
            document.on(Pattern.compile("^element\\.action$"), _eventCallback);

            set(R.id.Document,document);

            String uri = Value.stringValue(get(new String[]{"attributes","uri"}),null);

            if(uri != null) {

                Context context = getContext();

                if(uri.startsWith("assets://")) {

                    AssetManager assetManager = context.getAssets();

                    try {

                        InputStream in = assetManager.open(uri.substring(9));

                        try {
                            XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
                            parser.setInput(in,"utf-8");
                            XMLReader reader = new XMLReader(document);
                            document.setRootElement(reader.read(parser));
                        }
                        finally {
                            in.close();
                        }

                    } catch (Throwable e) {
                        Log.d("kk-app",e.getMessage(),e);
                    }

                }
                else {

                    int i = uri.indexOf("R.xml.");

                    if(i == 0) {
                        try {
                            Class<?> clazz = Class.forName(context.getPackageName() + ".R$xml");
                            java.lang.reflect.Field fd = clazz.getField(uri.substring(i + 6));
                            int id = fd.getInt(null);
                            XMLReader reader = new XMLReader(document);
                            document.setRootElement(reader.read(context.getResources().getXml(id)));
                        }
                        catch (Throwable e) {
                            Log.d("kk-app",e.getMessage(),e);
                        }
                    }
                    else if(i > 0) {
                        try {
                            Class<?> clazz = Class.forName(uri.substring(0,i) + "R$xml");
                            java.lang.reflect.Field fd = clazz.getField(uri.substring(i + 6));
                            int id = fd.getInt(null);
                            XMLReader reader = new XMLReader(document);
                            document.setRootElement(reader.read(context.getResources().getXml(id)));
                        }
                        catch (Throwable e) {
                            Log.d("kk-app",e.getMessage(),e);
                        }
                    }
                }
            }


        }

        return document;
    }

    public ElementObserver getElementObserver() {

        ElementObserver v = (ElementObserver) get(R.id.ElementObserver);

        if(v == null) {

            Document document = getDocument();

            if(document != null) {

                Element e = document.rootElement();

                if(e != null) {
                    v = new ElementObserver(e,this);
                    set(R.id.ElementObserver,v);
                    v.change();
                }

            }
        }

        return v;
    }

    public void cancelElementObserver() {

        ElementObserver v = (ElementObserver) get(R.id.ElementObserver);

        if (v != null) {
            v.off();
            remove(R.id.ElementObserver);
        }

    }


    public Fragment getFragment() {

        Fragment v = (Fragment) get(R.id.Fragment);

        if(v == null) {
            v = new AppFragment();
            ((AppFragment) v).setApp(this);
            set(R.id.Fragment,v);
        }

        return v;
    }

    public FragmentManager getFragmentManager() {

        {
            Fragment v = (Fragment) get(R.id.Fragment);

            if (v != null) {
                return v.getChildFragmentManager();
            }
        }

        {
            Activity v = getActivity();

            if(v != null && v instanceof FragmentActivity) {
                return ((FragmentActivity) v).getSupportFragmentManager();
            }
        }

        return null;
    }

    public AppStartup getStartup() {

        AppStartup v =  (AppStartup) get(R.id.Startup);

        if(v == null) {

            String name = Value.stringValue(get(new String[]{"attributes","startup"}),null);

            if(name != null && ! name.isEmpty()) {

                try {

                    Class<?> clazz = Class.forName(name);

                    v = (AppStartup) clazz.newInstance();

                    set(R.id.Startup,v);

                }
                catch (Throwable e){
                    Log.d("kk-app",e.getMessage(),e);
                }

            }
        }

        return v;
    }

    public void run() {

        AppStartup v =  getStartup();

        if(v != null) {
            v.run(this);
        }

        App p = firstChild();

        while(p != null) {
            p.run();
            p = p.nextSibling();
        }

    }

    public App find(String name) {

        App p = firstChild();

        while(p != null) {
            if(p.name().equals(name)) {
                return p;
            }
            p = p.nextSibling();
        }

        return null;
    }

    public static class Outlet implements Listener {

        private final WeakReference<App> _app;

        private final List<Inlet> _inlets = new LinkedList<Inlet>();
        public final String[] keys;

        public Outlet(String[] keys,App app) {
            this.keys = keys;
            _app = new WeakReference<App>(app);
        }

        @Override
        public void onChanged(IObserver observer, String[] keys) {
            Object v = observer.get(keys);
            Iterator<Inlet> i = _inlets.iterator();
            while(i.hasNext()) {
                Inlet inlet = i.next();
                if(!inlet.set(v)) {
                    i.remove();
                }
            }
        }

        public Outlet to(IObject object,String[] keys) {
            _inlets.add(new ObjectInlet(object,keys));
            return this;
        }

        public Outlet to(Inlet inlet) {
            _inlets.add(inlet);
            return this;
        }

        public void cancel() {
            App app = _app.get();
            if(app != null) {
                app.off(this,keys);
            }
        }

    }

    public static interface Inlet {

        public boolean set(Object value);

    }

    public static abstract class WeakInlet<T> implements Inlet {

        private WeakReference<T> _ref;

        public WeakInlet(T object) {
            _ref = new WeakReference<T>(object);
        }

        public T object() {
            return _ref.get();
        }

    }

    private static class ObjectInlet implements Inlet {

        public final WeakReference<IObject> ref;
        public final String[] keys;

        public ObjectInlet(IObject object,String[] keys) {
            this.ref = new WeakReference<IObject>(object);
            this.keys = keys;
        }

        public boolean set(Object value) {
            IObject object = ref.get();
            if(object != null) {
                object.set(keys, value);
                return true;
            }
            return false;
        }
    }

    public static App load(String uri,Context context) throws Throwable {

        App v = null;

        if(uri.startsWith("assets://")) {

            AssetManager assetManager = context.getAssets();

            try {

                InputStream in = assetManager.open(uri.substring(9));

                try {
                    XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
                    parser.setInput(in,"utf-8");
                    v = load(parser);
                }
                finally {
                    in.close();
                }

            } catch (Throwable e) {
                Log.d("kk-app",e.getMessage(),e);
            }

        }
        else {

            int i = uri.indexOf("R.xml.");

            if(i == 0) {
                try {
                    Class<?> clazz = Class.forName(context.getPackageName() + ".R$xml");
                    java.lang.reflect.Field fd = clazz.getField(uri.substring(i + 6));
                    int id = fd.getInt(null);
                    v = load(context.getResources().getXml(id));
                }
                catch (Throwable e) {
                    Log.d("kk-app",e.getMessage(),e);
                }
            }
            else if(i > 0) {
                try {
                    Class<?> clazz = Class.forName(uri.substring(0,i) + "R$xml");
                    java.lang.reflect.Field fd = clazz.getField(uri.substring(i + 6));
                    int id = fd.getInt(null);
                    v = load(context.getResources().getXml(id));
                }
                catch (Throwable e) {
                    Log.d("kk-app",e.getMessage(),e);
                }
            }
        }

        return v;
    }

    public static App load(int id, Context context ) throws Throwable {
        return load(context.getResources().getXml(id));
    }

    public static App load(XmlPullParser parser) throws Throwable {

        App root = null;
        App app = null;

        int type = parser.next();

        while(type != XmlPullParser.END_DOCUMENT) {
            switch (type) {
                case XmlPullParser.START_TAG:
                {
                    App a = new App(parser.getName());

                    IWithObject with = a.with(new String[]{"attributes"});

                    for(int i=0;i<parser.getAttributeCount();i++) {
                        with.set(parser.getAttributeName(i),parser.getAttributeValue(i));
                    }

                    if(root == null) {
                        root = a;
                    }

                    if(app == null) {
                        app = a;
                    }
                    else {
                        app.append(a);
                        app = a;
                    }
                }
                break;
                case XmlPullParser.END_TAG:
                {
                    if(app != null) {
                        app = app.parent();
                    }

                }
                break;
            }

            type = parser.next();
        }

        return root;
    }

}
