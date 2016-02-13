/*******************************************************************************
 * 
 * MIT License
 * Copyright (c) 2015-2016 NetIQ Corporation, a Micro Focus company
 *
 ******************************************************************************/

package org.gromitsoft.gradle.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.GradleScriptException

/*
 * This task runs the forbidden functions task on the project.  This task 
 * ensures that we do not call functions which are forbidden in controllers.
 * 
 * https://github.com/zgrossbart/forbiddenfunctions
 */
class ForbiddenFunctionsTask extends DefaultTask {
    @TaskAction
    def action() {
        println ':checkForbiddenFunctions'

        def args = []
        args.add('-funcs')

        args.add(new File(project.projectDir, project.checkForbiddenFunctions.fileName))

        project.checkForbiddenFunctions.files.each { f ->
            args.add(f)
            //println f
        }


        def errors = com.grossbart.forbiddenfunction.FFRunner.run(args as String[])

        if (errors.size > 0) {
            errors.each { err ->
                println err
            }
            throw new GradleScriptException("There were forbidden functions.  Look above and fix the issues.", null);
        }
    }
}
