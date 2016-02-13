package org.orangeresearch.beckon;

import android.app.Application;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by james on 2/13/2016.
 */
public class GlobalState extends Application {

    private GoogleSignInAccount acct;
    GoogleSignInOptions gso;

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
