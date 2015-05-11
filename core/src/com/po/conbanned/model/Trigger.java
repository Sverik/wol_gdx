package com.po.conbanned.model;

public class Trigger extends Obstacle implements Wireable {
	public int sheepCount;
	private final String id;

	public Trigger(String id) {
		super(Type.TRIGGER);
		this.id = id;
	}

	@Override
	public boolean isActive() {
		return sheepCount > 0;
	}

	@Override
	public String getId() {
		return id;
	}
}
