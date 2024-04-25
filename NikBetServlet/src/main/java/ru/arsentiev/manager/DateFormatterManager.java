package ru.arsentiev.manager;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import ru.arsentiev.processing.dateformatter.LocalDateFormatter;
import ru.arsentiev.processing.dateformatter.TimeStampFormatter;

@UtilityClass
public class DateFormatterManager {
    @Getter
    private static final LocalDateFormatter localDateFormatter;
    @Getter
    private static final TimeStampFormatter timeStampFormatter;

    static {
        localDateFormatter = new LocalDateFormatter();
        timeStampFormatter = new TimeStampFormatter();
    }
}
