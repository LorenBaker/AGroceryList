package com.lbconsulting.agrocerylist.classes;

/**
 * Miscellaneous common utilities
 */
public class clsUtils {

    public static String formatGTIN(String gtin) {
        String result = gtin;
        gtin = gtin.trim();
        if (gtin.isEmpty()) {
            return "";
        }
        switch (gtin.length()) {
            case 6:
                String part1 = gtin.substring(0, 3);
                String part2 = gtin.substring(3, 6);
                result = part1 + " " + part2;
                break;

            case 8:
                part1 = gtin.substring(0, 4);
                part2 = gtin.substring(4, 8);
                result = part1 + " " + part2;
                break;

            case 12:
                part1 = gtin.substring(0, 1);
                part2 = gtin.substring(1, 6);
                String part3 = gtin.substring(6, 11);
                String part4 = gtin.substring(11);
                result = part1 + " " + part2 + " " + part3 + " " + part4;
                break;

            case 13:
                part1 = gtin.substring(0, 1);
                part2 = gtin.substring(1, 7);
                part3 = gtin.substring(7, 12);
                result = part1 + " " + part2 + " " + part3;
                break;

            case 14:
                part1 = gtin.substring(0, 1);
                part2 = gtin.substring(1, 3);
                part3 = gtin.substring(3, 8);
                part4 = gtin.substring(8, 13);
                String part5 = gtin.substring(13);
                result = part1 + " " + part2 + " " + part3 + " " + part4 + " " + part5;
                break;
        }

        return result;
    }


    public static String UpcE2A(String upc_E) {
        // source: http://www.taltech.com/files/UPC.vb

        // check that the upc_E string only contains numbers
        if (!upc_E.matches("[0-9]+")) {
            // invalid upc_E
            return "";
        }

        switch (upc_E.length()) {

            case 6:
                // do nothing everything is OK
                break;

            case 7:
                // truncate last digit - assume that it is the UPCE check digit
                upc_E = upc_E.substring(0, 6);
                break;

            case 8:
                // truncate first and last digit
                //  assume that the first digit is the number system digit
                //  and the last digit is the UPCE check digit
                upc_E = upc_E.substring(1, 7);
                break;

            default:
                // invalid upc_E
                return "";
        }

        // break up the string into its 6 individual digits
        int digit1 = Integer.parseInt(upc_E.substring(0, 1));
        int digit2 = Integer.parseInt(upc_E.substring(1, 2));
        int digit3 = Integer.parseInt(upc_E.substring(2, 3));
        int digit4 = Integer.parseInt(upc_E.substring(3, 4));
        int digit5 = Integer.parseInt(upc_E.substring(4, 5));
        int digit6 = Integer.parseInt(upc_E.substring(5));

        // expand the 6 digit UPCE number to a 12 digit UPCA number
        String ManufacturerNumber;
        String ItemNumber;
        switch (digit6) {
            case 0:
            case 1:
            case 2:
                ManufacturerNumber = String.valueOf(digit1) + String.valueOf(digit2) + String.valueOf(digit6) + "00";
                ItemNumber = "00" + String.valueOf(digit3) + String.valueOf(digit4) + String.valueOf(digit5);
                break;

            case 3:
                ManufacturerNumber = String.valueOf(digit1) + String.valueOf(digit2) + String.valueOf(digit3) + "00";
                ItemNumber = "000" + String.valueOf(digit4) + String.valueOf(digit5); // original code was in error
                break;
            case 4:
                ManufacturerNumber = String.valueOf(digit1) + String.valueOf(digit2) + String.valueOf(digit3) + String.valueOf(digit4) + "0";
                ItemNumber = "0000" + String.valueOf(digit5);  // original code was in error
                break;

            default:
                ManufacturerNumber = String.valueOf(digit1) + String.valueOf(digit2) + String.valueOf(digit3) + String.valueOf(digit4) + String.valueOf(digit5);
                ItemNumber = "0000" + String.valueOf(digit6);
        }

        // put the number system digit "0" together with the manufacturer code and Item number
        String manufacturerItemNumbers = "0" + ManufacturerNumber + ItemNumber;

        // calculate the check digit - note UPCE and UPCA check digits are the same
        int check = 0;
        String Test;
        for (int x = 1; x < 12; x++) {
            if (x < 11) {
                Test = manufacturerItemNumbers.substring(x - 1, x);
            } else {
                Test = manufacturerItemNumbers.substring(x - 1);
            }
            switch (x) {
                case 1:
                case 3:
                case 5:
                case 7:
                case 9:
                case 11:
                    check = check + Integer.parseInt(Test) * 7;       // odd position digits multiplied by 7
                    break;

                case 2:
                case 4:
                case 6:
                case 8:
                case 10:
                    check = check + Integer.parseInt(Test) * 9;       // even position digits multiplied by 9
                    break;
            }
        }

        check = mod(check, 10) + 48;    // convert value to ASCII character value
        char ch = (char) check; //check character

        String result = manufacturerItemNumbers + ch; // put the pieces together and return
        return result;
    }

    private static int mod(int x, int y) {
        int result = x % y;
        if (result < 0)
            result += y;
        return result;
    }


    public static String UpcA2E(String upc_A) {
        // source: http://www.taltech.com/files/UPC.vb
        // test data: http://www.barcodeisland.com/upce.phtml
        String holdString;
        String upc_E = "";

        // If the source message string is less than 12 characters long, make it 12 characters
        if (upc_A.length() < 12) {
            holdString = "000000000000" + upc_A;
            upc_A = holdString.substring(holdString.length() - 12);
        }

        if (!upc_A.startsWith("0") && !upc_A.startsWith("1")) {
            // Invalid upc_A. upc_A must start with 0 or 1.)
            return "";
        } else {
            if (upc_A.substring(3, 6).equals("000")
                    || upc_A.substring(3, 6).equals("100")
                    || upc_A.substring(3, 6).equals("200")) {
                upc_E = upc_A.substring(1, 3) + upc_A.substring(8, 11) + upc_A.substring(3, 4);

            } else if (upc_A.substring(4, 6).equals("00")) {
                upc_E = upc_A.substring(1, 4) + upc_A.substring(9, 11) + "3";

            } else if (upc_A.substring(5, 6).equals("0")) {
                upc_E = upc_A.substring(1, 5) + upc_A.substring(10, 11) + "4";

            } else if (Integer.parseInt(upc_A.substring(10, 11)) >= 5) {
                upc_E = upc_A.substring(1, 6) + upc_A.substring(10, 11);
            }
        }

        return upc_E;
    }
}
