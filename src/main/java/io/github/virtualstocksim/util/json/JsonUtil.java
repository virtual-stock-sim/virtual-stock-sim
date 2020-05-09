package io.github.virtualstocksim.util.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import io.github.virtualstocksim.util.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.function.Function;

public class JsonUtil
{
    private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);

    /**
     * Attempts to
     * @param parent Parent element to retrieve member from
     * @param memberName Name of member that was attempting to be retrieved
     * @param getFunc Anonymous function to retrieve element from parent element
     * @return Result of getFunc
     */
    public static <R> Result<R, JsonError> getMemberAs(JsonObject parent, String memberName, Function<? super JsonElement, R> getFunc)
    {
        logger.trace("Extracting " + memberName + " from " + parent);
        try
        {
            if(parent.has(memberName))
            {
                return getAs(parent.get(memberName), getFunc);
            }
            else
            {
                return Result.WithError(JsonError.NONEXISTENT);
            }
        }
        catch (JsonParseException e)
        {
            return Result.WithError(JsonError.INVALID);
        }
    }

    public static <R> Result<R, JsonError> getAs(JsonElement element, Function<? super JsonElement, R> getFunc)
    {
        logger.trace("Converting " + element);
        try
        {
            if(element.isJsonNull())
            {
                return Result.WithError(JsonError.NULL);
            }
            else
            {
                return Result.WithValue(getFunc.apply(element));
            }
        }
        catch (IllegalStateException | ClassCastException | NumberFormatException e)
        {
            return Result.WithError(JsonError.WRONG_TYPE);
        }
    }

}
