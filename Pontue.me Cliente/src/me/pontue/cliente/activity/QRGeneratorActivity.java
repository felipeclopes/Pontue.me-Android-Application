package me.pontue.cliente.activity;

import me.pontue.cliente.EmailValidator;
import me.pontue.cliente.R;
import me.pontue.cliente.qrutil.QRCode;
import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class QRGeneratorActivity extends Activity {

	private ImageView image;
	private Bitmap encodeAsBitmap;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.qrgenerator);
		image = (ImageView) findViewById(R.id.imgQR);
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (getEmail() == null || getEmail().equals("")) {
			callConfig();
		} else {
			drawQR();
		}
	}

	private String getEmail() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		String emailBuscado = prefs.getString("email", null);
		return emailBuscado;
	}

	private void drawQR() {
		try {
			String email = getEmail();
			Log.d("QRGenerator", "Vai desenhar: " + email);
			encodeAsBitmap = QRCode.encodeAsBitmap(email);
		} catch (Exception e) {
			e.printStackTrace();
		}
		image.setImageBitmap(encodeAsBitmap);
	}

	@Override
	protected void onRestart() {
		drawQR();
		super.onRestart();
	}

	private void callConfig() {
		// Intent i = new Intent(this, ConfigurationActivity.class);
		// startActivity(i);

		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.email_alert_dialog);
		dialog.setTitle(R.string.dialog_email);
		dialog.setCancelable(false);
		EditText emailTxt = (EditText) dialog.findViewById(R.id.txtEmailDialog);
		emailTxt.setText(getEmail());

		Button cmdOk = (Button) dialog.findViewById(R.id.btnOk);
		cmdOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				EmailValidator ev = new EmailValidator();
				EditText edit = (EditText) dialog.findViewById(R.id.txtEmailDialog);
				String emailDigitado = edit.getText().toString();
				boolean validate = ev.validate(emailDigitado);
				if (!validate) {
					Toast.makeText(QRGeneratorActivity.this, R.string.email_invalido, Toast.LENGTH_LONG).show();
				} else {
					SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
					Editor editor = prefs.edit();
					editor.putString(getString(R.string.email_pref), emailDigitado);
					editor.commit();
					dialog.dismiss();
					drawQR();
				}
			}
		});
		dialog.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, 0, 0, R.string.menu_config);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (featureId == 0) {
			callConfig();
			return true;
		} else {
			return super.onMenuItemSelected(featureId, item);
		}
	}
}