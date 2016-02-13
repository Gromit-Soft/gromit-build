/*******************************************************************************
 * 
 * MIT License
 * Copyright (c) 2015-2016 NetIQ Corporation, a Micro Focus company
 *
 ******************************************************************************/

package org.gromitsoft.gradle.plugin

import org.gradle.api.file.FileTree

class ForbiddenFunctionsExtension {
    public static final String NAME = 'checkForbiddenFunctions'

    FileTree files
    String fileName = 'src/build/forbidden_controller_functions.txt'
}