package com.github.restup.registry;

import com.github.restup.annotations.field.RelationshipType;
import com.github.restup.mapping.fields.MappedField;
import com.github.restup.path.ResourcePath;
import com.github.restup.util.Assert;
import java.util.Objects;

import java.io.Serializable;
import java.util.*;

import static com.github.restup.annotations.field.RelationshipType.manyToOne;
import static com.github.restup.annotations.field.RelationshipType.oneToMany;

public class ResourceRelationship<FROM, FROM_ID extends Serializable, TO, TO_ID extends Serializable> {


//	private class Example {
//		// field on Foo is id (default value of "joinField")
//		@Relationship(resource=Foo.class)
//		private Long fooId;
//		// field on bar is named identity
//		@Relationship(resource=Bar.class, joinField="identity")
//		private Long barId;
//		// composite key on baz is type,id
//		@Relationship(resource=Baz.class, joinField="type")
//		private String bazType;
//		@Relationship(resource=Baz.class)
//		private Long bazId;
//		
//	    // Embedded object has same relationships!
//		private Example example;
//	}


    private final Resource<FROM, FROM_ID> from;
    private final Resource<TO, TO_ID> to;
    private final RelationshipType fromType;
    private final RelationshipType toType;

    private final List<ResourcePath> fromPaths;
    private final List<ResourcePath> toPaths;


    public ResourceRelationship(Resource<FROM, FROM_ID> from, Resource<TO, TO_ID> to
            , List<ResourcePath> fromPaths) {
        super();
        Assert.notNull(from, "from may not be null");
        Assert.notNull(to, "to may not be null");
        Assert.notEmpty(fromPaths, "fromPaths may not be empty");
        this.from = from;
        this.to = to;
        this.fromPaths = fromPaths;
        List<ResourcePath> toPaths = new ArrayList<ResourcePath>();
        RelationshipType type = RelationshipType.manyToOne;
        for (ResourcePath path : fromPaths) {
            MappedField<?> mf = path.lastMappedField();
            if (to.getType() == mf.getRelationshipResource()) {
                type = mf.getRelationshipType();
                ResourcePath toPath = ResourcePath.path(to, mf.getRelationshipJoinField());
                toPaths.add(toPath);
            }
        }
        this.toPaths = toPaths;
        this.fromType = type;
        this.toType = converse(type, fromPaths);
    }

    public static String getRelationshipNameForToResource(ResourceRelationship relationship) {
        Resource to = relationship.getTo();
        Resource from = relationship.getFrom();
        if (relationship.isToOneRelationship(to)) {
            return from.getName();
        }
        return from.getPluralName();
    }

    /**
     * @param resource
     * @return A list of *all* relationships defined from this resource, including nested objects
     */
    public static List<ResourcePath> getAllRelationshipPaths(Resource<?, ?> resource) {
        List<ResourcePath> result = new ArrayList<ResourcePath>();
        for (ResourcePath path : resource.getAllPaths()) {
            MappedField<?> mf = path.lastMappedField();
            if (mf != null && mf.getRelationshipResource() != null) {
                if (mf.getRelationshipResource() != null) {
                    result.add(path);
                }
            }
        }
        return result;
    }

    private RelationshipType converse(RelationshipType type, List<ResourcePath> fromPaths) {
        switch (type) {
            case manyToOne:
                return oneToMany;
            case oneToOne:
                // if there is more than one relationship then it may be to more than one
                // resource, thus even if it is oneToOne individually, overall it is oneToMany
                return fromPaths.size() == 1 ? type : oneToMany;
            case manyToMany:
                return type;
            case oneToMany:
                return manyToOne;
        }
        return null;
    }

    public RelationshipType getType(Resource<?, ?> resource) {
        if (resource != null) {
            if (resource.equals(from)) {
                return fromType;
            } else if (resource.equals(to)) {
                return toType;
            }
        }
        return null;
    }

    private Set<Object> collect(Object instance, List<ResourcePath> list) {
        Set<Object> result = new HashSet<Object>();
        for (ResourcePath path : list) {
            path.collectValues(result, instance);
        }
        return result;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public Set<FROM_ID> getFromIds(Object from) {
        return (Set) collect(from, fromPaths);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public Set<TO_ID> getToIds(Object to) {
        return (Set) collect(to, toPaths);
    }

    @SuppressWarnings("rawtypes")
    public Set getJoinIds(Resource<?, ?> resource, Object data) {
        if (isFrom(resource)) {
            return getFromIds(data);
        } else {
            return getToIds(data);
        }
    }

    public Set<FROM_ID> getIds(FROM from) {
        return getFromIds(from);
    }

    public Set<FROM_ID> getIds(Collection<FROM> from) {
        return getFromIds(from);
    }

    public Set<TO_ID> getIdsTo(TO to) {
        return getToIds(to);
    }

    public Set<TO_ID> getIdsTo(Collection<TO> to) {
        return getToIds(to);
    }

    public Resource<FROM, FROM_ID> getFrom() {
        return from;
    }

    public Resource<TO, TO_ID> getTo() {
        return to;
    }

    public List<ResourcePath> getFromPaths() {
        return fromPaths;
    }

    public List<ResourcePath> getToPaths() {
        return toPaths;
    }

    public boolean isFrom(Resource<?, ?> resource) {
        return Objects.equals(resource, from);
    }

    public boolean isTo(Resource<?, ?> resource) {
        return Objects.equals(resource, to);
    }

    public boolean isToOneRelationship(Resource<?, ?> resource) {
        RelationshipType type = getType(resource);
        return RelationshipType.isToOne(type);
    }

}
