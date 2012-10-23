package me.pontue.estabelecimento.ui.util;

import me.pontue.estabelecimento.R;
import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

public class SimpleUIUtil {

	public static Dialog createDialog(Context ctx, String title, String msg) {
		Context mContext = ctx.getApplicationContext();
		Dialog dialog = new Dialog(mContext);

		dialog.setContentView(R.layout.dialog);
		dialog.setTitle(title);

		TextView text = (TextView) dialog.findViewById(R.id.txtDialogMsg);
		text.setText(msg);
		return dialog;
	}

}
