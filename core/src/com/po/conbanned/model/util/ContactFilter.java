package com.po.conbanned.model.util;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.po.conbanned.model.Reference;
import com.po.conbanned.model.World;

public class ContactFilter implements com.badlogic.gdx.physics.box2d.ContactFilter {
	@Override
	public boolean shouldCollide(Fixture fixtureA, Fixture fixtureB) {
		Reference refA = (Reference) fixtureA.getUserData();
		Reference refB = (Reference) fixtureB.getUserData();
		// lammas ja koer ei kollideeru
		if (refA.getType() == Reference.Type.DOG && refB.getType() == Reference.Type.SHEEP) {
			return false;
		}
		if (refA.getType() == Reference.Type.SHEEP && refB.getType() == Reference.Type.DOG) {
			return false;
		}
		return true;
	}
}
