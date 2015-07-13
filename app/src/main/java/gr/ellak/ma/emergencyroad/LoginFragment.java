package gr.ellak.ma.emergencyroad;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;


/**
 * Created by billiout on 5/3/2015.
 */
public class LoginFragment extends Fragment {
    Activity act;
    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        act = getActivity();
        rootView = inflater.inflate(R.layout.fragment_login, container, false);
        setHasOptionsMenu(true);

        final BootstrapEditText email = (BootstrapEditText) rootView.findViewById(R.id.email);
        final BootstrapEditText password = (BootstrapEditText) rootView.findViewById(R.id.password);
        final BootstrapButton btnLogin = (BootstrapButton) rootView.findViewById(R.id.btnLogin);
        CheckBox checkBox = (CheckBox) rootView.findViewById(R.id.showPass);

        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (Character.isSpaceChar(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            }
        };

        email.setFilters(new InputFilter[]{filter});
        password.setFilters(new InputFilter[]{filter});

        if(!act.getSharedPreferences("EroadPrefs", 0).getString("user_name", "").equals("")){
            email.setText(act.getSharedPreferences("EroadPrefs", 0).getString("user_email", ""));
        }

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String string_email = email.getText().toString();
                String string_password = password.getText().toString();
                if (!string_password.trim().equals("")) {
                    new CheckIfUserExists(string_email, encryptPassword(string_password)).execute();
                }else {
                    Toast.makeText(act, "The password can not be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }

    private String encryptPassword(String password)
    {
        String sha1 = "";
        try
        {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(password.getBytes("UTF-8"));
            sha1 = byteToHex(crypt.digest());
        }
        catch(NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch(UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return sha1;
    }

    private String byteToHex(final byte[] hash)
    {
        Formatter formatter = new Formatter();
        for (byte b : hash)
        {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    class CheckIfUserExists extends AsyncTask<Void, Void, Boolean>{
        String username;
        String password;
        public CheckIfUserExists(String username, String password){
            this.username = username;
            this.password = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            JSONParser sh = new JSONParser();

            JSONObject jsonObject = null;
            try {
                List<NameValuePair> params1 = new ArrayList<NameValuePair>();
                params1.add(new BasicNameValuePair("username", URLEncoder.encode(username, "UTF-8")));
                jsonObject = sh.makeHttpRequest(act, "http://titansoft.netau.net/check_if_user_exists.php", "GET", params1);
                if(jsonObject.getString("code").equals("1")){
                    return Boolean.valueOf(jsonObject.getString("is_exists"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean result){
           if(result){
               new LoginUser(username, password).execute();
           }
           else {
               System.out.println("As");
               Toast.makeText(act, "The username or password you entered was incorrect. Try again!", Toast.LENGTH_LONG).show();
           }
        }
    }

    class LoginUser extends AsyncTask<Void, Void, JSONObject>{
        String username;
        String password;
        public LoginUser(String username, String password){
            this.username = username;
            this.password = password;
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            JSONParser sh = new JSONParser();

            try {
                List<NameValuePair> params1 = new ArrayList<NameValuePair>();
                params1.add(new BasicNameValuePair("username", URLEncoder.encode(username, "UTF-8")));
                params1.add(new BasicNameValuePair("password", URLEncoder.encode(password, "UTF-8")));
                JSONObject jsonObject = sh.makeHttpRequest(act, "http://titansoft.netau.net/login_user.php", "GET", params1);
                if(jsonObject.getString("code").equals("1")){
                    return jsonObject.getJSONObject("info");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject response){
            if(response != null) {
                try {
                    act.getSharedPreferences("EroadPrefs", 0).edit().putBoolean("is_connected", true).apply();
                    act.getSharedPreferences("EroadPrefs", 0).edit().putInt("user_id", response.getInt("id")).apply();
                    act.getSharedPreferences("EroadPrefs", 0).edit().putString("user_name", response.getString("username")).apply();
                    act.getSharedPreferences("EroadPrefs", 0).edit().putString("user_email", response.getString("email")).apply();
                    Toast.makeText(act, "Successful Login", Toast.LENGTH_SHORT).show();
                    Intent i = getActivity().getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage(getActivity().getBaseContext().getPackageName());
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    act.finish();
                    act.startActivity(i);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            else{
                System.out.println("Sdf");
                Toast.makeText(act, "The username or password you entered was incorrect. Try again!", Toast.LENGTH_LONG).show();
            }
        }
    }
}
