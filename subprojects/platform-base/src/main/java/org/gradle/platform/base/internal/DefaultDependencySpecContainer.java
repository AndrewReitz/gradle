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

import com.google.common.collect.ImmutableSet;
import org.gradle.platform.base.*;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static java.util.Collections.emptySet;

public class DefaultDependencySpecContainer implements DependencySpecContainer {

    private final List<DependencySpecBuilder> builders = new LinkedList<DependencySpecBuilder>();
    private final String defaultProject;

    public DefaultDependencySpecContainer(String defaultProject) {
        if (defaultProject == null) {
            throw new IllegalArgumentException("defaultProject");
        }
        this.defaultProject = defaultProject;
    }

    @Override
    public ProjectDependencySpecBuilder project(String path) {
        return projectDependency().project(path);
    }

    @Override
    public ProjectDependencySpecBuilder library(String name) {
        ensureLibraryNotModule(name);
        return projectDependency().library(name).project(defaultProject);
    }

    @Override
    public ModuleDependencySpecBuilder module(String moduleIdOrName) {
        return moduleIdOrName.contains(":")
            ? moduleDependencyFromModuleId(moduleIdOrName)
            : moduleDependency().module(moduleIdOrName);
    }

    @Override
    public ModuleDependencySpecBuilder group(String name) {
        return moduleDependency().group(name);
    }

    @Override
    public Collection<DependencySpec> getDependencies() {
        if (isEmpty()) {
            return emptySet();
        }
        return dependencySpecSet();
    }

    @Override
    public boolean isEmpty() {
        return builders.isEmpty();
    }

    private DefaultProjectDependencySpec.Builder projectDependency() {
        return add(new DefaultProjectDependencySpec.Builder());
    }

    private DefaultModuleDependencySpec.Builder moduleDependency() {
        return add(new DefaultModuleDependencySpec.Builder());
    }

    private <T extends DependencySpecBuilder> T add(T builder) {
        builders.add(builder);
        return builder;
    }

    private void ensureLibraryNotModule(String name) {
        if (name != null && name.contains(":")) {
            throw new IllegalArgumentException(
                String.format("`%s' is not a valid library name. Did you mean to refer to a module instead?", name));
        }
    }

    private ModuleDependencySpecBuilder moduleDependencyFromModuleId(String moduleId) {
        //TODO:RBO: validate module id syntax here
        String[] components = moduleId.split(":");
        return moduleDependency()
            .group(components[0])
            .module(components[1])
            .version(components.length < 3 ? null : components[2]);
    }

    private Set<DependencySpec> dependencySpecSet() {
        ImmutableSet.Builder<DependencySpec> specs = ImmutableSet.builder();
        for (DependencySpecBuilder specBuilder : builders) {
            specs.add(specBuilder.build());
        }
        return specs.build();
    }
}
