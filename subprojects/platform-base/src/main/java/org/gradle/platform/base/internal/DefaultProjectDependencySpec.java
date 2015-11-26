/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradle.platform.base.internal;

import com.google.common.base.Joiner;
import org.apache.commons.lang.ObjectUtils;
import org.gradle.api.Nullable;
import org.gradle.platform.base.DependencySpec;
import org.gradle.platform.base.ProjectDependencySpec;
import org.gradle.platform.base.ProjectDependencySpecBuilder;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class DefaultProjectDependencySpec implements ProjectDependencySpec {

    private final String projectPath;
    private final String libraryName;

    public DefaultProjectDependencySpec(String libraryName, String projectPath) {
        if (projectPath == null) {
            throw new IllegalArgumentException("A project dependency spec must have at least one of project or library name not null");
        }
        this.libraryName = libraryName;
        this.projectPath = projectPath;
    }

    @Override
    public String getProjectPath() {
        return projectPath;
    }

    @Nullable
    @Override
    public String getLibraryName() {
        return libraryName;
    }

    @Override
    public String getDisplayName() {
        List<String> parts = newArrayList();
        parts.add("project '" + getProjectPath() + "'");
        if (getLibraryName() != null) {
            parts.add("library '" + getLibraryName() + "'");
        }
        return Joiner.on(' ').join(parts);
    }

    public static class Builder implements ProjectDependencySpecBuilder {
        private String projectPath;
        private String libraryName;

        @Override
        public ProjectDependencySpecBuilder project(String path) {
            projectPath = path;
            return this;
        }

        @Override
        public ProjectDependencySpecBuilder library(String name) {
            libraryName = name;
            return this;
        }

        @Override
        public DependencySpec build() {
            return new DefaultProjectDependencySpec(libraryName, projectPath);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DefaultProjectDependencySpec that = (DefaultProjectDependencySpec) o;
        return ObjectUtils.equals(projectPath, that.projectPath)
            && ObjectUtils.equals(libraryName, that.libraryName);
    }

    @Override
    public int hashCode() {
        int result = ObjectUtils.hashCode(projectPath);
        result = 31 * result + ObjectUtils.hashCode(libraryName);
        return result;
    }
}
