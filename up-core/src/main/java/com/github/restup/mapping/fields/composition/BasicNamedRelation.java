package com.github.restup.mapping.fields.composition;

import com.github.restup.annotations.field.RelationshipType;
import com.github.restup.registry.ResourceRegistry;

class BasicNamedRelation extends AbstractBasicRelation {

	private final String resourceName;
	
	BasicNamedRelation(String name, RelationshipType type, String joinField, boolean includable,
			boolean validateReferences, String resourceName) {
		super(name, type, joinField, includable, validateReferences);
		this.resourceName = resourceName;
	}

	@Override
	public String getResource(ResourceRegistry registry) {
		return resourceName;
	}
	
	public String getResourceName() {
        return resourceName;
    }

}
