package com.muly.couple;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;

public class MainActivity extends Activity {
    private static final String HOME = "https://muly-pied.vercel.app/";
    private static final int PICK_FILE = 100;
    private WebView web;
    private ValueCallback<Uri[]> chooser;
    private LinearLayout cover;
    private TextView subtitle;
    private Button retry;
    private boolean loaded = false;
    private boolean failed = false;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(Color.parseColor("#FFF8F5"));
        getWindow().setNavigationBarColor(Color.parseColor("#FFF8F5"));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        FrameLayout root = new FrameLayout(this);
        web = new WebView(this);
        root.addView(web, new FrameLayout.LayoutParams(-1,-1));
        cover = new LinearLayout(this);
        cover.setOrientation(LinearLayout.VERTICAL);
        cover.setGravity(Gravity.CENTER);
        cover.setBackgroundColor(Color.parseColor("#FFF5F3"));
        cover.setPadding(28,28,28,28);
        TextView heart = new TextView(this);
        heart.setText("♡"); heart.setTextSize(68); heart.setTextColor(Color.parseColor("#AA5275")); heart.setGravity(Gravity.CENTER);
        TextView title = new TextView(this);
        title.setText("Muly"); title.setTextSize(34); title.setTextColor(Color.parseColor("#49333D")); title.setGravity(Gravity.CENTER);
        subtitle = new TextView(this);
        subtitle.setText("İkimizin küçük dünyası"); subtitle.setTextSize(15); subtitle.setTextColor(Color.parseColor("#886C77")); subtitle.setGravity(Gravity.CENTER);
        retry = new Button(this);
        retry.setText("Tekrar dene"); retry.setVisibility(View.GONE);
        retry.setOnClickListener(v -> { failed=false; loaded=false; retry.setVisibility(View.GONE); subtitle.setText("Yükleniyor…"); web.loadUrl(HOME); });
        cover.addView(heart); cover.addView(title); cover.addView(subtitle); cover.addView(retry);
        root.addView(cover, new FrameLayout.LayoutParams(-1,-1));
        setContentView(root);
        WebSettings settings = web.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(true);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        settings.setMediaPlaybackRequiresUserGesture(true);
        settings.setUserAgentString(settings.getUserAgentString()+" MulyAndroid/2.0");
        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(web,false);
        web.setWebViewClient(new WebViewClient() {
            @Override public boolean shouldOverrideUrlLoading(WebView v, WebResourceRequest req) {
                if (!req.isForMainFrame()) return false;
                Uri u=req.getUrl();
                if ("https".equals(u.getScheme()) && "muly-pied.vercel.app".equals(u.getHost())) return false;
                if ("https".equals(u.getScheme())) {
                    try { startActivity(new Intent(Intent.ACTION_VIEW,u)); } catch(Exception ignored) {}
                }
                return true;
            }
            @Override public void onPageStarted(WebView v,String url,android.graphics.Bitmap icon) { failed=false; }
            @Override public void onPageFinished(WebView v,String url) {
                if(!failed){loaded=true;cover.animate().alpha(0).setDuration(250).withEndAction(()->{cover.setVisibility(View.GONE);cover.setAlpha(1);}).start();}
            }
            @Override public void onReceivedError(WebView v,WebResourceRequest req,WebResourceError err) {
                if(req.isForMainFrame()){failed=true;loaded=false;cover.setVisibility(View.VISIBLE);subtitle.setText("Bağlantı kurulamadı. İnternetini kontrol et.");retry.setVisibility(View.VISIBLE);}
            }
        });
        web.setWebChromeClient(new WebChromeClient(){
            @Override public boolean onShowFileChooser(WebView v,ValueCallback<Uri[]> cb,FileChooserParams params) {
                if(chooser!=null)chooser.onReceiveValue(null);
                chooser=cb;
                try{ startActivityForResult(params.createIntent(),PICK_FILE);return true; }
                catch(Exception ex){chooser=null;cb.onReceiveValue(null);return false;}
            }
        });
        if(state!=null && web.restoreState(state)!=null){}else web.loadUrl(HOME);
    }
    @Override protected void onActivityResult(int req,int result,Intent data){
        super.onActivityResult(req,result,data);
        if(req==PICK_FILE&&chooser!=null){chooser.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(result,data));chooser=null;}
    }
    @Override protected void onSaveInstanceState(Bundle state){super.onSaveInstanceState(state);if(web!=null)web.saveState(state);}
    @Override public void onBackPressed(){
        if(web!=null&&web.canGoBack()){web.goBack();return;}
        if(loaded) new AlertDialog.Builder(this).setTitle("Muly").setMessage("Uygulamadan çıkmak istiyor musun?").setNegativeButton("Kal",null).setPositiveButton("Çık",(d,w)->finish()).show();
        else super.onBackPressed();
    }
    @Override protected void onDestroy(){if(chooser!=null){chooser.onReceiveValue(null);chooser=null;}if(web!=null){web.destroy();web=null;}super.onDestroy();}
}
