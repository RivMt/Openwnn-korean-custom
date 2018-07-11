package me.blog.hgl1002.openwnn.event;

public class SoftKeyFlickEvent extends OpenWnnEvent {

	private int keyCode;
	private Direction direction;

	public SoftKeyFlickEvent(int keyCode, Direction direction) {
		this.keyCode = keyCode;
		this.direction = direction;
	}

	public int getKeyCode() {
		return keyCode;
	}

	public Direction getDirection() {
		return direction;
	}

	public enum Direction {

		UP(-100), DOWN(-200), LEFT(-300), RIGHT(-400);

		private int twelveKeyOffset;

		Direction(int twelveKeyOffset) {
			this.twelveKeyOffset = twelveKeyOffset;
		}

		public int getTwelveKeyOffset() {
			return twelveKeyOffset;
		}

	}

}
