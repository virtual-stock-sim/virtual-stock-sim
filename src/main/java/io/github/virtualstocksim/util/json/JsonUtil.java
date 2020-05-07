package io.github.virtualstocksim.util.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import io.github.virtualstocksim.util.Errorable;
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
    public static <R> Errorable<R, JsonError> getMemberAs(JsonObject parent, String memberName, Function<? super JsonElement, R> getFunc)
    {
        try
        {
            if(parent.has(memberName))
            {
                JsonElement element = parent.get(memberName);
                if(!element.isJsonNull())
                {
                    return getAs(element, getFunc);
                }
                else
                {
                    return Errorable.WithError(JsonError.NULL);
                }
            }
            else
            {
                return Errorable.WithError(JsonError.NONEXISTENT);
            }
        }
        catch (JsonParseException e)
        {
            return Errorable.WithError(JsonError.INVALID);
        }
    }

    public static <R> Errorable<R, JsonError> getAs(JsonElement element, Function<? super JsonElement, R> getFunc)
    {
        try
        {
            if(!element.isJsonNull())
            {
                return Errorable.WithValue(getFunc.apply(element));
            }
            else
            {
                return Errorable.WithError(JsonError.NULL);
            }
        }
        catch (IllegalStateException | ClassCastException | NumberFormatException e)
        {
            return Errorable.WithError(JsonError.WRONG_TYPE);
        }
    }

}
