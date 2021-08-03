package com.dah.taigafx;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class Json {
    private static final JavaTimeModule javaTimeModule = new JavaTimeModule();

    private static ObjectMapper createObjectMapper() {
        return new ObjectMapper()
                .registerModule(javaTimeModule);
    }

    private static final ThreadLocal<ObjectMapper> OBJECT_MAPPER_THREAD_LOCAL
            = ThreadLocal.withInitial(Json::createObjectMapper);

    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER_THREAD_LOCAL.get();
    }
}
