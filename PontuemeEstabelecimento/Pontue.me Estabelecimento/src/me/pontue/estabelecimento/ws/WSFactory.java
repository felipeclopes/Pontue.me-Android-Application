package me.pontue.estabelecimento.ws;

public class WSFactory {

	private static WSPontuemeDetails details;

	public static WSPontuemeAsyncTask getWSPontueMeInstance() {
		if (details == null) {
			details = new WSPontuemeDetails();
		}
		return new WSPontuemeAsyncTask(details);
	}

}
