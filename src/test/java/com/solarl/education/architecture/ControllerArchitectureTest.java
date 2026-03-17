package com.solarl.education.architecture;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaField;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(packages = "com.solarl.education", importOptions = ImportOption.DoNotIncludeTests.class)
class ControllerArchitectureTest {

    @ArchTest
    static final ArchRule controllers_should_have_only_service_dependencies =
            classes()
                    .that()
                    .resideInAPackage("..controller..")
                    .and()
                    .haveSimpleNameEndingWith("Controller")
                    .should(haveOnlyServiceLayerFields());

    private static ArchCondition<JavaClass> haveOnlyServiceLayerFields() {
        return new ArchCondition<>("have non-static fields only from package ..service..") {
            @Override
            public void check(JavaClass controllerClass, ConditionEvents events) {
                for (JavaField field : controllerClass.getFields()) {
                    if (field.getOwner().equals(controllerClass)
                            && !field.getModifiers().contains(JavaModifier.STATIC)) {
                        boolean validFieldType = field.getRawType().getPackageName()
                                .startsWith("com.solarl.education.service");
                        String message = String.format(
                                "Field %s in %s has type %s",
                                field.getName(),
                                controllerClass.getName(),
                                field.getRawType().getName()
                        );
                        events.add(new SimpleConditionEvent(field, validFieldType, message));
                    }
                }
            }
        };
    }
}
