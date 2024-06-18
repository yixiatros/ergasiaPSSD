package com.example.ergasiapssd;

import java.util.HashMap;
import java.util.Map;

public abstract class GreekToEnglish {

    public static String convert(String term) {
        StringBuilder stringBuilder = new StringBuilder();
        Map<String, String> values = new HashMap<>();

        // Uppercase
        values.put("Α", "A");
        values.put("Ά", "A");
        values.put("Β", "B");
        values.put("Γ", "G");
        values.put("Δ", "D");
        values.put("Ε", "E");
        values.put("Έ", "E");
        values.put("Ζ", "Z");
        values.put("Η", "I");
        values.put("Ή", "I");
        values.put("Θ", "TH");
        values.put("Ι", "I");
        values.put("Ί", "I");
        values.put("Ϊ", "I");
        values.put("Κ", "K");
        values.put("Λ", "L");
        values.put("Μ", "M");
        values.put("Ν", "N");
        values.put("Ξ", "KS");
        values.put("Ο", "O");
        values.put("Ό", "O");
        values.put("Π", "P");
        values.put("Ρ", "R");
        values.put("Σ", "S");
        values.put("Τ", "T");
        values.put("Υ", "Y");
        values.put("Ύ", "Y");
        values.put("Φ", "F");
        values.put("Χ", "X");
        values.put("Ψ", "PS");
        values.put("Ω", "W");
        values.put("Ώ", "W");

        // Lowercase
        values.put("α", "a");
        values.put("ά", "a");
        values.put("β", "b");
        values.put("γ", "g");
        values.put("δ", "d");
        values.put("ε", "e");
        values.put("έ", "e");
        values.put("ζ", "z");
        values.put("η", "i");
        values.put("ή", "i");
        values.put("θ", "th");
        values.put("ι", "i");
        values.put("ί", "i");
        values.put("ϊ", "i");
        values.put("κ", "k");
        values.put("λ", "l");
        values.put("μ", "m");
        values.put("ν", "n");
        values.put("ξ", "ks");
        values.put("ο", "o");
        values.put("ό", "o");
        values.put("π", "p");
        values.put("ρ", "r");
        values.put("σ", "s");
        values.put("ς", "s");
        values.put("τ", "t");
        values.put("υ", "y");
        values.put("ύ", "y");
        values.put("φ", "f");
        values.put("χ", "x");
        values.put("ψ", "ps");
        values.put("ω", "w");
        values.put("ώ", "w");

        for (int i = 0; i < term.length() ; i++) {
            if(values.containsKey(String.valueOf(term.charAt(i)))){
                stringBuilder.append(values.get(String.valueOf(term.charAt(i))));
            }else {
                stringBuilder.append(term.charAt(i));
            }
        }

        return stringBuilder.toString();
    }
}
