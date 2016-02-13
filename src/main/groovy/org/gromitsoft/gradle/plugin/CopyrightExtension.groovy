/*******************************************************************************
 * 
 * MIT License
 * Copyright (c) 2015-2016 NetIQ Corporation, a Micro Focus company
 *
 ******************************************************************************/

package org.gromitsoft.gradle.plugin

import org.gradle.api.file.FileCollection

class CopyrightExtension {
    public static final String NAME = 'checkCopyright'

    FileCollection files
    String fileName = 'src/build/copyright.txt'
}