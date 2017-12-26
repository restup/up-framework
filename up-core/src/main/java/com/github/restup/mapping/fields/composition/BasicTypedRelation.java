package com.github.restup.mapping.fields.composition;

import com.github.restup.annotations.field.RelationshipType;
import com.github.restup.registry.Resource;
import com.github.restup.registry.ResourceRegistry;

public class BasicTypedRelation extends BasicRelation {

	private final Class<?> resourceClass;
	private String resourceName;
	
	BasicTypedRelation(String name, RelationshipType type, String joinField, boolean includable,
			boolean validateReferences, Class<?> resourceClass) {
		super(name, type, joinField, includable, validateReferences);
		this.resourceClass = resourceClass;
	}

	@Override
	public String getResource(ResourceRegistry registry) {
		if ( resourceName == null ) {
			Resource<?,?> resource = registry.getResource(resourceClass);
			if ( resource != null ) {
				resourceName = resource.getName();
			} else {
				// at startup, when relationships are being mapped "something"
				// is required to map a missing relationship.  Use the type name
				// to identify this missing resource relationship in the interim.
				return resourceClass.getName();
			}
		}
		return resourceName;
	}

}
