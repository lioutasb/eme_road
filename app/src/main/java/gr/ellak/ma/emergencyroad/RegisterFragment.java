package gr.ellak.ma.emergencyroad;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
 * Created by billiout on 28/3/2015.
 */
public class RegisterFragment extends Fragment {
    Activity act;
    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        act = getActivity();
        rootView = inflater.inflate(R.layout.fragment_register, container, false);
        setHasOptionsMenu(true);

        final BootstrapEditText email = (BootstrapEditText) rootView.findViewById(R.id.email);
        final BootstrapEditText username = (BootstrapEditText) rootView.findViewById(R.id.username);
        final BootstrapEditText password1 = (BootstrapEditText) rootView.findViewById(R.id.password1);
        final BootstrapEditText password2 = (BootstrapEditText) rootView.findViewById(R.id.password2);
        final BootstrapEditText firstname = (BootstrapEditText) rootView.findViewById(R.id.firstname);
        final BootstrapEditText lastname = (BootstrapEditText) rootView.findViewById(R.id.lastname);
        final BootstrapButton btnRegister = (BootstrapButton) rootView.findViewById(R.id.btnRegister);

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
        password1.setFilters(new InputFilter[]{filter});
        password2.setFilters(new InputFilter[]{filter});

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String string_email = email.getText().toString();
                String string_password1 = password1.getText().toString();
                String string_password2 = password2.getText().toString();
                String string_firstname = firstname.getText().toString();
                String string_lastname = lastname.getText().toString();
                String string_username = username.getText().toString();

                boolean valid = true;
                if(string_email.equals("")){
                    Toast.makeText(act, "The e-mail cannot be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(string_password1.equals("")){
                    Toast.makeText(act, "The password cannot be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(string_password2.equals("")){
                    Toast.makeText(act, "The Confirm password cannot be empty!", Toast.LENGTH_SHORT).show();
                    return ;
                }

                if(string_username.equals("")){
                    Toast.makeText(act, "The Username cannot be empty!", Toast.LENGTH_SHORT).show();
                    return ;
                }

                if(isValidEmail(string_email)){
                    if(string_password1.equals(string_password2)){
                        if(isValidPasswordLength(string_password1)){
                            if(isValidPasswordCharacters(string_password1)){
                                new CheckIfUserExists(string_email, encryptPassword(string_password1), string_firstname, string_lastname, string_username).execute();
                            }
                            else {
                                Toast.makeText(act, "The password contains characters that aren't allowed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Toast.makeText(act, "The password must be at least 6(six) characters long.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(act, "The passwords on fields Password and Confirm password not matching.", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(act, "Invalid e-mail!", Toast.LENGTH_SHORT).show();
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

    class CheckIfUserExists extends AsyncTask<Void, Void, Boolean> {
        String email;
        String password;
        String firstname;
        String lastname;
        String username;

        public CheckIfUserExists(String email, String password, String firstname, String lastname, String username){
            this.email = email;
            this.password = password;
            this.firstname = firstname;
            this.lastname = lastname;
            this.username = username;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            JSONParser sh = new JSONParser();

            try {
                List<NameValuePair> params1 = new ArrayList<NameValuePair>();
                params1.add(new BasicNameValuePair("username", URLEncoder.encode(username, "UTF-8")));
                JSONObject jsonObject = sh.makeHttpRequest(act, "http://titansoft.netau.net/check_if_user_exists.php", "GET", params1);
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
                Toast.makeText(act, "Username Already in Use. Please try again!", Toast.LENGTH_LONG).show();
                //YoYo.with(Techniques.Shake).duration(700).playOn(rootView.findViewById(R.id.edittexts_rel));
            }
            else {
                new RegisterUser(email, password, firstname, lastname, username).execute();
            }
        }
    }

    class RegisterUser extends AsyncTask<Void, Void, JSONObject> {
        String email;
        String password;
        String firstname;
        String lastname;
        String username;

        public RegisterUser(String email, String password, String firstname, String lastname, String username){
            this.email = email;
            this.password = password;
            this.firstname = firstname;
            this.lastname = lastname;
            this.username = username;
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            JSONParser sh = new JSONParser();

            try {
                List<NameValuePair> params1 = new ArrayList<NameValuePair>();
                params1.add(new BasicNameValuePair("username", URLEncoder.encode(username, "UTF-8")));
                params1.add(new BasicNameValuePair("password", URLEncoder.encode(password, "UTF-8")));
                params1.add(new BasicNameValuePair("email", URLEncoder.encode(email, "UTF-8")));
                params1.add(new BasicNameValuePair("first_name", URLEncoder.encode(firstname, "UTF-8")));
                params1.add(new BasicNameValuePair("last_name", URLEncoder.encode(lastname, "UTF-8")));
                JSONObject jsonObject = sh.makeHttpRequest(act, "http://titansoft.netau.net/register_user.php", "GET", params1);
                if(jsonObject.getString("code").equals("1")){
                    return jsonObject;
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
                    act.getSharedPreferences("EroadPrefs", 0).edit().putInt("user_id", response.getInt("userid")).apply();
                    act.getSharedPreferences("EroadPrefs", 0).edit().putString("user_name", username).apply();
                    act.getSharedPreferences("EroadPrefs", 0).edit().putString("user_email", email).apply();
                    Toast.makeText(act, "Successful Registration", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(act, "Email Address Already in Use. Please try again!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean isValidEmail(CharSequence target) {
        return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public boolean isValidPasswordLength(String str){
        return str.length() >= 6;
    }

    public boolean isValidPasswordCharacters(String str){
        System.out.println(str);
        return !str.matches(".*[^A-Za-z0-9!+@#$%^&*()_].*");
    }
}
