package com.po.conbanned.model.util;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.po.conbanned.model.Obstacle;
import com.po.conbanned.model.Reference;
import com.po.conbanned.model.Sheep;
import com.po.conbanned.model.Trigger;
import com.po.conbanned.model.World;

public class ContactListener implements com.badlogic.gdx.physics.box2d.ContactListener {
	private interface FlockAction {
		public void action(Sheep sheepA, Sheep sheepB, boolean touching);
	}

	private enum ContactType {
		BEGIN,
		END,
		;
	}

	private FlockAction[] flockActions = new FlockAction[ContactType.values().length];
	{
		flockActions[ContactType.BEGIN.ordinal()] = new FlockAction() {
			@Override
			public void action(Sheep sheepA, Sheep sheepB, boolean isTouching) {
				if (isTouching) {
					sheepA.getFlock().add(sheepB);
					sheepB.getFlock().add(sheepA);
				}
			}
		};

		flockActions[ContactType.END.ordinal()] = new FlockAction() {
			@Override
			public void action(Sheep sheepA, Sheep sheepB, boolean isTouching) {
				if (!isTouching) {
					sheepA.getFlock().remove(sheepB);
					sheepB.getFlock().remove(sheepA);
				}
			}
		};
	}

	private void testAndDoTriggerAction(Reference refA, Reference refB, Contact contact, ContactType contactType) {
		Trigger trigger;
		boolean withSheep = false;
		if (refA.getType() == Reference.Type.WIRE) {
			trigger = (Trigger) refA.getObstacle();
			withSheep = (refB.getType() == Reference.Type.SHEEP);
		} else if (refB.getType() == Reference.Type.WIRE) {
			trigger = (Trigger) refB.getObstacle();
			withSheep = (refA.getType() == Reference.Type.SHEEP);
		} else {
			return;
		}

		if (withSheep) {
			if (contactType == ContactType.BEGIN && contact.isTouching()) {
				trigger.sheepCount++;
			} else if (contactType == ContactType.END && !contact.isTouching()) {
				trigger.sheepCount--;
			}
		}
	}

	private void event(Contact contact, ContactType contactType) {
		Reference refA = (Reference) contact.getFixtureA().getUserData();
		Reference refB = (Reference) contact.getFixtureB().getUserData();
		if (flockingEvent(refA, refB)) {
			flockActions[contactType.ordinal()].action(refA.getSheep(), refB.getSheep(), contact.isTouching());
		} else {
			testAndDoTriggerAction(refA, refB, contact, contactType);
		}
	}

	private boolean flockingEvent(Reference refA, Reference refB) {
		if (refA.getType() == Reference.Type.SHEEP && refB.getType() == Reference.Type.FLOCK) {
			return true;
		}
		if (refB.getType() == Reference.Type.SHEEP && refA.getType() == Reference.Type.FLOCK) {
			return true;
		}
		return false;
	}

	@Override
	public void beginContact(Contact contact) {
		event(contact, ContactType.BEGIN);
	}

	@Override
	public void endContact(Contact contact) {
		event(contact, ContactType.END);
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
	}
}
