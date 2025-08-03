package com.AidanC.CLI;

import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

@Configuration
@ImportRuntimeHints(NativeImageConfiguration.CliRuntimeHints.class)
public class NativeImageConfiguration {

    static class CliRuntimeHints implements RuntimeHintsRegistrar {
        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            hints.reflection()
                    .registerType(ApiCommand.class, MemberCategory.INVOKE_DECLARED_METHODS,
                            MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                            MemberCategory.DECLARED_FIELDS)
                    .registerType(ApiResponse.class, MemberCategory.INVOKE_DECLARED_METHODS,
                            MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                            MemberCategory.DECLARED_FIELDS)
                    .registerType(FilePathRequest.class, MemberCategory.INVOKE_DECLARED_METHODS,
                            MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                            MemberCategory.DECLARED_FIELDS);

            hints.resources()
                .registerPattern("application*.properties")
                .registerPattern("application*.yml");
        }
    }
}
