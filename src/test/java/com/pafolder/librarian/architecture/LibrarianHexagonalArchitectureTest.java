package com.pafolder.librarian.architecture;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.Architectures.onionArchitecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@AnalyzeClasses(packages = "com.pafolder.librarian")
public class LibrarianHexagonalArchitectureTest {

  JavaClasses importedClasses;

  @BeforeEach
  void init() {
    importedClasses = new ClassFileImporter()
        .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
        .importPackages("com.pafolder.librarian");
  }

  @Test
  public void layeredArchitectureTest() {
    layeredArchitecture()
        .consideringOnlyDependenciesInLayers()
        .layer("domain").definedBy("..domain..")
        .layer("application").definedBy("..application..")
        .layer("infrastructure").definedBy("..infrastructure..")
        .whereLayer("domain").mayOnlyBeAccessedByLayers("application", "infrastructure")
        .whereLayer("domain").mayNotAccessAnyLayer()
        .whereLayer("application").mayOnlyAccessLayers("domain")
        .whereLayer("infrastructure").mayOnlyAccessLayers("domain", "application")
        .check(importedClasses);
  }

  @Test
  public void onionArchitectureTest() {
    onionArchitecture()
        .ensureAllClassesAreContainedInArchitectureIgnoring("com.pafolder.librarian..")
        .domainModels("..domain..")
        .domainServices("..domain.service..", "..domain.repository..")
        .applicationServices("..application..")
        .adapter("infrastructure", "..infrastructure..")
        .check(importedClasses);
  }

  @Test
  public void domain_should_not_use_application() {
    ArchRule rule = ArchRuleDefinition.noClasses()
        .that().resideInAPackage("..domain..")
        .should().accessClassesThat().resideInAPackage("..application..");

    rule.check(importedClasses);
  }

  @Test
  public void domain_should_not_use_hibernate() {
    ArchRule rule = ArchRuleDefinition.noClasses()
        .that().resideInAPackage("..domain..")
        .should().accessClassesThat().resideInAPackage("..hibernate..");

    rule.check(importedClasses);
  }

  @Test
  public void domain_should_not_use_infrastructure() {
    ArchRule rule = ArchRuleDefinition.noClasses()
        .that().resideInAPackage("..domain..")
        .should().accessClassesThat().resideInAPackage("..infrastructure..");

    rule.check(importedClasses);
  }

  @Test
  public void application_should_not_use_infrastructure() {
    ArchRule rule = ArchRuleDefinition.noClasses()
        .that().resideInAPackage("..application..")
        .should().accessClassesThat().resideInAPackage("..infrastructure..");

    rule.check(importedClasses);
  }

  @Test
  public void infrastructure_should_not_use_application() {
    ArchRule rule = ArchRuleDefinition.noClasses()
        .that().resideInAPackage("..infrastructure..")
        .should().accessClassesThat().resideInAPackage("..application..");

    rule.check(importedClasses);
  }


}
