package me.pontue.estabelecimento.ui.activity;

import me.pontue.estabelecimento.R;
import me.pontue.estabelecimento.ui.util.UiConstants;
import me.pontue.estabelecimento.ws.WSFactory;
import me.pontue.estabelecimento.ws.WSPontuemeAsyncTask;
import me.pontue.estabelecimento.ws.WSPontuemeDetails;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class CouponActivity extends Activity implements OnClickListener {

	private TextView txtMsg;
	private TextView txtRecompensa;
	private Long beneficio;
	private Button btnOk;
	private String token;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// full screen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.cupom);
		txtMsg = (TextView) findViewById(R.id.txtCupomMsg);
		txtRecompensa = (TextView) findViewById(R.id.txtCupomRecompensa);
		btnOk = (Button) findViewById(R.id.btnCupomOk);

		WSPontuemeAsyncTask ws = WSFactory.getWSPontueMeInstance();
		WSPontuemeDetails details = ws.getWSPontuemeDetails();

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			beneficio = (Long) extras.get(UiConstants.BENEFICIO_EXTRA);
			token = (String) extras.get(UiConstants.TOKEN);
		} else {
			throw new RuntimeException(getString(R.string.erro_coupom_receber));
		}

		txtRecompensa.setText(getString(R.string.cupom_comeco) + details.getBenefitSelectName());
		btnOk.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v == btnOk) {
			Intent i = new Intent(this, HomeActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			i.putExtra(UiConstants.TOKEN, token);
			this.startActivity(i);
		}
	}

}
