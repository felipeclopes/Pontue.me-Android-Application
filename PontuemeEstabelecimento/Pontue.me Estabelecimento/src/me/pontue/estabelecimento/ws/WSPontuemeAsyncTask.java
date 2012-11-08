package me.pontue.estabelecimento.ws;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.pontue.estabelecimento.R;
import me.pontue.estabelecimento.ui.activity.CheckinActivity;
import me.pontue.estabelecimento.ui.activity.EmailValidator;
import me.pontue.estabelecimento.ui.activity.HomeActivity;
import me.pontue.estabelecimento.ui.util.UIUtil;
import me.pontue.estabelecimento.ui.util.UiConstants;
import me.pontue.estabelecimento.util.AndroidUtil;
import me.pontue.estabelecimento.ws.ResponseStatus.StatusEnum;
import me.pontue.estabelecimento.ws.entity.Beneficio;
import me.pontue.estabelecimento.ws.util.WSRequestEnum;
import me.pontue.estabelecimento.ws.util.WSUtil;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class WSPontuemeAsyncTask extends AsyncTask<String, Long, ResponseStatus> {

	// private final String URL_WS = "http://pontueme.webbyapp.com/api/v1/";
	// //OLD
	private final String URL_WS = "http://www.pontue.me/api/v1/"; // PROD
	// private final String URL_WS = "http://192.168.1.9:3000/api/v1/"; // LOCAL
	private WSRequestEnum actualTask;
	private Context ctx;
	private ProgressDialog dialog;
	private TextView errorLabel;
	private WSPontuemeDetails details;

	public WSPontuemeAsyncTask(WSPontuemeDetails details) {
		this.details = details;
	}

	public WSPontuemeDetails getWSPontuemeDetails() {
		return details;
	}

	public void setContext(Context ctx) {
		this.ctx = ctx;
	}

	public void setErroLabel(TextView txtErro) {
		errorLabel = txtErro;
	}

	public void setToken(String token) {
		details.setToken(token);
	}

	public void setEmailPassword(String email, String password) {
		details.setEmail(email);
		details.setPassword(password);
	}

	private List<String> getMessagesFromExceptions(List<Exception> exceptions) {
		ArrayList<String> messages = new ArrayList<String>();
		for (Exception exception : exceptions) {
			messages.add(exception.getMessage());
		}
		return messages;
	}

	@Override
	protected void onPostExecute(ResponseStatus result) {
		super.onPostExecute(result);
		if (dialog.isShowing()) {
			dialog.dismiss();
		}

		if (StatusEnum.Error.equals(result.getStatus())) {
			if (result.getMessages() != null && result.getMessages().size() > 0) {
				UIUtil.createWarningAlertDialog(ctx, result.getMessages()).show();
			} else {
				UIUtil.createWarningAlertDialog(ctx, getMessagesFromExceptions(result.getExceptions())).show();
			}

			return;
		}

		if (StatusEnum.Warning.equals(result.getStatus())) {
			List<String> messages = result.getMessages();
			StringBuilder sb = new StringBuilder();
			for (String s : messages) {
				sb.append(s).append(" - ");
			}

			sb.replace(sb.length() - 3, sb.length(), "");
			if (errorLabel != null) {
				errorLabel.setText(sb.toString());
			} else {
				Toast.makeText(ctx, sb.toString(), Toast.LENGTH_LONG).show();
			}
			return;
		}

		if (result.getExceptions() != null && result.getExceptions().size() > 0) {
			UIUtil.createWarningAlertDialog(ctx, getMessagesFromExceptions(result.getExceptions())).show();
			return;
		}

		if (actualTask == WSRequestEnum.checkin) {
			Intent i = new Intent(ctx, CheckinActivity.class);
			i.putExtra(UiConstants.EXTRA_EMAIL, details.getEmail());
			i.putExtra(UiConstants.PONTOS_EXTRA, details.getPoints());
			i.putExtra(UiConstants.BENEFICIOS_EXTRA, details.getBeneficios());
			i.putExtra(UiConstants.TOKEN, details.getToken());
			ctx.startActivity(i);
		} else if (actualTask == WSRequestEnum.login) {
			Intent i = new Intent(ctx, HomeActivity.class);
			i.putExtra(UiConstants.TOKEN, details.getToken());
			ctx.startActivity(i);
		} else if (actualTask == WSRequestEnum.coupons) {
			// Intent i = new Intent(ctx, CouponActivity.class);
			// i.putExtra(UiConstants.BENEFICIO_EXTRA,
			// details.getBenefitSelected());
			// i.putExtra(UiConstants.TOKEN, details.getToken());
			// ctx.startActivity(i);

			showDialogBenefitCollected();
		}
	}

	private void showDialogBenefitCollected() {
		final Dialog dialog = new Dialog(ctx);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.resgate_dialog);
		// dialog.setTitle("Parabéns!");
		dialog.setCancelable(false);
		WSPontuemeAsyncTask ws = WSFactory.getWSPontueMeInstance();
		WSPontuemeDetails details = ws.getWSPontuemeDetails();
		if (details != null) {
			final String token = details.getToken();
			TextView text = (TextView) dialog.findViewById(R.id.txtDialogResgateText);
			text.setText(details.getBenefitSelectName());
			Button cmdOk = (Button) dialog.findViewById(R.id.btnOk);
			cmdOk.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
					Intent i = new Intent(ctx, HomeActivity.class);
					i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					i.putExtra(UiConstants.TOKEN, token);
					ctx.startActivity(i);
				}
			});
			dialog.show();
		}
	}

	@Override
	protected void onPreExecute() {
		dialog = new ProgressDialog(ctx);
		dialog.setMessage("Aguarde um momento...");
		dialog.setIndeterminate(true);
		dialog.show();
		super.onPreExecute();
	}

	@Override
	protected ResponseStatus doInBackground(String... params) {
		if (!AndroidUtil.haveInternet(ctx)) {
			ResponseStatus resp = new ResponseStatus();
			resp.addMessage(ctx.getString(R.string.no_internet));
			resp.setStatus(StatusEnum.Error);
			return resp;
		}

		if (params != null) {
			actualTask = WSRequestEnum.valueOf(params[0]);
			if (actualTask.equals(WSRequestEnum.login)) {
				return doLogin(details.getEmail(), details.getPassword());
			} else if (actualTask.equals(WSRequestEnum.checkin)) {
				details.setEmail(params[1]);
				return doCheckin(details.getEmail());
			} else if (actualTask.equals(WSRequestEnum.coupons)) {
				details.setBenefitSelected(Long.valueOf(params[2]));
				return getCupom(params[1], params[2]);
			} else {
				throw new RuntimeException(ctx.getString(R.string.erro_app_parametro));
			}
		}
		return null;
	}

	public ArrayList<Beneficio> getBeneficios() {
		return details.getBeneficios();
	}

	public long getPoints() {
		return details.getPoints();
	}

	private ResponseStatus doLogin(String email, String password) {
		ResponseStatus resposeStatus = new ResponseStatus();
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(URL_WS + "tokens.json");

		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("email", email)); // versa
																		// nova
			// nameValuePairs.add(new BasicNameValuePair("name", email));
			nameValuePairs.add(new BasicNameValuePair("password", password));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpResponse response = httpclient.execute(httppost);
			String responseBody = WSUtil.getResponseBody(response.getEntity());
			try {
				JSONObject jsonObj = new JSONObject(responseBody);
				details.setToken(jsonObj.getString("token"));
				resposeStatus.setStatus(StatusEnum.Ok);
			} catch (JSONException e) {
				resposeStatus.addException(e);
				resposeStatus.addMessage(ctx.getString(R.string.erro_usuariosenha));
				resposeStatus.setStatus(StatusEnum.Warning);
			}
		} catch (ClientProtocolException e) {
			resposeStatus.addException(e);
			resposeStatus.setStatus(StatusEnum.Error);
		} catch (IOException e) {
			resposeStatus.addException(e);
			resposeStatus.setStatus(StatusEnum.Error);
		}
		return resposeStatus;
	}

	private ResponseStatus doCheckin(String codigoLido) {
		ResponseStatus resposeStatus = new ResponseStatus();
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(URL_WS + "checkins.json?auth_token=" + details.getToken());
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			EmailValidator ev = new EmailValidator();
			if (ev.validate(codigoLido)) {
				nameValuePairs.add(new BasicNameValuePair("email", codigoLido));
			} else {
				nameValuePairs.add(new BasicNameValuePair("code", codigoLido));
			}
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpResponse response = httpclient.execute(httppost);

			String responseBody = WSUtil.getResponseBody(response.getEntity());
			try {
				JSONObject jsonObj = new JSONObject(responseBody);
				details.setPoints(jsonObj.getLong("points"));
				JSONArray benefitsArr = jsonObj.getJSONArray("benefits");
				details.setBeneficios(null);
				for (int i = 0; i < benefitsArr.length(); i++) {
					JSONObject jsonObject = benefitsArr.getJSONObject(i);
					Beneficio ben = new Beneficio();
					ben.setTexto(jsonObject.getString("description"));
					ben.setNome(jsonObject.getString("name"));
					ben.setId(jsonObject.getLong("id"));
					ben.setPontos(jsonObject.getInt("checkins_needed"));
					details.addBeneficio(ben);
				}
				resposeStatus.setStatus(StatusEnum.Ok);
			} catch (JSONException e) {
				resposeStatus.addException(e);
				resposeStatus.addMessage(ctx.getString(R.string.erro_checkin));
				resposeStatus.setStatus(StatusEnum.Warning);
			}
		} catch (ClientProtocolException e) {
			resposeStatus.addException(e);
			resposeStatus.setStatus(StatusEnum.Error);
		} catch (IOException e) {
			resposeStatus.addException(e);
			resposeStatus.setStatus(StatusEnum.Error);
		}
		return resposeStatus;
	}

	private ResponseStatus getCupom(String codigoLido, String benefitId) {
		ResponseStatus resposeStatus = new ResponseStatus();
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(URL_WS + "coupons.json?auth_token=" + details.getToken());

		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			EmailValidator ev = new EmailValidator();
			if (ev.validate(codigoLido)) {
				nameValuePairs.add(new BasicNameValuePair("email", codigoLido));
			} else {
				nameValuePairs.add(new BasicNameValuePair("code", codigoLido));
			}
			// nameValuePairs.add(new BasicNameValuePair("email", mail)); old
			nameValuePairs.add(new BasicNameValuePair("benefit_id", benefitId));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpResponse response = httpclient.execute(httppost);

			String responseBody = WSUtil.getResponseBody(response.getEntity());
			try {
				JSONObject jsonObj = new JSONObject(responseBody);
				boolean b = jsonObj.getBoolean("result");
				if (b) {
					resposeStatus.setStatus(StatusEnum.Ok);
				} else {
					resposeStatus.setStatus(StatusEnum.Warning);
					resposeStatus.addMessage(ctx.getString(R.string.erro_cupom));
				}
			} catch (JSONException e) {
				resposeStatus.setStatus(StatusEnum.Error);
				resposeStatus.addMessage(ctx.getString(R.string.erro_cupom2));
				resposeStatus.addException(e);
			}
		} catch (ClientProtocolException e) {
			resposeStatus.addException(e);
			resposeStatus.setStatus(StatusEnum.Error);
		} catch (IOException e) {
			resposeStatus.addException(e);
			resposeStatus.setStatus(StatusEnum.Error);
		}
		return resposeStatus;
	}
}
