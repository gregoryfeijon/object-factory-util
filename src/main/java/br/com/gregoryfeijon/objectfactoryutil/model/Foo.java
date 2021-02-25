package br.com.gregoryfeijon.objectfactoryutil.model;

import br.com.gregoryfeijon.objectfactoryutil.annotation.ObjectConstructor;
import br.com.gregoryfeijon.objectfactoryutil.exception.ObjectFactoryUtilException;
import br.com.gregoryfeijon.objectfactoryutil.util.ObjectFactoryUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@ObjectConstructor(exclude = { "fooId" })
public class Foo implements Serializable {

	private static final long serialVersionUID = 4390538007183032023L;

	private long fooId;
	private String fooName;
	private Bar bar;
	private List<Bar> bars;

	public Foo(Foo foo) throws ObjectFactoryUtilException {
		ObjectFactoryUtil.createFromObject(foo, this);
	}
	
	public Foo(long fooId, String fooName, Bar bar, List<Bar> bars) {
		this.fooId = fooId;
		this.fooName = fooName;
		this.bar = bar;
		this.bars = bars;
	}
}
