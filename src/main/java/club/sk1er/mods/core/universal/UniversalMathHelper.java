package club.sk1er.mods.core.universal;

public class UniversalMathHelper {

    public static float clamp_float(float one, float two, float three) {
        if (one < two) return two;
        return Math.min(one, three);
    }

    public static float sqrt_double(double value) {
        return (float) Math.sqrt(value);
    }

    public static double wrapAngleTo180_double(double value) {
        value = value % 360.0D;

        if (value >= 180.0D) {
            value -= 360.0D;
        }

        if (value < -180.0D) {
            value += 360.0D;
        }

        return value;
    }

    public static int clamp_int(int num, int min, int max) {
        return num < min ? min : (Math.min(num, max));
    }
}
