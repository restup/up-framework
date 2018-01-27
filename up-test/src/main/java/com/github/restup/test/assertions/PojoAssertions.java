package com.github.restup.test.assertions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.PojoClassFilter;
import com.openpojo.reflection.filters.FilterClassName;
import com.openpojo.reflection.filters.FilterPackageInfo;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.affirm.Affirm;
import com.openpojo.validation.rule.Rule;
import com.openpojo.validation.rule.impl.EqualsAndHashCodeMatchRule;
import com.openpojo.validation.rule.impl.GetterMustExistRule;
import com.openpojo.validation.rule.impl.NoFieldShadowingRule;
import com.openpojo.validation.rule.impl.NoPublicFieldsExceptStaticFinalRule;
import com.openpojo.validation.rule.impl.NoStaticExceptFinalRule;
import com.openpojo.validation.rule.impl.SetterMustExistRule;
import com.openpojo.validation.test.Tester;
import com.openpojo.validation.test.impl.DefaultValuesNullTester;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;

/**
 * Assertions using openpojo with sensible defaults and utility methods 
 * for simplified, fluent pojo assertions
 * 
 * @author abuttaro
 *
 */
public class PojoAssertions {

    private List<PojoClass> classes;
    private List<Rule> rules;
    private List<Tester> testers;
    private boolean includeDefaultTesters;
    private boolean includeDefaultRules;
    private int expectedClasses;

    PojoAssertions() {
        classes = new ArrayList<>();
        rules = new ArrayList<>();
        testers = new ArrayList<>();
        includeDefaultTesters = true;
        includeDefaultRules = true;
    }

    private PojoAssertions me() {
        return this;
    }

    public static List<Rule> defaultRules() {
        // Add Rules to validate structure for POJO_PACKAGE
        // See com.openpojo.validation.rule.impl for more ...
        return Arrays.asList(new EqualsAndHashCodeMatchRule(), new NoFieldShadowingRule(), new NoPublicFieldsExceptStaticFinalRule(), new NoStaticExceptFinalRule(),
                new GetterMustExistRule(), new SetterMustExistRule()
//                , new SerializableMustHaveSerialVersionUIDRule()
                );
    }

    public static List<Tester> defaultTesters() {
        // Add Testers to validate behaviour for POJO_PACKAGE
        // See com.openpojo.validation.test.impl for more ...
        return Arrays.asList(
                new SetterTester(), new GetterTester(), new DefaultValuesNullTester());
    }

    public PojoAssertions with(Rule... rules) {
        this.rules.addAll(Arrays.asList(rules));
        return me();
    }

    public PojoAssertions with(Tester... testers) {
        this.testers.addAll(Arrays.asList(testers));
        return me();
    }

    public PojoAssertions add(Class<?>... classes) {
        for (Class<?> clazz : classes) {
            this.classes.add(PojoClassFactory.getPojoClass(clazz));
            expectedClasses++;
        }
        return me();
    }

    public PojoAssertions addMatching(int expected, String packageName, String regex) {
        return add(expected, new FilterClassName("^" + packageName + "\\." + regex), packageName);
    }

    public PojoAssertions addMatching(int expected, Class<?> relativeTo, String regex) {
        String packageName = relativeTo.getPackage().getName();
        return addMatching(expected, packageName, regex);
    }

    public PojoAssertions addMatchingRecursively(int expected, String packageName, String regex) {
        return addRecursively(expected, new FilterClassName(regex), packageName);
    }

    public PojoAssertions add(int expected, String... packages) {
        return add(expected, new FilterPackageInfo(), packages);
    }

    public PojoAssertions add(int expected, PojoClassFilter pojoClassFilter, String... packages) {
        expectedClasses += expected;
        for (String pkg : packages) {
            this.classes.addAll(PojoClassFactory.getPojoClasses(pkg, pojoClassFilter));
        }
        return me();
    }

    public PojoAssertions addRecursively(int expected, String... packages) {
        return addRecursively(expected, new FilterPackageInfo(), packages);
    }

    public PojoAssertions addRecursively(int expected, PojoClassFilter pojoClassFilter, String... packages) {
        expectedClasses += expected;
        for (String pkg : packages) {
            this.classes.addAll(PojoClassFactory.getPojoClassesRecursively(pkg, pojoClassFilter));
        }
        return me();
    }
    
    public PojoAssertions includeDefaultRules(boolean b) {
        this.includeDefaultRules = b;
        return me();
    }
    
    public PojoAssertions includeDefaultTesters(boolean b) {
        this.includeDefaultTesters = b;
        return me();
    }

    public void validate() {

        ValidatorBuilder builder = ValidatorBuilder.create();

        addRules(builder);

        addTesters(builder);

        Affirm.affirmEquals("Classes added / removed?", expectedClasses, classes.size());

        Validator validator = builder.build();
        validator.validate(classes);
    }

    private void addTesters(ValidatorBuilder validator) {
        List<Tester> validatorTesters = new ArrayList<>(testers);
        if (includeDefaultTesters) {
            validatorTesters.addAll(defaultTesters());
        }

        validatorTesters.forEach(t -> validator.with(t));
    }

    private void addRules(ValidatorBuilder validator) {
        List<Rule> validatorRules = new ArrayList<>(rules);
        if (includeDefaultRules) {
            validatorRules.addAll(defaultRules());
        }
        validatorRules.forEach(r -> validator.with(r));
    }

}
