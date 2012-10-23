package me.pontue.cliente.ui.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import me.pontue.cliente.R;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class UIUtil {

	public static Dialog createOkAlertDialog(Context context, int textId) {
		Resources resources = context.getResources();
		return createOkAlertDialog(context, resources.getString(textId));
	}

	public static Dialog createOkAlertDialog(Context context, String textStr) {
		return createAlertDialog(context, textStr, R.drawable.icon_ok_64x64);
	}

	public static Dialog createInfoAlertDialog(Context context, int textId) {
		Resources resources = context.getResources();
		return createInfoAlertDialog(context, resources.getString(textId));
	}

	public static Dialog createInfoAlertDialog(Context context, String textStr) {
		return createAlertDialog(context, textStr, R.drawable.icon_info_64x64);
	}

	public static Dialog createWarningAlertDialog(Context context, int textId) {
		Resources resources = context.getResources();
		return createWarningAlertDialog(context, resources.getString(textId));
	}

	public static Dialog createWarningAlertDialog(Context context, String textStr) {
		return createAlertDialog(context, textStr, R.drawable.icon_warning_64x64);
	}

	public static Dialog createInfoAlertDialog(Context context, String textStr, String title) {
		return createAlertDialog(context, textStr, title, R.drawable.icon_info_64x64);
	}

	public static Dialog createAlertDialog(Context context, String textStr, String title, int imageId) {
		final Dialog dialog = new Dialog(context);
		dialog.setContentView(R.layout.custom_alert_dialog);
		dialog.setTitle(title);

		ImageView image = (ImageView) dialog.findViewById(R.id.alertIcon);
		image.setImageResource(imageId);

		TextView text = (TextView) dialog.findViewById(R.id.alertText);
		text.setText(textStr);

		Button cmdOk = (Button) dialog.findViewById(R.id.btnOk);
		cmdOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		return dialog;
	}

	public static Dialog createAlertDialog(Context context, String textStr, int imageId) {
		return createAlertDialog(context, textStr, "Titulo", imageId);
	}

	public static Dialog createYesOrNoDialog(Context context, final DialogInterface.OnClickListener dialogClickListener, String msg) {

		Resources resources = context.getResources();
		final Dialog dialog = new Dialog(context);
		dialog.setContentView(R.layout.yes_or_no_alert_dialog);
		dialog.setTitle("Titulo");

		TextView text = (TextView) dialog.findViewById(R.id.alertText);
		text.setText(msg);

		final Button cmdYes = (Button) dialog.findViewById(R.id.btnYes);
		final Button cmdNo = (Button) dialog.findViewById(R.id.btnNo);

		OnClickListener lister = new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();

				if (cmdYes == v) {
					dialogClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
				} else {
					dialogClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
					dialog.dismiss();
				}
			}
		};

		cmdYes.setOnClickListener(lister);
		cmdNo.setOnClickListener(lister);

		return dialog;
	}

	public static Dialog createWarningAlertDialog(Context context, List<String> textStr) {
		return createAlertDialog(context, textStr, R.drawable.icon_warning_64x64);
	}

	public static Dialog createAlertDialog(Context context, List<String> textStr, int imageId) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		Resources resources = context.getResources();
		final Dialog dialog = new Dialog(context);

		dialog.setContentView(R.layout.summary_alert_dialog);
		dialog.setTitle("Oooops");
		ImageView image = (ImageView) dialog.findViewById(R.id.alertIcon);
		image.setImageResource(imageId);

		TextView summarySubtitleText = (TextView) dialog.findViewById(R.id.summarySubtitleText);
		// summarySubtitleText.setText(textStr.remove(0));

		TableLayout summaryContainer = (TableLayout) dialog.findViewById(R.id.summaryDialogMsgContainer);

		for (int i = 0; i < textStr.size(); i++) {
			View row = inflater.inflate(R.layout.summary_alert_item_row, null);
			TextView item = (TextView) row.findViewById(R.id.summaryItemText);
			item.setText(textStr.get(i));
			summaryContainer.addView(row);
		}

		Button cmdOk = (Button) dialog.findViewById(R.id.btnOk);
		cmdOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		return dialog;
	}

	public static void getChilds(View view, List<View> childs, Class<? extends View> field) {
		getChilds(view, childs);
		List<View> result = new ArrayList<View>(0);
		for (Iterator<View> iterator = childs.iterator(); iterator.hasNext();) {
			View object = iterator.next();
			if (field.isInstance(object)) {
				result.add(object);
			}
		}
		childs.clear();
		childs.addAll(result);

	}

	public static void getChilds(View view, List<View> childs) {
		ViewGroup viewGroup = null;
		if (view instanceof LinearLayout) {
			viewGroup = ((LinearLayout) view);
		} else if (view instanceof ScrollView) {
			viewGroup = ((ScrollView) view);
		} else if (view instanceof RelativeLayout) {
			viewGroup = ((RelativeLayout) view);
		} else if (view instanceof android.gesture.GestureOverlayView) {
			viewGroup = ((android.gesture.GestureOverlayView) view);
		} else if (view instanceof TableLayout) {
			viewGroup = ((TableLayout) view);
		} else if (view instanceof TableRow) {
			viewGroup = ((TableRow) view);
		}
		if (viewGroup != null) {
			int max = viewGroup.getChildCount();
			for (int i = 0; i < max; i++) {
				childs.add(viewGroup.getChildAt(i));
				getChilds(viewGroup.getChildAt(i), childs);
			}
		}
	}

	public static void clearEditFields(View rootLayout) {
		List<View> childs = new ArrayList<View>();
		getChilds(rootLayout, childs);

		for (View v : childs) {
			if (v instanceof EditText) {
				((EditText) v).setText(null);
			} else if (v instanceof CheckBox) {
				((CheckBox) v).setChecked(false);
			} else if (v instanceof Spinner) {
				((Spinner) v).setSelection(0);
			}
		}
	}

	/**
	 * This method gets the TextItems items from a spinner and return the texts of them all
	 * 
	 * @param s
	 *            the spinner
	 * @return the texts of all the items
	 */
	public static String[] getValuesFromSpinner(Spinner s) {
		if (s.getAdapter() == null) {
			return new String[0];
		}
		int count = s.getAdapter().getCount();
		String[] values = new String[count];
		for (int i = 0; i < count; i++) {
			values[i] = (String) s.getAdapter().getItem(i);
		}
		return values;
	}

	/**
	 * Get the spinner value as String. If the selected Item position is less than 1, returns null.
	 * 
	 * @param s
	 *            instancia do spinner
	 * @return .toString() do item selecionado
	 */
	public static String getSpinnerActualValue(Spinner s) {
		if (s == null || s.getSelectedItemPosition() < 1) {
			return null;
		}
		return s.getSelectedItem().toString();
	}

	// public static AlertDialog createYesorNoDialog(Context ctx, DialogInterface.OnClickListener
	// dialogClickListener, String msg) {
	// AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
	// builder.setMessage(msg).setPositiveButton(ctx.getString(R.string.sim),
	// dialogClickListener).setNegativeButton(ctx.getString(R.string.nao), dialogClickListener);
	// return builder.show();
	// }

	public static void changeViewListVisibility(List<View> li, boolean visibible) {
		int vis = visibible ? View.VISIBLE : View.GONE;
		for (View view : li) {
			view.setVisibility(vis);
		}
	}
}
