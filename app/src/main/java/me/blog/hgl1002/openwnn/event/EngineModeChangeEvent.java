package me.blog.hgl1002.openwnn.event;

import me.blog.hgl1002.openwnn.KOKR.EngineMode;

public class EngineModeChangeEvent extends OpenWnnEvent {

	EngineMode engineMode;

	public EngineModeChangeEvent(EngineMode engineMode) {
		this.engineMode = engineMode;
	}

	public EngineMode getEngineMode() {
		return engineMode;
	}

}
