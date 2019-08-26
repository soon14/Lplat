package com.zengshi.butterfly.core.velocity;

import org.apache.velocity.app.event.ReferenceInsertionEventHandler;

public class RenderableHandler implements ReferenceInsertionEventHandler {
	public Object referenceInsert(String reference, Object value) {
		if (value instanceof Renderable) {
			return ((Renderable) value).render();
		}

		return value;
	}
}
