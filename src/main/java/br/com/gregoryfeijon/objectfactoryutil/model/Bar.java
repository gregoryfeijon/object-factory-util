package br.com.gregoryfeijon.objectfactoryutil.model;

import br.com.gregoryfeijon.objectfactoryutil.annotation.ObjectConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@ObjectConstructor
public class Bar implements Serializable {

	private static final long serialVersionUID = 5879335447426884784L;

	private long barId;
	private String barName;

	public Bar(long barId, String barName) {
		this.barId = barId;
		this.barName = barName;
	}
}
