
package io.rivmt.keyboard.openwnn.event;

public abstract class OpenWnnEvent {

	private boolean cancelled;

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

}

