package org.orangeresearch.beckon;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by james on 2/13/2016.
 */
public class GlobalState extends Application {

    private GoogleSignInAccount acct;
    GoogleSignInOptions gso;
    private boolean logOut = false;

    public boolean isLogOut() {
        return logOut;
    }

    public void setLogOut(boolean logOut) {
        this.logOut = logOut;
    }




    public GoogleSignInOptions getGso() {
        return gso;
    }

    public void setGso(GoogleSignInOptions gso) {
        this.gso = gso;
    }

    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    public void setGoogleApiClient(GoogleApiClient googleApiClient) {
        mGoogleApiClient = googleApiClient;
    }

    private GoogleApiClient mGoogleApiClient;


    public void setAcct ( GoogleSignInAccount acct){

        this.acct = acct;

    }

    public GoogleSignInAccount getAcct() {
        return this.acct;
    }

    public void cleanup()
    {
        acct = null;
        gso = null;
    }


}

