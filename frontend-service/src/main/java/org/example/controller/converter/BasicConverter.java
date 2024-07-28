package org.example.controller.converter;

import lombok.extern.slf4j.Slf4j;

import java.util.regex.Pattern;

@Slf4j
public class BasicConverter {

    protected Long readIdFromStringByPattern(Pattern pattern, String str) {
        log.info("Parsing id from string '{}' by pattern: {}", str, pattern);
        try {
            var matcher = pattern.matcher(str);
            if (matcher.find()
                    && matcher.groupCount() == 1) {
                String id = matcher.group(1);
                log.info("Parsed '{}' from string '{}' by pattern: {}", id, str, pattern);
                return stringToLong(id);
            }
        } catch (Exception ignored) {}

        return null;
    }

    protected Long stringToLong(String str) {
        try {
            return Long.parseLong(str);
        } catch (Exception ignored) {}

        return null;
    }
}
