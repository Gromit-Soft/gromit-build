/*******************************************************************************
 *
 * Copyright 2015 Gromit Soft Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package org.gromitsoft.gradle.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/*
 * CreateLegendFiles task creates legend.css and legend.html template files
 */
class CreateLegendFilesTask extends DefaultTask  {
    @TaskAction
    def action() {
        println ':create legend files'

        def images = []
        def basePathLength = (project.projectDir.path + new File(project.createLegendFiles.cssPath).path).length() + 1

        project.createLegendFiles.imageFiles.each { element ->
            images.add(element.getPath().substring(basePathLength).replace('\\', '/'))
        }

        // legend.css file
        def legendCss = new StringBuffer();
        def legendCssFile = project.file(project.projectDir.path + '/' + project.createLegendFiles.cssPath + '/legend.css')

        //legend.html
        def legendHtml = new StringBuffer();
        def legendHtmlFile = project.file(project.projectDir.path + '/' + project.createLegendFiles.viewPath + '/legend.html')
        legendHtml.append('<h2>Legend</h2>\n')
        legendHtml.append('<table><tbody>\n')

        // css template line   images_focus/icon_save.png
        def cssLine = '.{imageClass} {\n' +
                '    background: transparent url("{imagePath}") no-repeat scroll 0 0 /16px auto;\n' +
                '    height: 22px;\n' +
                '    display: inline-block;\n' +
                '    padding-left: 25px;\n' +
                '}'

        // html template line
        def htmlLine = '<tr><td class="{imageClass}">{imageText}</td></tr>'

        project.createLegendFiles.cssFiles.each { element ->
            if (element.exists()) {
                element.eachLine { line, count ->

                    if (line.indexOf('url(') > -1) {

                        // get image path
                        def index = line.indexOf('url(') + 4
                        def image = line.substring(index, line.indexOf(')', index))
                        if (image.startsWith('"') || image.startsWith('\'')) {
                            image = image.substring(1);
                        }
                        if (image.endsWith('"') || image.endsWith('\'')) {
                            image = image.substring(0, image.length() - 1);
                        }

                        // by pass it is is not image file
                        if (!image.startsWith('data:')) {

                            // get image file name
                            def imageName = image.substring(image.indexOf('/') + 1, image.lastIndexOf('.'))

                            def iter = images.iterator()
                            while (iter.hasNext()) {
                                def cssImage = iter.next()
                                if (image.equals(cssImage)) {

                                    def cssl = cssLine.replace('{imageClass}', 'l_' + imageName).replace('{imagePath}', cssImage)
                                    legendCss.append(cssl + '\n')

                                    def htmll = htmlLine.replace('{imageClass}', 'l_' + imageName).replace('{imageText}', cssImage)
                                    legendHtml.append(htmll + '\n')
                                    iter.remove()
                                    break
                                }
                            }
                        }
                    }
                }
            }
        }

        // write to files
        legendCssFile.write(legendCss.toString())
        legendHtml.append('</tbody></table>')
        legendHtmlFile.write(legendHtml.toString())

        println("""
        A list of all the used images has been generated in:
        ${legendCssFile.path}  and  ${legendHtmlFile.path}
        """)
    }
}
