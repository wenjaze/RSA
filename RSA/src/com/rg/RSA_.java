package com.rg;

import java.math.BigInteger;
import java.util.Random;

public class RSA_ {

    public static BigInteger zero = new BigInteger("0");
    public static BigInteger one = new BigInteger("1");
    public static BigInteger two = new BigInteger("2");
    public static BigInteger q = getLargePrime();
    public static BigInteger p = getLargePrime();
    public static BigInteger d = GetD();
    public static BigInteger e = GetE();
    public static BigInteger n = GetN();
    public static BigInteger[] PK = {n,e};
    public static BigInteger SK = d;
    //public static BigInteger cipherText = Encrypt(plainText);

    // FML() == BigInteger.modPow();
    public static BigInteger Decrypt(BigInteger cipher){
        BigInteger DecryptedText = FML(cipher,SK,n);
        return DecryptedText;
    }

    public static BigInteger Encrypt(BigInteger plain){
        if (plain.compareTo(n) == -1){
            BigInteger CipherText = FML(plain,PK[1],PK[0]);
            return CipherText;
        }
        else {
            System.out.println("M > N");
            return zero.subtract(one);
        }
    }

    public static BigInteger FML(BigInteger base, BigInteger exp, BigInteger mod){
        base=base.remainder(mod);
        if(exp.equals(zero)){
            return one;
        }
        else if (exp.equals(one)){
            return base;
        }
        else if(exp.remainder(two).equals(zero)){
            return FML(base.multiply(base.remainder(mod)),exp.divide(two),mod);
        }
        else {
            return (base.multiply(FML(base,exp.subtract(one),mod))).remainder(mod);
        }
    }

    public static BigInteger GetN(){
        return p.multiply(q);
    }
    public static BigInteger PhiN(){
        return p.subtract(one).multiply(q.subtract(one));
    }
    public static BigInteger GCD(BigInteger a,BigInteger b)
    {
        if (a.equals(zero))
            return b;

        return GCD(b.mod(a), a);
    }

    public static BigInteger GetE(){
        BigInteger g = one;
        BigInteger a = PhiN();
        BigInteger b = new BigInteger("8");
        while(b.compareTo(a) == -1){
            if (GCD(a,b).equals(one))
            {
                if (b.compareTo(new BigInteger("500")) > 0) break;
            }
            b = b.add(one);
        }
        return b;
    }

    static BigInteger[] GCDE(BigInteger p, BigInteger q) {
        if (q.equals(zero))
            return new BigInteger[] { p, one, zero };

        BigInteger[] vals = GCDE(q, p.mod(q));
        BigInteger d = vals[0];
        BigInteger a = vals[2];
        BigInteger b = vals[1].subtract(p.divide(q).multiply(vals[2]));
        return new BigInteger[] { d, a, b };
    }

    public static BigInteger GetD(){
        BigInteger E = GetE();
        BigInteger phiN = PhiN();
        BigInteger[] temp = GCDE(E,phiN);
        BigInteger D = temp[1];
        if ((D.compareTo(one) != 1) && (D.compareTo(phiN) < 1)){
            D = D.add(phiN);
        }
        return D;
    }



    public static BigInteger RandomBigInt(BigInteger max){
        Random rand = new Random();
        BigInteger randomNumber;
        BigInteger upperLimit = max.subtract(BigInteger.ONE);
        do {
            randomNumber = new BigInteger(upperLimit.bitLength(), rand);
        } while (randomNumber.compareTo(upperLimit) >= 0);

        return randomNumber;
    }

    public static boolean CheckComposite(int a,BigInteger d, BigInteger n, BigInteger s) {
        BigInteger a_bigInt = BigInteger.valueOf(a);
        if (a_bigInt.modPow(d,n).equals(one)) {
            return false;
        }
        for (BigInteger i = zero; i.compareTo(s) == -1; i.add(one)) {
            // if pow(a, 2**i * d, n) == n-1
            if (a_bigInt.modPow(two.pow(i.intValue()).multiply(d),n).equals(n.subtract(one)))
            {
                return false;
            }
        }
        return true;
    }



    public static boolean isPrime(BigInteger n){
        // Quick check
        int n_int = n.intValue();
        if (n_int == 0 || n_int == 1 || n_int % 2 == 0) return false;
        if (n_int == 2) return true;

        BigInteger d = n.subtract(one);
        BigInteger s = zero;
        // S és d kiszámolása.
        while (d.mod(two).equals(zero)){
            s.add(one);
            d = d.divide(two);
        }
        // mivel a prím nagyobb mint 3474749660383 ezért 6 bázissal teszteljük.
        int[] bases = new int[]{2,3,7,11,13,17};

        for (int b : bases)
        {
            if (CheckComposite(b,d,n,s)){
                //System.out.println("...");
                return false;
            }
        }
        return true;
    }

    public static BigInteger getLargePrime(){
        BigInteger p = RandomBigInt(new BigInteger("1000000000000000000"));
        while(!isPrime(p)){
            p = RandomBigInt(new BigInteger("1000000000000000000"));
        }
        return p;
    }

    public static void main(String[] args){

        RSA_ Instance = new RSA_();
        BigInteger en;
        if (Encrypt(new BigInteger(args[0])).compareTo(zero) > -1){
            en = Encrypt(new BigInteger(args[0]));
        }
        else {
            return;
        }
        BigInteger de = Decrypt(en);

        System.out.println("----- RSA -----" +
                        "\nP : " +Instance.p+
                        "\nQ : " +Instance.q+
                        "\nN : " + Instance.n+
                        "\nPhiN : " + Instance.PhiN() +
                        "\nE : " + Instance.e +
                        "\nD : " + Instance.d +
                        "\nPK : (" + Instance.PK[0] +","+Instance.PK[1]+")"+
                        "\nSK : " +Instance.d+
                        "\nPlain Text : " + args[0] +
                        "\nCipherText : " + en +
                        "\nDecrypted Text : "+ de);
    }

}
