/*
 *
 * Copyright © 2017 The Diamongo authors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.github.diamongo.core.processor;

import io.github.diamongo.core.migration.DbChangeSet;
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
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import static io.github.diamongo.core.util.ChecksumUtils.normalize;
import static io.github.diamongo.core.util.ChecksumUtils.sha256;

/**
 * Annotation processor that createsSHA-256 hashes from db change set classes.
 */
@SupportedAnnotationTypes("io.github.diamongo.core.migration.DbChangeSet")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class Sha256Processor extends AbstractProcessor {

    /**
     * Processes all classes annotated with {@link DbChangeSet} and creates SHA-256 hashes from the corresponding Java
     * source files. Files are {@link ChecksumUtils#normalize(CharSequence) normalized}
     * before the hash is taken.
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Filer filer = processingEnv.getFiler();
        annotations.stream()
                .flatMap(e -> roundEnv.getElementsAnnotatedWith(e).stream())
                .map(element -> {
                    try {
                        String packageName = element.getEnclosingElement().toString();
                        String simpleName = element.getSimpleName().toString();
                        String fileName = simpleName + ".java";

                        FileObject source = filer.getResource(StandardLocation.SOURCE_PATH, packageName, fileName);

                        CharSequence charContent = source.getCharContent(true);
                        CharSequence normalizedCharContent = normalize(charContent);
                        String sha256 = sha256(normalizedCharContent);

                        return new JavaSource(charContent, packageName, simpleName, sha256);
                    } catch (IOException ex) {
                        throw new UncheckedIOException(ex);
                    }
                })
                .forEach(javaSource -> {
                    try {
                        String fqcn = javaSource.getFullyQualifiedName() + "_sha256";
                        FileObject sha256HolderClass = filer.createSourceFile(fqcn);
                        System.out.printf("Writing Java file: %s%n", fqcn);

                        try (Writer writer = sha256HolderClass.openWriter()) {
                            writer.append(javaSource.createSha256HolderClassSource());
                        }
                    } catch (IOException ex) {
                        throw new UncheckedIOException(ex);
                    }
                });

        return true;
    }

    private static class JavaSource {
        private static final String LINE_SEP = System.getProperty("line.separator");

        CharSequence charContent;
        CharSequence packageName;
        CharSequence className;
        CharSequence sha256Hash;

        JavaSource(CharSequence charContent, CharSequence packageName, CharSequence className,
                CharSequence sha256Hash) {
            this.charContent = charContent;
            this.packageName = packageName;
            this.className = className;
            this.sha256Hash = sha256Hash;
        }

        String getFullyQualifiedName() {
            return String.format("%s.%s", packageName, className);
        }

        String createSha256HolderClassSource() {
            StringBuilder sb = new StringBuilder(300);
            sb.append("package ").append(packageName).append(';').append(LINE_SEP);
            sb.append("/* Class generated. Do not alter manually! */").append(LINE_SEP);
            sb.append("public class ").append(className).append("_sha256 {").append(LINE_SEP);
            sb.append("    public static final String SHA256 = \"").append(sha256Hash).append("\";").append(LINE_SEP);
            sb.append("}").append(LINE_SEP);
            return sb.toString();
        }
    }
}

