package com.github.restup.registry;

import com.github.restup.annotations.field.RelationshipType;
import com.github.restup.mapping.fields.MappedField;
import com.github.restup.path.ResourcePath;
import com.github.restup.util.Assert;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

class BasicResourceRelationship<FROM, FROM_ID extends Serializable, TO, TO_ID extends Serializable>
        implements ResourceRelationship<FROM, FROM_ID, TO, TO_ID> {

    // private class Example {
    // // field on Foo is id (default value of "joinField")
    // @Relationship(resource=Foo.class)
    // private Long fooId;
    // // field on bar is named identity
    // @Relationship(resource=Bar.class, joinField="identity")
    // private Long barId;
    // // composite key on baz is type,id
    // @Relationship(resource=Baz.class, joinField="type")
    // private String bazType;
    // @Relationship(resource=Baz.class)
    // private Long bazId;
    //
    // // Embedded object has same relationships!
    // private Example example;
    // }

    private final Resource<FROM, FROM_ID> from;
    private final Resource<TO, TO_ID> to;
    private final RelationshipType fromType;
    private final RelationshipType toType;

    private final List<ResourcePath> fromPaths;
    private final List<ResourcePath> toPaths;

    BasicResourceRelationship(Resource<FROM, FROM_ID> from, Resource<TO, TO_ID> to
            , List<ResourcePath> fromPaths) {
        super();
        Assert.notNull(from, "from may not be null");
        Assert.notNull(to, "to may not be null");
        Assert.notEmpty(fromPaths, "fromPaths may not be empty");
        this.from = from;
        this.to = to;
        this.fromPaths = fromPaths;
        List<ResourcePath> toPaths = new ArrayList<>();
        RelationshipType type = RelationshipType.manyToOne;
        for (ResourcePath path : fromPaths) {
        	
            MappedField<?> mf = path.lastMappedField();
            if (mf != null && Objects.equals(to.getName(), mf.getRelationshipResource(from.getRegistry()))) {
                type = mf.getRelationshipType();
                ResourcePath toPath = ResourcePath.path(to, mf.getRelationshipJoinField());
                toPaths.add(toPath);
            }
        }
        this.toPaths = toPaths;
        fromType = type;
        toType = ResourceRelationship.converse(type, fromPaths);
    }

    public static String getRelationshipNameForToResource(BasicResourceRelationship<?, ?, ?, ?> relationship) {
        Resource<?,?> to = relationship.getTo();
        Resource<?,?> from = relationship.getFrom();
        if (relationship.isToOneRelationship(to)) {
            return from.getName();
        }
        return from.getPluralName();
    }

    /**
     * @return A list of *all* relationships defined from this resource, including nested objects
     */
    public static List<ResourcePath> getAllRelationshipPaths(Resource<?, ?> resource) {
        return resource.getAllPaths().stream()
        		.filter(path -> path.lastMappedField().isRelationship())
        		.collect(Collectors.toList());
    }

    private static Set<Object> collect(Object instance, List<ResourcePath> list) {
        // TODO Collector
        Set<Object> result = new HashSet<>();
        for (ResourcePath path : list) {
            path.collectValues(result, instance);
        }
        return result;
    }

    @Override
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

    @Override
    public Set<FROM_ID> getFromIds(Object from) {
        return (Set) collect(from, fromPaths);
    }

    @Override
    public Set<TO_ID> getToIds(Object to) {
        return (Set) collect(to, toPaths);
    }

    @Override
    public Set getJoinIds(Resource<?, ?> resource, Object data) {
        if (isFrom(resource)) {
            return getFromIds(data);
        } else {
            return getToIds(data);
        }
    }

    @Override
    public Set<FROM_ID> getIds(FROM from) {
        return getFromIds(from);
    }

    @Override
    public Set<FROM_ID> getIds(Collection<FROM> from) {
        return getFromIds(from);
    }

    @Override
    public Set<TO_ID> getIdsTo(TO to) {
        return getToIds(to);
    }

    @Override
    public Set<TO_ID> getIdsTo(Collection<TO> to) {
        return getToIds(to);
    }

    @Override
    public Resource<FROM, FROM_ID> getFrom() {
        return from;
    }

    @Override
    public Resource<TO, TO_ID> getTo() {
        return to;
    }

    @Override
    public List<ResourcePath> getFromPaths() {
        return fromPaths;
    }

    @Override
    public List<ResourcePath> getToPaths() {
        return toPaths;
    }

    @Override
    public boolean isToOneRelationship(Resource<?, ?> resource) {
        RelationshipType type = getType(resource);
        return RelationshipType.isToOne(type);
    }

    public RelationshipType getFromType() {
        return fromType;
    }

    public RelationshipType getToType() {
        return toType;
    }

}
