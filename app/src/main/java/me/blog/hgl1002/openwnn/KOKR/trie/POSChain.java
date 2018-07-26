package me.blog.hgl1002.openwnn.KOKR.trie;

import static me.blog.hgl1002.openwnn.KOKR.trie.MiniPOS.*;

public enum POSChain {

	SUBSTANTIVE_JOSA(SUBSTANTIVE, JOSA),
	PREDICATE_PREEOMI_EOMI(PREDICATE, PRE_EOMI, EOMI),
	PREDICATE_EOMI(PREDICATE, EOMI),

	MODIFIER_SPACE(MODIFIER, SPACE),
	INDEPENDENT_SPACE(INDEPENDENT, SPACE);

	private final MiniPOS[] posList;

	POSChain(MiniPOS... posLIst) {
		this.posList = posLIst;
	}

	public MiniPOS[] getPosList() {
		return posList;
	}
}
