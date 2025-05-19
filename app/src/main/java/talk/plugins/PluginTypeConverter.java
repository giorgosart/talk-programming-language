package talk.plugins;

/**
 * Utility class for converting between Talk and Java types.
 * This helps in handling different type conversions when passing
 * arguments between Talk scripts and Java plugin methods.
 */
public class PluginTypeConverter {
    
    /**
     * Convert a Talk value to the specified Java type if possible
     * @param value The value to convert
     * @param targetType The target Java type
     * @return The converted value
     * @throws IllegalArgumentException if conversion is not possible
     */
    public static Object convertToType(Object value, Class<?> targetType) {
        if (value == null) {
            return null;
        }
        
        if (targetType.isInstance(value)) {
            return value;
        }
        
        // String conversion is common
        if (targetType == String.class) {
            return value.toString();
        }
        
        // Number conversions
        if (value instanceof Number) {
            Number num = (Number) value;
            
            if (targetType == Integer.class || targetType == int.class) {
                return num.intValue();
            } else if (targetType == Double.class || targetType == double.class) {
                return num.doubleValue();
            } else if (targetType == Float.class || targetType == float.class) {
                return num.floatValue();
            } else if (targetType == Long.class || targetType == long.class) {
                return num.longValue();
            } else if (targetType == Short.class || targetType == short.class) {
                return num.shortValue();
            } else if (targetType == Byte.class || targetType == byte.class) {
                return num.byteValue();
            }
        }
        
        // String to number conversions
        if (value instanceof String) {
            String str = (String) value;
            
            try {
                if (targetType == Integer.class || targetType == int.class) {
                    return Integer.parseInt(str);
                } else if (targetType == Double.class || targetType == double.class) {
                    return Double.parseDouble(str);
                } else if (targetType == Float.class || targetType == float.class) {
                    return Float.parseFloat(str);
                } else if (targetType == Long.class || targetType == long.class) {
                    return Long.parseLong(str);
                } else if (targetType == Short.class || targetType == short.class) {
                    return Short.parseShort(str);
                } else if (targetType == Byte.class || targetType == byte.class) {
                    return Byte.parseByte(str);
                } else if (targetType == Boolean.class || targetType == boolean.class) {
                    return Boolean.parseBoolean(str);
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Cannot convert '" + str + "' to " + targetType.getSimpleName());
            }
        }
        
        // If we get here, conversion failed
        throw new IllegalArgumentException("Cannot convert " + value.getClass().getSimpleName() + 
                                          " to " + targetType.getSimpleName());
    }
}
