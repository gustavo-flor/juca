package com.github.gustavoflor.juca.architecture

import com.github.gustavoflor.juca.Application
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import com.tngtech.archunit.library.Architectures.layeredArchitecture


@AnalyzeClasses(
    packagesOf = [Application::class],
    importOptions = [ImportOption.DoNotIncludeTests::class]
)
internal class ArchitectureTest {
    companion object {
        private val BASE_PACKAGE = Application::class.java.`package`.name
        private val CORE_LAYER_PACKAGE = "$BASE_PACKAGE.core"
        private val ENTRYPOINT_LAYER_PACKAGE = "$BASE_PACKAGE.entrypoint"
        private val DATA_LAYER_PACKAGE = "$BASE_PACKAGE.data"
        private val CONFIG_LAYER_PACKAGE = "$BASE_PACKAGE.config"
        private const val CORE_LAYER = "Core"
        private const val DATA_LAYER = "Data"
        private const val CONFIG_LAYER = "Config"
        private const val ENTRYPOINT_LAYER = "Entrypoint"
    }

    @ArchTest
    val layerDependenciesAreRespected: ArchRule = layeredArchitecture()
        .consideringAllDependencies()
        .layer(CONFIG_LAYER).definedBy("$CONFIG_LAYER_PACKAGE..")
        .layer(ENTRYPOINT_LAYER).definedBy("$ENTRYPOINT_LAYER_PACKAGE..")
        .layer(CORE_LAYER).definedBy("$CORE_LAYER_PACKAGE..")
        .layer(DATA_LAYER).definedBy("$DATA_LAYER_PACKAGE..")
        .whereLayer(CONFIG_LAYER).mayNotBeAccessedByAnyLayer()
        .whereLayer(ENTRYPOINT_LAYER).mayNotBeAccessedByAnyLayer()
        .whereLayer(DATA_LAYER).mayOnlyBeAccessedByLayers(CORE_LAYER)

    @ArchTest
    val noCoreClassesShouldAccessDataClasses: ArchRule = noClasses()
        .that()
        .resideInAPackage("$CORE_LAYER_PACKAGE..")
        .should()
        .accessClassesThat()
        .resideInAnyPackage("$DATA_LAYER_PACKAGE..")
}
