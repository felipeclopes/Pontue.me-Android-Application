package me.pontue.estabelecimento.ui.activity;

import me.pontue.estabelecimento.R;
import me.pontue.estabelecimento.ui.util.UiConstants;
import me.pontue.estabelecimento.ws.ResponseStatus;
import me.pontue.estabelecimento.ws.WSFactory;
import me.pontue.estabelecimento.ws.WSPontuemeAsyncTask;
import me.pontue.estabelecimento.ws.WSPontuemeDetails;
import me.pontue.estabelecimento.ws.util.WSRequestEnum;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.google.zxing.client.android.CaptureActivity;

public class HomeActivity extends Activity implements OnClickListener {

	private ImageView btnQR;
	private ImageView imgEmail;
	// private EditText editTxtEmail;
	// private TextView txtEmail;
	private int READ_CODE = 1;
	private WSPontuemeAsyncTask ws;
	private String token;
	private EmailValidator emailValidator;
	private Dialog emailCollectorDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// full screen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.home);
		emailValidator = new EmailValidator();

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			token = extras.getString(UiConstants.TOKEN);
		}
		if (token == null) {
			Intent i = new Intent(this, LoginActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			this.startActivity(i);
			Toast.makeText(this, "Seu login expirou! Favor fazer login novamente!", Toast.LENGTH_LONG).show();
			finish();
		}

		btnQR = (ImageView) findViewById(R.id.imageQR);
		// editTxtEmail = (EditText) findViewById(R.id.edittxtEmail);
		// txtEmail = (TextView) findViewById(R.id.txtEmail);
		imgEmail = (ImageView) findViewById(R.id.imgHomeEmail);

		btnQR.setOnClickListener(this);
		imgEmail.setOnClickListener(this);
		// txtEmail.setOnClickListener(this);

		// hide keyboard
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		// InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		// imm.hideSoftInputFromWindow(editTxtEmail.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);

		// editTxtEmail.setOnEditorActionListener(new OnEditorActionListener() {
		// @Override
		// public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		// if (actionId == EditorInfo.IME_ACTION_GO) {
		// String emailA = editTxtEmail.getText().toString();
		// EmailValidator ev = new EmailValidator();
		// if (ev.validate(emailA)) {
		// callCheckin(emailA);
		// return true;
		// } else {
		// Toast.makeText(HomeActivity.this, "Ops... email inválido :(", Toast.LENGTH_SHORT).show();
		// }
		// }
		// return false;
		// }
		// });
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (emailCollectorDialog != null) {
			emailCollectorDialog.dismiss();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == READ_CODE) {
			if (resultCode == Activity.RESULT_OK) {
				String qrread = data.getStringExtra("SCAN_RESULT");
				callCheckin(qrread);
			}
		}
	}

	private void callCheckin(String read) {
		ws = WSFactory.getWSPontueMeInstance();
		ws.setContext(this);
		AsyncTask<String, Long, ResponseStatus> task = ws.execute(WSRequestEnum.checkin.name(), read);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		if (emailCollectorDialog != null) {
			emailCollectorDialog.dismiss();
		}
	}

	private void showEmailCollectorDialog() {
		emailCollectorDialog = new Dialog(this);
		emailCollectorDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		emailCollectorDialog.setContentView(R.layout.emailcollector_dialog);
		emailCollectorDialog.setCancelable(true);
		TextView cmdOk = (TextView) emailCollectorDialog.findViewById(R.id.txtEmailOK);
		final EditText txtEmail = (EditText) emailCollectorDialog.findViewById(R.id.edittxtEmail);
		cmdOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				callEmailValidator(txtEmail);
				emailCollectorDialog.dismiss();
			}

		});

		txtEmail.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_GO) {
					callEmailValidator(txtEmail);
				}
				return false;
			}
		});
		emailCollectorDialog.show();
	}

	private void callEmailValidator(EditText editEmail) {
		String emailtyped = editEmail.getText().toString();
		if ("".equals(emailtyped)) {
			Toast.makeText(HomeActivity.this, R.string.erro_home_email_nao_digitado, Toast.LENGTH_LONG).show();
			return;
		}

		EmailValidator ev = new EmailValidator();
		if (ev.validate(emailtyped)) {
			callCheckin(emailtyped);
		} else {
			Toast.makeText(HomeActivity.this, "Ops... email inválido :(", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onClick(View v) {
		if (v == btnQR) {
			// Intent intent = new Intent("com.google.zxing.client.android.SCAN");
			Intent intent = new Intent(this, CaptureActivity.class);
			intent.putExtra("SCAN_MODE", UiConstants.QR_CODE_MODE);
			startActivityForResult(intent, READ_CODE);
			// } else if (v == txtEmail) {
			// if ("".equals(editTxtEmail.getText().toString())) {
			// Toast.makeText(this, R.string.erro_home_email_nao_digitado, Toast.LENGTH_LONG).show();
			// return;
			// }
			//
			// String emailA = editTxtEmail.getText().toString();
			// EmailValidator ev = new EmailValidator();
			// if (ev.validate(emailA)) {
			// callCheckin(emailA);
			// } else {
			// Toast.makeText(this, "Ops... email inválido :(", Toast.LENGTH_SHORT).show();
			// }
		} else if (v == imgEmail) {
			showEmailCollectorDialog();
		}
	}
}
