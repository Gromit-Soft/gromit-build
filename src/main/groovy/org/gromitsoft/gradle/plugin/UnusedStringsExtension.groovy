/*******************************************************************************
 * 
 * MIT License
 * Copyright (c) 2015-2016 NetIQ Corporation, a Micro Focus company
 *
 ******************************************************************************/

package org.gromitsoft.gradle.plugin

import org.gradle.api.file.FileCollection

class UnusedStringsExtension {
    public static final String NAME = 'unusedStrings'

    FileCollection sourceFiles
    File bundleFile
    File xlfFile
}