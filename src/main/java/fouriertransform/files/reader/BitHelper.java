package fouriertransform.files.reader;

class BitHelper {
    public static int convertBytesToFloat(byte[] bytes) {
        switch (bytes.length) {
            case 1 -> {
                return bytes[0] - 128;
            }
            case 2 -> {
                return convertTwoBytesLittleEndianTwosComplement(bytes);
            }
            case 4 -> {
                return convertFourBytesLittleEndian(bytes);
            }
            default -> {
                return 0;
            }
        }
    }

    public static int convertFourBytesLittleEndian(byte[] bytes) {
        return (bytes[0] & 0xFF) | ((bytes[1] & 0xFF) << 8) | ((bytes[2] & 0xFF) << 16)
                | ((bytes[3] & 0xFF) << 24);
    }

    public static int convertTwoBytesLittleEndian(byte[] bytes) {
        return (bytes[0] & 0xFF) | ((bytes[1] & 0xFF) << 8);
    }

    public static int convertTwoBytesLittleEndianTwosComplement(byte[] bytes) {
        int s = (bytes[0] & 0xFF) | ((bytes[1] & 0xFF) << 8);
        if ((s >> 15) == 1)
            return -1 * ((s - 1) ^ 0xFFFF);
        return s;
    }
}
