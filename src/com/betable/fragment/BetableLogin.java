package com.betable.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.betable.R;
import com.betable.http.BetableUrl;

public class BetableLogin extends DialogFragment {
    private static final String TAG = "BetableLogin";

    private static final String ACCESS_TOKEN_FRAGMENT = "#access_token=";

    BetableLoginListener listener;
    ProgressDialog loadingDialog;
    WebView browser;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.setActivityAsListener(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        this.browser = (WebView) inflater.inflate(R.layout.betable_login,
                container, false);
        this.initializeBrowser();
        this.browser.loadUrl(BetableUrl.AUTHORIZATION_URL.getAsString("",
                this.createAuthQueryParams()));
        return this.browser;
    }

    // helpers

    private List<BasicNameValuePair> createAuthQueryParams() {
        List<BasicNameValuePair> queryParams = new ArrayList<BasicNameValuePair>();
        queryParams.add(new BasicNameValuePair("state", UUID.randomUUID()
                .toString()));
        queryParams.add(new BasicNameValuePair("response", "code"));
        return null;
    }

    private void initializeBrowser() {
        this.browser.getSettings().setJavaScriptEnabled(true);

        this.browser.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                BetableLogin.this.loadingDialog = ProgressDialog.show(
                        BetableLogin.this.getActivity(), "Betable",
                        "Please wait...");
                int start = url.indexOf(ACCESS_TOKEN_FRAGMENT);
                if (start > -1) {
                    String accessToken = url.substring(start
                            + ACCESS_TOKEN_FRAGMENT.length(), url.length());
                    BetableLogin.this.listener.onSuccessfulLogin(accessToken);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                BetableLogin.this.loadingDialog.dismiss();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

        });
    }

    private void setActivityAsListener(Activity activity) {
        if (!BetableLoginListener.class.isInstance(activity)) {
            throw new IllegalStateException("Activities implementing " + TAG
                    + " must implement the " + TAG
                    + ".BetableLoginListener interface.");
        }
        this.listener = (BetableLoginListener) activity;
    }

    // interfaces

    public interface BetableLoginListener {
        public void onSuccessfulLogin(String accessToken);

        public void onFailedLogin();
    }

}
