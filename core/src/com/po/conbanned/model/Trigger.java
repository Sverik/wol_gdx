package com.po.conbanned.model;

public class Trigger extends Obstacle implements Wireable {
	public int sheepCount;
	private Wireable output;
	public final String outputId;

	public Trigger(String trackId, String defId, String outputId) {
		super(trackId, defId, Type.TRIGGER);
		this.outputId = outputId;
	}

	@Override
	public void setActive(boolean active) {
	}

	@Override
	public boolean isActive() {
		return sheepCount > 0;
	}

	public void setOutput(Wireable output) {
		this.output = output;
	}

	@Override
	public Wireable getOutput() {
		return output;
	}
}
