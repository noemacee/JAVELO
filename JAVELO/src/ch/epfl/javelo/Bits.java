package ch.epfl.javelo;

public final class Bits {
    private Bits(){}

    public int extractSigned(int value, int start, int length){
        Preconditions.checkArgument(start+length >=0 && start+length <=31);
        String valueToString = String.valueOf(value);
        String substringValue = valueToString.substring(start-1, start+length-1);
        for (int i = 0; i < 32 - start - length; i++) {
            substringValue = '0' + substringValue;
        }
        return Integer.parseInt(substringValue);
    }

    public int extractUnsigned(int value, int start, int length){
        Preconditions.checkArgument(start+length >=0 && start+length <=31 && length != 32);
        String valueToString = String.valueOf(value);
        String substringValue = valueToString.substring(start-1, start+length-1);
        for (int i = 0; i < 32 - start - length; i++) {
            substringValue = '1' + substringValue;
        }
        return Integer.parseInt(substringValue);
    }
}

