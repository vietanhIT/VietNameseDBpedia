package com.vietnamesedbpedia.dbpedia;

import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created by vietanknb on 3/3/2017.
 */
public class StringKernel {
    /**
     * Define a static logger variable so that it references the
     * Logger instance named <code>StringKernel</code>.
     */
    static Logger logger = Logger.getLogger(org.fbk.cit.hlt.thewikimachine.util.StringKernel.class.getName());
    //
    private double lambda;

    //
    private int length;

    public static String join(Collection<String> col, String delim) {
        StringBuilder sb = new StringBuilder();
        Iterator<String> iter = col.iterator();
        if (iter.hasNext()) {
            sb.append(iter.next());
        }
        while (iter.hasNext()) {
            sb.append(delim);
            sb.append(iter.next());
        }
        return sb.toString();
    }

    //
    public StringKernel(double lambda, int length) {
        this.lambda = lambda;
        this.length = length;

//		logger.info("lambda: " + lambda);
//		logger.info("length: " + length);
    } // end constructor

    //
    public double get(String s1, String s2) {
        return k(s1.split(" "), s2.split(" "), length);
    } // end compare

    public double getNormalized(String[] s1, String[] s2) {
        double k0 = k(s1, s2, length);
        double k1 = k(s1, s1, length);
        double k2 = k(s2, s2, length);
        return k0 / Math.sqrt(k1 * k2);
    }

    //
    private double k(String[] s, String[] t, int n) {
        logger.debug("k('" + s + "', '" + t + "', " + n + ")");
        logger.debug("k(" + s + ", " + t +")");
        if (Math.min(s.length, t.length) < n) {
            logger.debug("k.return 0");
            return 0;
        }

        String[] p = prefix(s);
        String x = last(s);

        logger.debug("k.p: " + p.toString());
        logger.debug("k.x: " + x);

        return k(p, t, n) + sumk(p, t, x, n - 1);
    } // end k

    //
    private double sumk(String[] s, String[] t, String x, int n) {
        logger.debug("sumk('" + s.toString() + "', '" + t.toString() + "', '" +  x + "', " + n + ")");

        double sum = 0;

        for (int j = 0; j < t.length; j++) {
            logger.debug("sumk.t[" + j + "]='" + t[j] + "' vs. '" + x + "'");

            if (t[j].equals(x)) {
                logger.debug("sumk.t[" + j + "]='" + x + "'");
                sum += kp(s, prefix(t, j), n) * Math.pow(lambda, 2);
                logger.debug("sumk.sum[1.." + j + "]=" + sum);
            }
        }

        logger.debug("sumk.sum: " + sum);
        return sum;
    } // end sumk

    //
    private double kp(String[] s, String[] t, int n) {
        logger.debug("kp('" + s.toString() + "', '" + t.toString() + "', " + n + ")");

        if (n == 0) {
            logger.debug("kp.return 1");
            return 1;
        }

        if (Math.min(s.length, t.length) < n) {
            logger.debug("kp.return 0");
            return 0;
        }

        String[] p = prefix(s);
        String x = last(s);

        return (lambda * kp(p, t, n)) + sumkp(p, t, x, n - 1);
    } // end kp

    //
    private double sumkp(String[] s, String[] t, String x, int n) {
        logger.debug("sumkp('" + s.toString() + "', '" + t.toString() + "', '" +  x + "', " + n + ")");

        double sum = 0;

        for (int j = 0; j < t.length; j++) {
            logger.debug("sumkp.t[" + j + "]=" + x + "' vs. " + x + "'");

            if (t[j].equals(x)) {
                logger.debug("sumk.t[" + j + "]='" + x + "'");
                logger.debug(" pre sumkp.sum: " + sum);
                sum += kp(s, prefix(t, j), n) * Math.pow(lambda, t.length - j +1);
                logger.debug("sumkp.sum[1.." + j + "]=" + sum);
            }
        }

        logger.debug("sumkp.sum: " + sum);
        return sum;
    } // end sumkp

    //
    private String last(String[] t) {
        return t[t.length - 1];
    } // end last

    //
    private String[] prefix(String[] t) {
        return prefix(t, t.length - 1);
    } // end prefix

    //
    private String[] prefix(String[] s, int j) {
        logger.debug("prefix: '" + s.toString() + "', " + j);

        if (j < 0) {
            return null;
        }

        String[] d = new String[j];

        for (int i = 0; i < j; i++) {
            d[i] = s[i];
        }

        return d;
    } // end prefix

    //
    public static void main(String args[]) throws Exception {
//		String logConfig = System.getProperty("log-config");
//		if (logConfig == null) {
//			logConfig = "log-config.txt";
//		}
//
//		PropertyConfigurator.configure(logConfig);
//
//		if (args.length != 4) {
//			System.err.println("java org.itc.irst.tcc.kre.ml.kernels.StringKernel lambda length s t");
//			System.exit(-1);
//		}

//		double lambda = Double.parseDouble(args[0]);
//		int length = Integer.parseInt(args[1]);
//		String s = args[2];
//		String t = args[3];

        double lambda = .2;
        int length = 1;
        String s = "restingplace hello";
        String t = "1 restingplace hello 1 h ert";

        StringKernel sk = new StringKernel(lambda, length);
        double k0 = sk.get(s, t);
        double k1 = sk.get(s, s);
        double k2 = sk.get(t, t);
        double k = k0 / Math.sqrt(k1 * k2);
        logger.info(k0);
        logger.info("k('" + s + "', '" + t + "')=" + k);
        logger.info(k1+"---"+k2);
//        logger.info(Math.pow(lambda, 2));
    } // end main

}
