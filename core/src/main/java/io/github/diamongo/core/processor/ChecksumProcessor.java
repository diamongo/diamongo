/*
 * Copyright © 2017 The Diamongo authors. All rights reserved.
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
 */
package io.github.diamongo.core.processor;

import io.github.diamongo.core.migration.MigrationMarker;
import io.github.diamongo.core.util.ChecksumUtils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import static io.github.diamongo.core.util.ChecksumUtils.normalize;
import static io.github.diamongo.core.util.ChecksumUtils.sha256;

/**
 * Annotation processor that generates the class {@code io.github.diamongo.core.migration.MigrationWrappers} which will
 * contain {@link io.github.diamongo.core.migration.MigrationWrapper} instances for all classes annotated with
 * {@link MigrationMarker}.
 */
@SupportedAnnotationTypes("io.github.diamongo.core.migration.MigrationMarker")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public final class ChecksumProcessor extends AbstractProcessor {

    private static final String MIGRATION_WRAPPERS_FQCN = "io.github.diamongo.core.migration.JavaMigrations";

    /**
     * Processes all classes annotated with {@link MigrationMarker} and creates SHA-256 hashes from the corresponding
     * Java source files. Files are {@link ChecksumUtils#normalize(CharSequence) normalized} before the hash is taken.
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.isEmpty() || roundEnv.processingOver()) {
            return true;
        }

        System.out.println();
        System.out.println("********************************************************************************");
        System.out.println("*                    Running Diamongo ChecksumProcessor...                     *");
        System.out.println("********************************************************************************");
        System.out.println();

        Filer filer = processingEnv.getFiler();

        String migrationWrappersSource = annotations.stream()
                .flatMap(elem -> roundEnv.getElementsAnnotatedWith(elem).stream())
                .map(elem -> {
                    try {
                        TypeElement classElem = (TypeElement) elem;
                        String className = classElem.getQualifiedName().toString();

                        System.out.printf("Processing file: %s%n", className);

                        PackageElement packageElem = (PackageElement) classElem.getEnclosingElement();
                        String packageName = packageElem.getQualifiedName().toString();

                        String fileName = elem.getSimpleName().toString() + ".java";
                        FileObject source = filer.getResource(StandardLocation.SOURCE_PATH, packageName, fileName);

                        CharSequence charContent = source.getCharContent(false);
                        String normalizedCharContent = normalize(charContent);
                        String sha256 = sha256(normalizedCharContent);

                        return new MigrationData(className, sha256);
                    } catch (IOException ex) {
                        // log and re-throw, the compiler won't print the stacktrace
                        ex.printStackTrace();
                        throw new UncheckedIOException(ex);
                    }
                })
                .reduce(new JavaMigrationsCreator(), JavaMigrationsCreator::addMigrationData, (c1, c2) -> null)
                .create();

        try {
            System.out.printf("Writing class: %s%n", MIGRATION_WRAPPERS_FQCN);
            FileObject migrationWrappersClassObject = filer.createSourceFile(MIGRATION_WRAPPERS_FQCN);
            try (Writer writer = migrationWrappersClassObject.openWriter()) {
                writer.append(migrationWrappersSource);
            }
        } catch (IOException ex) {
            System.err.println("Cannot create source file: " + MIGRATION_WRAPPERS_FQCN);
            ex.printStackTrace();
        }

        System.out.println();
        System.out.println("********************************************************************************");
        System.out.println("*                 Finished running Diamongo ChecksumProcessor                  *");
        System.out.println("********************************************************************************");
        System.out.println();

        return true;
    }
}

