package byteback.specification.plugin;

import byteback.specification.plugin.Plugin.Attach;
import byteback.specification.plugin.Plugin.Export;
import byteback.specification.Contract.Abstract;

@Attach("java.util.List")
public abstract class ListSpec<T> {

	@Export
	public boolean isImmutable;

	@Export
	@Abstract
	public ListSpec() {
	}

}
