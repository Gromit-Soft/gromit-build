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
        println ':createLegendFiles'
        def jsonSlurper = new groovy.json.JsonSlurper();
        def reader = new BufferedReader(new FileReader(project.createLegendFiles.legendFile))
        def parsedData = jsonSlurper.parse(reader)
        def imagesList = new ArrayList<LegendImage>()
        parsedData.legendImages.each {
            lImage ->
                def image = new LegendImage(lImage.path, lImage.description, lImage.separator)
                imagesList.add(image)
        }
        //imagesList.each {lImage -> println lImage}

        // legend.css file
        def legendCss = new StringBuffer();
        def legendCssFile = project.file(project.projectDir.path + '/' + project.createLegendFiles.cssPath + '/legend.css')

        //legend.html
        def legendHtml = new StringBuffer();
        def legendHtmlFile = project.file(project.projectDir.path + '/' + project.createLegendFiles.viewPath + '/legend.html')
        legendHtml.append('<div class="modal-header">\n' +
                '    <a class="close" href="#" title="{{i18n.CLOSE}}" ng-click="close()">x</a>\n' +
                '    <h3 class="modal-title">{{i18n.legend}}</h3>\n' +
                '</div>\n')
        legendHtml.append('<div class="modal-body">\n')
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

        imagesList.each { image ->
            if (image.separator) {
                legendHtml.append('<tr><td>&nbsp;</td></tr>\n')
            } else {
                // get image file name
                def imageName = image.path.substring(image.path.indexOf('/') + 1, image.path.lastIndexOf('.'))
                def cssl = cssLine.replace('{imageClass}', 'l_' + imageName).replace('{imagePath}', image.path)
                legendCss.append(cssl + '\n')

                def htmll = htmlLine.replace('{imageClass}', 'l_' + imageName).replace('{imageText}', image.description)
                legendHtml.append(htmll + '\n')
            }
        }

        // write to files
        legendCssFile.write(legendCss.toString())
        legendHtml.append('</tbody></table>\n')
        legendHtml.append('</div>')
        legendHtmlFile.write(legendHtml.toString())
//        println("""
//        A list of all the used images has been generated in:
//        ${legendCssFile.path}  and  ${legendHtmlFile.path}
//        """)
    }
}

class LegendImage {
    def path
    def description
    def separator

    LegendImage (path, description, separator) {
        this.separator = separator;
        this.path = path
        this.description = description
    }

    def String toString() {
        return "Path: " + this.path + " Description: " + this.description + " Separator: " + this.separator
    }
}