package io.rivmt.keyboard.openwnn.event;

import io.rivmt.keyboard.openwnn.KOKR.EngineMode;

public class EngineModeChangeEvent extends OpenWnnEvent {

	EngineMode engineMode;

	public EngineModeChangeEvent(EngineMode engineMode) {
		this.engineMode = engineMode;
	}

	public EngineMode getEngineMode() {
		return engineMode;
	}

}
