package com.github.restup.registry;

import static com.github.restup.annotations.field.RelationshipType.manyToOne;
import static com.github.restup.annotations.field.RelationshipType.oneToMany;

import com.github.restup.annotations.field.RelationshipType;
import com.github.restup.path.ResourcePath;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public interface ResourceRelationship<FROM, FROM_ID extends Serializable, TO, TO_ID extends Serializable> {

    static <FROM, FROM_ID extends Serializable, TO, TO_ID extends Serializable> ResourceRelationship<FROM, FROM_ID, TO, TO_ID> of(Resource<FROM, FROM_ID> from,
            Resource<TO, TO_ID> to, List<ResourcePath> fromPaths) {
        return new BasicResourceRelationship<>(from, to, fromPaths);
    }

    static String getRelationshipNameForToResource(ResourceRelationship<?, ?, ?, ?> relationship) {
        Resource<?, ?> to = relationship.getTo();
        Resource<?, ?> from = relationship.getFrom();
        if (relationship.isToOneRelationship(to)) {
            return from.getName();
        }
        return from.getPluralName();
    }

    /**
     * @param resource to get paths from
     * @return A list of *all* relationships defined from this resource, including nested objects
     */
    static List<ResourcePath> getAllRelationshipPaths(Resource<?, ?> resource) {
        return resource.getAllPaths().stream()
                .filter(path -> path.lastMappedField().isRelationship())
                .collect(Collectors.toList());
    }

    static RelationshipType converse(RelationshipType type, List<ResourcePath> fromPaths) {
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

    RelationshipType getType(Resource<?, ?> resource);

    Set<FROM_ID> getFromIds(Object from);

    Set<TO_ID> getToIds(Object to);

    Set getJoinIds(Resource<?, ?> resource, Object data);

    Set<FROM_ID> getIds(FROM from);

    Set<FROM_ID> getIds(Collection<FROM> from);

    Set<TO_ID> getIdsTo(TO to);

    Set<TO_ID> getIdsTo(Collection<TO> to);

    Resource<FROM, FROM_ID> getFrom();

    Resource<TO, TO_ID> getTo();

    List<ResourcePath> getFromPaths();

    List<ResourcePath> getToPaths();

    default boolean isFrom(Resource<?, ?> resource) {
        return Objects.equals(resource, getFrom());
    }

    default boolean isTo(Resource<?, ?> resource) {
        return Objects.equals(resource, getTo());
    }

    boolean isToOneRelationship(Resource<?, ?> resource);

}
