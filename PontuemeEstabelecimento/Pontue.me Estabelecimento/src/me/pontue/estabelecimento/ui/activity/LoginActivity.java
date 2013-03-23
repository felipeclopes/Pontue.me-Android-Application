package me.pontue.estabelecimento.ui.activity;

import com.google.analytics.tracking.android.EasyTracker;

import me.pontue.estabelecimento.R;
import me.pontue.estabelecimento.ws.ResponseStatus;
import me.pontue.estabelecimento.ws.WSFactory;
import me.pontue.estabelecimento.ws.WSPontuemeAsyncTask;
import me.pontue.estabelecimento.ws.util.WSRequestEnum;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends Activity implements OnClickListener {

	private Button button;
	private EditText user;
	private EditText password;
	private TextView txtErro;
	private WSPontuemeAsyncTask ws;
	private AsyncTask<String, Long, ResponseStatus> task;

	/*
	 * private static final String SENHA = "123"; private static final String
	 * LOGIN = "Mc Donalds";
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.login);
		user = (EditText) findViewById(R.id.editTxtLoginUser);
		password = (EditText) findViewById(R.id.editTxtLoginPassword);
		button = (Button) findViewById(R.id.txtLoginButton);
		txtErro = (TextView) findViewById(R.id.txtLoginErro);
		button.setOnClickListener(this);

		txtErro.setText("");

		// hide keyboard
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(user.getWindowToken(),
				InputMethodManager.HIDE_IMPLICIT_ONLY);
		imm.hideSoftInputFromWindow(password.getWindowToken(),
				InputMethodManager.HIDE_IMPLICIT_ONLY);
		
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		EasyTracker.getInstance().activityStart(this);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		EasyTracker.getInstance().activityStop(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		if (v == button) {
			ws = WSFactory.getWSPontueMeInstance();
			String userTyped = user.getText().toString();
			String passwordTyped = password.getText().toString();
			if ("".equals(userTyped) && "".equals(passwordTyped)) {
				userTyped = "cadastro@pontue.me"; // new
				passwordTyped = "p0ntu3m3";

				// userTyped = "Mc Donalds"; // old
				// passwordTyped = "123";

				// userTyped = "cadastro@pontue.me";
				// passwordTyped = "p0ntu3m3";
			}

			ws.setEmailPassword(userTyped, passwordTyped);
			ws.setContext(this);
			ws.setErroLabel(txtErro);
			task = ws.execute(WSRequestEnum.login.name());
		}
	}
}
