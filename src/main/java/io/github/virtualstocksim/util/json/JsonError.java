package io.github.virtualstocksim.util.json;

public enum JsonError
{
    // JsonNull
    NULL,
    // The value that was returned is not what was expected
    WRONG_TYPE,
    // The member does not exist
    NONEXISTENT,
    // The Json is invalid
    INVALID
}