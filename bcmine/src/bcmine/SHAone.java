package bcmine;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.nio.ByteBuffer;

public class SHAone {

    int A, B, C, D, E;

    int F;
    int j, temp;

    String Encript(byte[] dataIn) {
        byte[] paddedData = padTheMessage(dataIn);
        int[] H = {0x67452301, 0xEFCDAB89, 0x98BADCFE, 0x10325476, 0xC3D2E1F0};
        int[] K = {0x5A827999, 0x6ED9EBA1, 0x8F1BBCDC, 0xCA62C1D6};

        if (paddedData.length % 64 != 0) {
            System.out.println("Invalid padded data length.");
            System.exit(0);
        }

        int passesReq = paddedData.length / 64;
        byte[] work = new byte[64];

        for (int passCntr = 0; passCntr < passesReq; passCntr++) {
            System.arraycopy(paddedData, 64 * passCntr, work, 0, 64);
            processTheBlock(work, H, K);
        }

        return intArrayToHexStr(H);
    }
    //-------------------------------------------//

    private byte[] padTheMessage(byte[] data) {
        int origLength = data.length;
        int tailLength = origLength % 64;
        int padLength = 0;
        if ((64 - tailLength >= 9)) {
            padLength = 64 - tailLength;
        } else {
            padLength = 128 - tailLength;
        }

        byte[] thePad = new byte[padLength];
        thePad[0] = (byte) 0x80;
        long lengthInBits = origLength * 8;

        for (int cnt = 0; cnt < 8; cnt++) {
            thePad[thePad.length - 1 - cnt] = (byte) ((lengthInBits >> (8 * cnt)) & 0x00000000000000FF);
        }

        byte[] output = new byte[origLength + padLength];

        System.arraycopy(data, 0, output, 0, origLength);
        System.arraycopy(thePad, 0, output, origLength, thePad.length);

        return output;

    }
    //-------------------------------------------//

    private void processTheBlock(byte[] work, int H[], int K[]) {

        int[] W = new int[80];
        for (int outer = 0; outer < 16; outer++) {
            int temp = 0;
            for (int inner = 0; inner < 4; inner++) {
                temp = (work[outer * 4 + inner] & 0x000000FF) << (24 - inner * 8);
                W[outer] = W[outer] | temp;
            }
        }

        for (int j = 16; j < 80; j++) {
            W[j] = rotateLeft(W[j - 3] ^ W[j - 8] ^ W[j - 14] ^ W[j - 16], 1);
        }

        A = H[0];
        B = H[1];
        C = H[2];
        D = H[3];
        E = H[4];

        for (int j = 0; j < 20; j++) {
            F = (B & C) | ((~B) & D);
            //	K = 0x5A827999;
            temp = rotateLeft(A, 5) + F + E + K[0] + W[j];
//            System.out.println(Integer.toHexString(K[0]));
            E = D;
            D = C;
            C = rotateLeft(B, 30);
            B = A;
            A = temp;
        }

        for (int j = 20; j < 40; j++) {
            F = B ^ C ^ D;
            //   K = 0x6ED9EBA1;
            temp = rotateLeft(A, 5) + F + E + K[1] + W[j];
//            System.out.println(Integer.toHexString(K[1]));
            E = D;
            D = C;
            C = rotateLeft(B, 30);
            B = A;
            A = temp;
        }

        for (int j = 40; j < 60; j++) {
            F = (B & C) | (B & D) | (C & D);
            //   K = 0x8F1BBCDC;
            temp = rotateLeft(A, 5) + F + E + K[2] + W[j];
            E = D;
            D = C;
            C = rotateLeft(B, 30);
            B = A;
            A = temp;
        }

        for (int j = 60; j < 80; j++) {
            F = B ^ C ^ D;
            //   K = 0xCA62C1D6;
            temp = rotateLeft(A, 5) + F + E + K[3] + W[j];
            E = D;
            D = C;
            C = rotateLeft(B, 30);
            B = A;
            A = temp;
        }

        H[0] += A;
        H[1] += B;
        H[2] += C;
        H[3] += D;
        H[4] += E;

        int n;
//        for (n = 0; n < 16; n++) {
//            System.out.println("W[" + n + "] = " + toHexString(W[n]));
//        }

//        System.out.println("H0:" + Integer.toHexString(H[0]));
//        System.out.println("H0:" + Integer.toHexString(H[1]));
//        System.out.println("H0:" + Integer.toHexString(H[2]));
//        System.out.println("H0:" + Integer.toHexString(H[3]));
//        System.out.println("H0:" + Integer.toHexString(H[4]));
    }

    private String intArrayToHexStr(int[] data) {
        String output = "";
        String tempStr = "";
        int tempInt = 0;
        for (int cnt = 0; cnt < data.length; cnt++) {

            tempInt = data[cnt];

            tempStr = Integer.toHexString(tempInt);

            if (tempStr.length() == 1) {
                tempStr = "0000000" + tempStr;
            } else if (tempStr.length() == 2) {
                tempStr = "000000" + tempStr;
            } else if (tempStr.length() == 3) {
                tempStr = "00000" + tempStr;
            } else if (tempStr.length() == 4) {
                tempStr = "0000" + tempStr;
            } else if (tempStr.length() == 5) {
                tempStr = "000" + tempStr;
            } else if (tempStr.length() == 6) {
                tempStr = "00" + tempStr;
            } else if (tempStr.length() == 7) {
                tempStr = "0" + tempStr;
            }
            output = output + tempStr;
        }//end for loop
        return output;
    }//end intArrayToHexStr

    final int rotateLeft(int value, int bits) {
        int q = (value << bits) | (value >>> (32 - bits));
        return q;
    }

    static final String toHexString(final ByteBuffer bb) {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bb.limit(); i += 4) {
            if (i % 4 == 0) {
                sb.append('\n');
            }
            sb.append(toHexString(bb.getInt(i))).append(' ');
        }
        sb.append('\n');
        return sb.toString();
    }

    static final String toHexString(int x) {
        return padStr(Integer.toHexString(x));
    }
    static final String ZEROS = "00000000";
    static final String padStr(String s) {
        if (s.length() > 8) {
            return s.substring(s.length() - 8);
        }
        return ZEROS.substring(s.length()) + s;
    }
}
