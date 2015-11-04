package metropolia.fi.suondbubbles.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.Serializable;

import metropolia.fi.suondbubbles.R;
import metropolia.fi.suondbubbles.apiConnection.ServerConnection;

/**
 * Java class to handle reusable __static__ attributes and methods along the application.
 */
 public abstract class SoundBubbles {

    public static ServerConnection serverConnection = null;


    public static void hideKeyboard(Context ctx, View v){
        v.clearFocus();
        InputMethodManager imm = (InputMethodManager)ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public static boolean userIsLogged(){
        if(serverConnection == null){
            return false;
        }else if(!serverConnection.isLogged){
            return false;
        }
        return true;
    }

    public static Object getObjectFromIntent(Activity ctx , String id){
        Bundle extras = ctx.getIntent().getExtras();
        if(extras == null) {
            new Exception();
        } else {
            return extras.getSerializable(id);
        }
        return null;
    }

    public static void setObjectToIntent(Intent intent, Serializable obj, String id){
        intent.putExtra(id, obj);
    }

    public static void openLoginActivity(Activity ctx){
        Intent intent = new Intent(ctx, LoginActivity.class);
        ctx.startActivity(intent);
        //ctx.finish(); // destroy that Activity
    }

    public static void logout(){
        serverConnection = null;
    }

    public static void setSimpleSnackbar(View view, String text){
        Snackbar.make(view, text, Snackbar.LENGTH_LONG)
                .setAction("CLOSE", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {}
                })
                .show();
    }

}
