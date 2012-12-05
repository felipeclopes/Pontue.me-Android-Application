package me.pontue.estabelecimento.ui.activity;

import java.util.ArrayList;

import me.pontue.estabelecimento.R;
import me.pontue.estabelecimento.ui.util.UiConstants;
import me.pontue.estabelecimento.ws.ResponseStatus;
import me.pontue.estabelecimento.ws.WSFactory;
import me.pontue.estabelecimento.ws.WSPontuemeAsyncTask;
import me.pontue.estabelecimento.ws.entity.Beneficio;
import me.pontue.estabelecimento.ws.util.WSRequestEnum;
import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class CheckinActivity extends Activity {

	private ListView lv;
	private final Long NO_BEN_ID = -1L;
	private TextView txtTituloCheckin;
	private Long pontosDisponiveis;
	private WSPontuemeAsyncTask ws;
	private String token;
	private String email;
	private ArrayList<Beneficio> beneficios;
	private int soundID;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// full screen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		SoundPool soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
				AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
				float actualVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
				float maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
				float volume = actualVolume / maxVolume;
				soundPool.play(soundID, volume, volume, 1, 0, 1f);
			}
		});
		soundID = soundPool.load(this, R.raw.alerttone, 1);

		setContentView(R.layout.checkin);

		lv = (ListView) findViewById(R.id.listaCheckouts);
		txtTituloCheckin = (TextView) findViewById(R.id.txtTituloCheckin2);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			email = extras.getString(UiConstants.EXTRA_EMAIL);
			pontosDisponiveis = extras.getLong(UiConstants.PONTOS_EXTRA);
			beneficios = (ArrayList<Beneficio>) extras.get(UiConstants.BENEFICIOS_EXTRA);
			token = (String) extras.get(UiConstants.TOKEN);
		}

		txtTituloCheckin.setText(String.valueOf(pontosDisponiveis));

		// Toast.makeText(this, "Voce tem " + pontos + " pontos nesse estabelecimento.", Toast.LENGTH_LONG).show();

		if (email == null && pontosDisponiveis == null && beneficios == null) {
			throw new RuntimeException(getString(R.string.erro_checkin));
		}

		Beneficio noBen = new Beneficio();
		noBen.setNome(getString(R.string.cupom_nao_agora));
		noBen.setTexto("");
		noBen.setId(NO_BEN_ID);
		beneficios.add(noBen);

		Beneficio[] bensArray = new Beneficio[beneficios.size()];
		for (int i = 0; i < beneficios.size(); i++) {
			bensArray[i] = beneficios.get(i);
		}

		BeneficiosAdapter adapt = new BeneficiosAdapter(this, bensArray);
		lv.setAdapter(adapt);
	}

	public class BeneficiosAdapter extends ArrayAdapter<Beneficio> implements OnClickListener {
		private final Context context;
		private final Beneficio[] values;

		public BeneficiosAdapter(Context context, Beneficio[] values) {
			super(context, R.layout.checkin_item, values);
			this.context = context;
			this.values = values;
		}

		@Override
		public int getCount() {
			return values.length;
		}

		@Override
		public Beneficio getItem(int position) {
			return values[position];
		}

		@Override
		public long getItemId(int position) {
			return values[position].getId();
		}

		@Override
		public int getPosition(Beneficio item) {
			for (int i = 0; i < values.length; i++) {
				if (values[i].equals(item)) {
					return i;
				}
			}
			return -1;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.checkin_item, parent, false);
			LinearLayout llBeneficio = (LinearLayout) rowView.findViewById(R.id.llItemBeneficio);
			TextView textView1 = (TextView) rowView.findViewById(R.id.txtCheckin1);
			TextView textView2 = (TextView) rowView.findViewById(R.id.txtCheckin2);
			TextView textViewPontos = (TextView) rowView.findViewById(R.id.txtCheckinPoints);
			TextView beneficioId = (TextView) rowView.findViewById(R.id.txtBeneficioId);

			String nome = values[position].getNome();
			Integer pontos = values[position].getPontos();
			String texto = values[position].getTexto();

			if (nome != null)
				textView1.setText(nome);

			if (texto != null)
				textView2.setText(texto);

			if (pontos != null)
				textViewPontos.setText(String.valueOf(pontos));
			else
				textViewPontos.setText("X");

			if (pontos != null && pontosDisponiveis < pontos) {
				textView1.setTextColor(getResources().getColor(R.color.beneficio_txt_titulo_npode));
			}

			beneficioId.setText(String.valueOf(values[position].getId()));

			if (position % 2 == 0) {
				textViewPontos.setBackgroundResource(R.color.beneficio_background_par);
				textViewPontos.setTextColor(getResources().getColor(R.color.beneficio_txt_par));
			} else {
				textViewPontos.setBackgroundResource(R.color.beneficio_background_impar);
				textViewPontos.setTextColor(getResources().getColor(R.color.beneficio_txt_impar));
			}

			llBeneficio.setOnClickListener(this);

			return rowView;
		}

		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.llItemBeneficio) {
				TextView beneficioId = (TextView) v.findViewById(R.id.txtBeneficioId);
				String id = beneficioId.getText().toString();
				Long idL = Long.valueOf(id);
				if (NO_BEN_ID.equals(idL)) {
					finish();
				} else {
					WSPontuemeAsyncTask ws = WSFactory.getWSPontueMeInstance();
					TextView nomeBeneficio = (TextView) v.findViewById(R.id.txtCheckin1);
					ws.getWSPontuemeDetails().setBenefitSelect(nomeBeneficio.getText().toString());
					ws.setContext(CheckinActivity.this);
					AsyncTask<String, Long, ResponseStatus> task = ws.execute(WSRequestEnum.coupons.name(), email, id);
				}
			}
		}
	}
}