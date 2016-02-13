/*******************************************************************************
 * 
 * MIT License
 * Copyright (c) 2015-2016 NetIQ Corporation, a Micro Focus company
 *
 ******************************************************************************/

package org.gromitsoft.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy

import org.gromitsoft.gradle.tasks.BuildInfoTask
import org.gromitsoft.gradle.tasks.CopyrightTask
import org.gromitsoft.gradle.tasks.ForbiddenFunctionsTask
import org.gromitsoft.gradle.tasks.UnusedImagesTask
import org.gromitsoft.gradle.tasks.FindUnusedStringsTask
import org.gromitsoft.gradle.tasks.RemoveUnusedStringsTask
import org.gromitsoft.gradle.tasks.GenerateXLFTask
import org.gromitsoft.gradle.tasks.GeneratePropertiesFromXLFTask
import org.gromitsoft.gradle.tasks.CreateLegendFilesTask

class GromitPlugin implements Plugin<Project>  {
    @Override
    void apply(Project project) {
        // create extensions property
        project.extensions.create(GromitPluginExtension.NAME, GromitPluginExtension)
        project.extensions.create(BuildInfoExtension.NAME, BuildInfoExtension)
        project.extensions.create(CopyrightExtension.NAME, CopyrightExtension)
        project.extensions.create(ForbiddenFunctionsExtension.NAME, ForbiddenFunctionsExtension)
        project.extensions.create(UnusedImagesExtension.NAME, UnusedImagesExtension)
        project.extensions.create(UnusedStringsExtension.NAME, UnusedStringsExtension)
        project.extensions.create(XLFExtension.NAME, XLFExtension)
        project.extensions.create(CreateLegendFilesExtension.NAME, CreateLegendFilesExtension)

        // create tasks
        project.task('generateBuildInfo', dependsOn: 'classes', type: BuildInfoTask,
                description: 'This task generates the build-info.json file we use for build information at runtime') {}
        project.task('checkCopyright', dependsOn: 'classes', type: CopyrightTask,
                description: 'Check the copyright header for all JavaScript source files') {}
        project.task('checkForbiddenFunctions', dependsOn: 'classes', type: ForbiddenFunctionsTask,
                description: 'This task checks JavaScript files for forbidden functions') {}
        project.task('findUnusedImages', dependsOn: 'classes', type: UnusedImagesTask,
                description: 'Finds all images which are not referenced from CSS files.') {}
        project.task('findUnusedStrings', dependsOn: 'classes', type: FindUnusedStringsTask,
                description: 'Finds all localized strings which are not referenced from HTML and JavaScript files.') {}
        project.task('removeUnusedStrings', dependsOn: 'classes', type: RemoveUnusedStringsTask,
                description: 'Removes all localized strings which are not referenced from HTML and JavaScript files.') {}
        project.task('generateXLF', dependsOn: 'classes', type: GenerateXLFTask,
                description: 'Generate the JavaScript localization service files into the src/main/app/js/services/i18n/ directory.') {}
        project.task('generatePropertiesFromXLF', dependsOn: 'classes', type: GeneratePropertiesFromXLFTask,
                description: 'Generate the properties files for field localization into the build/l10n directory.') {}
        project.task('createLegendFiles', dependsOn: 'classes', type: CreateLegendFilesTask,
                description: 'Create legend.css and legend.html template files') {}

        // utility tasks
        project.task('copyXLF', type: Copy, {
            from project.xlf.xlfFile
            into project.xlf.i18nPath
        })
    }
}