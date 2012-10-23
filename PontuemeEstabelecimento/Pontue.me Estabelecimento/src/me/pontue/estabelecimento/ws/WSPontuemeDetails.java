package me.pontue.estabelecimento.ws;

import java.io.Serializable;
import java.util.ArrayList;

import me.pontue.estabelecimento.ws.entity.Beneficio;

public class WSPontuemeDetails implements Serializable {

	private static final long serialVersionUID = -1396312688022027555L;
	private String token;
	private ArrayList<Beneficio> beneficios;
	private long points;
	private String email, password;
	private Long benefitSelected;
	private String benefitSelectName;

	public String getBenefitSelectName() {
		return benefitSelectName;
	}

	public void setBenefitSelect(String benefitSelect) {
		this.benefitSelectName = benefitSelect;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public void addBeneficio(Beneficio ben) {
		if (beneficios == null) {
			beneficios = new ArrayList<Beneficio>();
		}
		beneficios.add(ben);
	}

	public ArrayList<Beneficio> getBeneficios() {
		return beneficios;
	}

	public void setBeneficios(ArrayList<Beneficio> beneficios) {
		this.beneficios = beneficios;
	}

	public long getPoints() {
		return points;
	}

	public void setPoints(long points) {
		this.points = points;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Long getBenefitSelected() {
		return benefitSelected;
	}

	public void setBenefitSelected(Long benefitSelected) {
		this.benefitSelected = benefitSelected;
	}

}
