package io.github.virtualstocksim.util;

import java.sql.Timestamp;
import java.time.Instant;

public class Util
{

    public static Timestamp GetTimeStamp()
    {
        return Timestamp.from(Instant.now());
    }

}
