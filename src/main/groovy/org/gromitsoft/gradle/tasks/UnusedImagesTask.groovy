/*******************************************************************************
 * 
 * MIT License
 * Copyright (c) 2015-2016 NetIQ Corporation, a Micro Focus company
 *
 ******************************************************************************/

package org.gromitsoft.gradle.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/*
 * The findUnusedImages task find unused images in .js files
 */
class UnusedImagesTask extends DefaultTask  {
    @TaskAction
    def action() {
        println ':finding unused images'

        def images = []
        def basePathLength = (project.projectDir.path + new File(project.findUnusedImages.cssPath).path).length() + 1

        project.findUnusedImages.imageFiles.each { element ->
            images.add(element.getPath().substring(basePathLength).replace('\\', '/'))
        }

        project.findUnusedImages.cssFiles.each { element ->
            if (element.exists()) {
                element.eachLine { line, count ->

                    if (line.indexOf('url(') > -1) {

                        def index = line.indexOf('url(') + 4
                        def image = line.substring(index, line.indexOf(')', index))

                        if (image.startsWith('"') || image.startsWith('\'')) {
                            image = image.substring(1);
                        }

                        if (image.endsWith('"') || image.endsWith('\'')) {
                            image = image.substring(0, image.length() - 1);
                        }

                        def iter = images.iterator()
                        while (iter.hasNext()) {
                            def cssImage = iter.next()
                            if (image.equals(cssImage)) {
                                iter.remove()
                                break
                            }
                        }
                    }
                }
            }
        }

        def out = new StringBuffer();

        images.each { element ->
            out.append(project.findUnusedImages.cssPath + '/' + element + '\n')
        }

        def reportFile = project.file('unusedImagesReport.txt')

        reportFile.write(out.toString())

        println("""

        You have ${images.size()} unused images.

        A list of all the unused images has been generated in:
        ${reportFile.path}

        """)
    }
}
