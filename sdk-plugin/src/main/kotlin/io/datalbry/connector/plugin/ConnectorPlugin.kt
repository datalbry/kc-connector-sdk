package io.datalbry.connector.plugin

import io.datalbry.connector.plugin.setup.setupDependencies
import io.datalbry.connector.plugin.setup.setupLanguage
import io.datalbry.connector.plugin.setup.setupSpringBoot
import io.datalbry.connector.plugin.setup.setupTasks
import org.gradle.api.Plugin
import org.gradle.api.Project

class ConnectorPlugin: Plugin<Project> {

    override fun apply(project: Project) {
        val extension = setupExtensions(project)
        project.setupLanguage(extension)
        project.setupDependencies(extension)
        project.setupSpringBoot(extension)
        project.setupTasks(extension)
    }

    private fun setupExtensions(project: Project): ConnectorPluginExtension {
        val extension = project.extensions.create(EXTENSION_NAME, ConnectorPluginExtension::class.java, project)
        return extension
    }

    companion object {
        const val EXTENSION_NAME = "connector"
    }
}