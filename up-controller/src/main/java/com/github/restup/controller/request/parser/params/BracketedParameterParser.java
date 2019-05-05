package com.github.restup.controller.request.parser.params;

import static com.github.restup.controller.request.parser.params.ParameterResourceParser.ParameterResourceParsers.Parsed;

import com.github.restup.registry.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BracketedParameterParser implements ParameterParser {

    private final Pattern pattern;
    private final int resourceIndex;
    private final ResourceParser resourceParser;

    {
        pattern = Pattern.compile("\\[(.*?)\\]");
    }

    public BracketedParameterParser(ResourceParser resourceParser) {
        this(resourceParser, 0);
    }

    public BracketedParameterParser() {
        this(0);
    }

    public BracketedParameterParser(int resourceIndex) {
        this.resourceIndex = resourceIndex;
        resourceParser = this::parseResourceAtIndex;
    }

    public BracketedParameterParser(ResourceParser resourceParser, int resourceIndex) {
        this.resourceIndex = resourceIndex;
        this.resourceParser = resourceParser;
    }

    /**
     * parses resource or returns null
     */
    public static Resource parseResourceBeforeBrackets(ParameterParsingContext ctx, String value,
        String... tokens) {
        String resourceName = value;
        if (tokens.length > 0) {
            resourceName = value.substring(0, value.indexOf("["));
        }
        return ctx.getResource(resourceName);
    }

    @Override
    public ParameterParserResult parse(ParameterParsingContext ctx,
        String value) {
        Matcher matcher = pattern.matcher(value);
        List<String> matches = new ArrayList();
        while (matcher.find()) {
            matches.add(matcher.group(1));
        }
        String[] tokens = matches.toArray(new String[0]);
        return ParameterParserResult.of(resourceParser.parse(ctx, value, tokens), tokens);
    }

    /**
     * parses resource adding an error if not found
     */
    Resource parseResourceAtIndex(ParameterParsingContext ctx, String value,
        String... tokens) {
        return Parsed.parseResourceAt(ctx, resourceIndex, tokens);
    }

    @FunctionalInterface
    interface ResourceParser {

        Resource parse(ParameterParsingContext ctx, String value, String... tokens);
    }
}
