package me.pontue.cliente.activity;

import me.pontue.cliente.EmailValidator;
import me.pontue.cliente.R;
import me.pontue.cliente.R.xml;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.widget.Toast;

public class ConfigurationActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.login);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
			String email = prefs.getString("email", null);
			EmailValidator ev = new EmailValidator();
			boolean validate = ev.validate(email);
			if (!validate) {
				Toast.makeText(this, R.string.email_invalido, Toast.LENGTH_LONG).show();
			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
