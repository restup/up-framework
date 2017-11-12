package com.github.restup.controller.linking;

import com.github.restup.controller.model.HttpMethod;
import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.annotations.field.RelationshipType;
import com.github.restup.controller.linking.discovery.ServiceDiscovery;
import com.github.restup.controller.request.parser.params.PageLimitParser;
import com.github.restup.controller.request.parser.params.PageOffsetParser;
import com.github.restup.registry.Resource;
import com.github.restup.service.model.ResourceData;
import com.github.restup.service.model.response.PagedResult;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Default implementation of {@link LinkBuilder}
 */
public class DefaultLinkBuilder implements LinkBuilder {
    private final static Logger log = LoggerFactory.getLogger(DefaultLinkBuilder.class);
    private final ServiceDiscovery serviceDiscovery;

    public DefaultLinkBuilder(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    public List<Link> getLinks(ParsedResourceControllerRequest<?> request, Object result, Resource<?, ?> resource, Object id) {
        if (HttpMethod.DELETE == request.getMethod()) {
            return Collections.emptyList();
        }
        if (request.getRelationship() != null && !isList(result)) {
            Object rel = getId(request);
            return Arrays.asList(link(request, LinkRelations.self, request.getRelationship(), rel, resource),
                    link(request, LinkRelations.related, request.getRelationship(), rel));
        }
        return Arrays.asList(link(request, LinkRelations.self, resource, id));
    }

    public List<Link> getRelationshipLinks(ParsedResourceControllerRequest<?> request, Object Result, Resource<?, ?> relationship, Object id) {
        List<Link> links = new ArrayList<Link>();

        links.add(link(request, LinkRelations.related, request.getResource(), id, relationship));

//        links.add(link(LinkRelations.self, request.getResource(), id, JsonApiResult.RELATIONSHIPS, relationship));
        return links;
    }

    public List<Link> getRelationshipLinks(ParsedResourceControllerRequest<?> request, Object result, Resource<?, ?> relationship, Object id, RelationshipType type) {
        List<Link> links = new ArrayList<Link>();

        String name = RelationshipType.isToOne(type) ? relationship.getName() : relationship.getPluralName();

        links.add(link(request, LinkRelations.related, request.getResource(), id, name));

        return links;
    }

    public List<Link> getTopLevelLinks(ParsedResourceControllerRequest request, Object result) {
        List<Link> links = new ArrayList<Link>();
        String baseUrl = buildResourceRequestBaseUrl(request);
        if (result instanceof PagedResult) {
            PagedResult paged = (PagedResult) result;
            Integer offset = paged.getOffset();
            Integer limit = paged.getLimit();
            if (offset != null && limit != null) {
                if (offset > 0) {
                    paging(baseUrl, request, links, LinkRelations.first, 0, limit);
                    paging(baseUrl, request, links, LinkRelations.prev, offset - 1, limit);
                }
                paging(baseUrl, request, links, LinkRelations.self, offset, limit);
                if (paged.getTotal() != null) {
                    int max = ((int) (paged.getTotal() / (long) limit)) - 1;
                    if (offset < max) {
                        paging(baseUrl, request, links, LinkRelations.next, offset + 1, limit);
                        paging(baseUrl, request, links, LinkRelations.last, max, limit);
                    }
                } else {
                    paging(baseUrl, request, links, LinkRelations.next, offset + 1, limit);
                }
            } else {
                links.add(new BasicLink(LinkRelations.self, baseUrl));
            }
        } else if (isList(result)) {
            links.add(new BasicLink(LinkRelations.self, baseUrl));
        } else {
            return links;
        }
        if (request.getRelationship() != null) {
            links.add(link(request, LinkRelations.related, request.getRelationship(), getId(request)));
        }
        return links;
    }

    private boolean isList(Object result) {
        if (result instanceof ResourceData) {
            if (((ResourceData) result).getData() instanceof Iterable) {
                return true;
            }
        }
        return false;
    }

    private void paging(String baseUrl, ParsedResourceControllerRequest request, List<Link> links, LinkRelations rel, int offset, int limit) {
        StringBuilder sb = new StringBuilder();
        sb.append(baseUrl);
        char delimiter = baseUrl.contains("?") ? '&' : '?';
        param(sb, delimiter, getPageLimitParameterName(request), limit);
        param(sb, getPageOffsetParameterName(request), offset);
        links.add(new BasicLink(rel, sb.toString()));
    }

    /**
     * @return A url representing the request including all accepted parameters, less paging params
     */
    public String buildResourceRequestBaseUrl(ParsedResourceControllerRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append(request.getRequestUrl());
        char delimiter = '?';

        List<String> params = request.getAcceptedParameterNames();
        if (params != null) {
            for (String param : params) {
                if (includeParam(request, param)) {
                    String[] values = request.getParameter(param);
                    for (String value : values) {
                        param(sb, delimiter, param, value);
                        delimiter = '&';
                    }
                }
            }
        }
        return sb.toString();
    }


    private void param(StringBuilder sb, String param, Integer value) {
        param(sb, '&', param, value);
    }

    private void param(StringBuilder sb, char delimiter, String param, Integer value) {
        if (value != null) {
            param(sb, delimiter, param, String.valueOf(value));
        }
    }

    private void param(StringBuilder sb, String param, String value) {
        param(sb, '&', param, value);
    }

    private void param(StringBuilder sb, char delimiter, String param, String value) {
        sb.append(delimiter);
        sb.append(param);
        sb.append("=");
        try {
            sb.append(URLEncoder.encode(value, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            log.warn("Unable to encode value, using unencoded value in link: " + value, e);
            sb.append(value);
        }
    }

    private boolean includeParam(ParsedResourceControllerRequest request, String param) {
        return !Objects.equals(param, getPageLimitParameterName(request))
                && !Objects.equals(param, getPageOffsetParameterName(request));
    }

    public String getPageLimitParameterName(ParsedResourceControllerRequest request) {
        String result = request.getPageLimitParameterName();
        if (result == null) {
            //TODO default to a configured name
            result = PageLimitParser.LIMIT;
        }
        return result;
    }

    public String getPageOffsetParameterName(ParsedResourceControllerRequest request) {
        String result = request.getPageOffsetParameterName();
        if (result == null) {
            //TODO default to a configured name
            result = PageOffsetParser.OFFSET;
        }
        return result;
    }

    private Link link(ParsedResourceControllerRequest request, LinkRelations rel, Resource<?, ?> resource, Object... path) {
        return new BasicLink(rel, getUrl(request, resource, path));
    }

    private String getUrl(ParsedResourceControllerRequest request, Resource<?, ?> resource, Object... path) {
        String baseUrl = serviceDiscovery.locateResourceUrl(request, resource);
        StringBuilder sb = new StringBuilder();
        sb.append(baseUrl);
        for (Object s : path) {
            sb.append("/").append(s);
        }
        return sb.toString();
    }

    private Object getId(ParsedResourceControllerRequest<?> request) {
        return request.getIds().get(0);
    }

}
