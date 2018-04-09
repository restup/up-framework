package com.github.restup.test.assertions;

import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.PojoField;
import com.openpojo.validation.affirm.Affirm;
import com.openpojo.validation.test.Tester;
import com.openpojo.validation.utils.ValidationHelper;

/**
 * Attempts to provide at least some basic test coverage of toString ensuring that if toString is
 * implemented, it is executed and includes at least some meaningful detain from the object.
 *
 * Pojotester may be a better alternative to openpojo for more complete toString coverage
 * https://github.com/sta-szek/pojo-tester
 */
public class ToStringTester implements Tester {

    static boolean hasToString(PojoClass pojoClass) {
        return pojoClass.getPojoMethods()
            .stream()
            .filter(m -> m.getName().equals("toString"))
            .findFirst().isPresent();
    }

    static boolean hasFields(PojoClass pojoClass) {
        return pojoClass.getPojoFields().size() > 0;
    }

    @Override
    public void run(PojoClass pojoClass) {
        if (hasFields(pojoClass) && hasToString(pojoClass)) {
            Object classInstance = ValidationHelper.getBasicInstance(pojoClass);

            String toString = classInstance.toString();

            for (PojoField fieldEntry : pojoClass.getPojoFields()) {
                Object value = fieldEntry.get(classInstance);
                if (toString.contains(fieldEntry.getName())) {
                    // check that the name and value exist in toString
                    if (value == null) {
                        if (toString.contains("null")) {
                            return;
                        }
                    } else if (toString.contains(value.toString())) {
                        return;
                    }
                } else if (value != null && value.toString().equals(toString)) {
                    // permit an object that has a field that identifies the object and equals to string
                    return;
                }
            }
            Affirm.fail(pojoClass.getName() + ".toString() must contain some object detail");
        }
    }
}
