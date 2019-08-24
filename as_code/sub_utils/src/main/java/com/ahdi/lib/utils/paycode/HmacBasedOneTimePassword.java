
/*
 * Copyright 2013 Patrick Kelchner
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ahdi.lib.utils.paycode;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Generates HMAC-based one-time passwords according to <a
 * href="http://tools.ietf.org/html/rfc4226">RFC 4226</a>. 是否生成线程安全对象由构造器参数指定。
 * <p>
 * This class is designed to be reused per secret to generate passwords from.
 */
public final class HmacBasedOneTimePassword {
    public enum Algorithm {
        SHA1, SHA256, SHA512
    }

    private final Lock lock;
    private final Mac mac;

    /**
     * Generates the password corresponding to the given counter.
     */
    public final int generatePassword(final long counter) {
        return this.generatePassword(counter, (String[]) null);
    }

    private int getTruncation(final int numberOfDigits) {
        int truncation;
        switch (numberOfDigits) {
        case 6:
            truncation = 1000000;
            break;
        case 7:
            truncation = 10000000;
            break;
        case 8:
            truncation = 100000000;
            break;
        default:
            throw new IllegalArgumentException("'numberOfDigits' must be in the range [6..8]");
        }
        return truncation;
    }

    public final int generatePassword(final int numberOfDigits, final long counter) {
        return generatePassword(counter) % getTruncation(numberOfDigits);
    }

    public final int generatePassword(final int numberOfDigits, final long counter, final String... datas) {
        return generatePassword(counter, datas) % getTruncation(numberOfDigits);
    }

    private int getDigits(final int bitsOfDigits) {
        if (bitsOfDigits > 31 || bitsOfDigits < 8) {
            throw new IllegalArgumentException("'bitsOfDigits' must be in the range [8..31]");
        }
        return (int) ((1l << bitsOfDigits) - 1);
    }
    
    public final int generatePasswordByBits(final int bitsOfDigits, final long counter, final String... datas) {
        return generatePassword(counter, datas) & getDigits(bitsOfDigits);
    }

    public final int generatePasswordByBits(final int bitsOfDigits, final long counter) {
        return generatePassword(counter) & getDigits(bitsOfDigits);
    }

    public final int generatePassword(final long counter, final String... datas) {
        final byte[] counterBytes = ByteBuffer.allocate(8).order(ByteOrder.BIG_ENDIAN).putLong(counter).array();
        final byte[] hash;

        if (this.lock == null) {
            mac.update(counterBytes);
            if (datas != null) {
                for (String d : datas) {
                    if (d != null) {
                        mac.update(toBytes(d));
                    }
                }
            }
            hash = mac.doFinal();
        } else {
            lock.lock();
            try {
                mac.update(counterBytes);
                if (datas != null) {
                    for (String d : datas) {
                        if (d != null) {
                            mac.update(toBytes(d));
                        }
                    }
                }
                hash = mac.doFinal();
            } finally {
                lock.unlock();
            }
        }
        final int offset = hash[19] & 0x0F;
        final int truncatedHash = ((ByteBuffer) ByteBuffer.wrap(hash).order(ByteOrder.BIG_ENDIAN).position(offset))
                .getInt() & 0x7FFFFFFF;
        return truncatedHash;
    }

    private byte[] toBytes(String s) {
        try {
            return s.getBytes("US-ASCII");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 创建的对象非线程安全
     * 
     * @param algorithm
     *            the algorithm to use for hashing. The algorithm originally
     *            defined by RFC 4226 is SHA1.
     * @param numberOfDigits
     *            the number of digits returned by
     *            {@link #generatePassword(long)}
     * @param secret
     *            the secret to use for hashing
     * 
     */
    public HmacBasedOneTimePassword(final Algorithm algorithm, final byte[] secret) {
        this(algorithm, secret, false);
    }

    /**
     * 创建的对象非线程安全。使用Algorithm.SHA256
     * 
     * @param algorithm
     *            the algorithm to use for hashing. The algorithm originally
     *            defined by RFC 4226 is SHA1.
     * @param numberOfDigits
     *            the number of digits returned by
     *            {@link #generatePassword(long)}
     * @param secret
     *            the secret to use for hashing
     * 
     */
    public HmacBasedOneTimePassword(final byte[] secret) {
        this(Algorithm.SHA256, secret, false);
    }

    /**
     * @param algorithm
     *            the algorithm to use for hashing. The algorithm originally
     *            defined by RFC 4226 is SHA1.
     * @param numberOfDigits
     *            the number of digits returned by
     *            {@link #generatePassword(long)}
     * @param secret
     *            the secret to use for hashing
     * @param threadSafe
     *            是否保证使用线程安全
     */
    public HmacBasedOneTimePassword(final Algorithm algorithm, final byte[] secret, boolean threadSafe) {
        if (algorithm == null) {
            throw new NullPointerException("algorithm");
        }
        if (secret == null) {
            throw new NullPointerException("secret");
        }
        if (secret.length == 0) {
            throw new IllegalArgumentException("'secret' must contain at least one byte");
        }

        try {
            final SecretKeySpec key = new SecretKeySpec(secret, "raw");
            mac = Mac.getInstance("hmac" + algorithm);
            mac.init(key);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        } catch (InvalidKeyException e) {
            throw new IllegalStateException(e);
        }
        if (threadSafe) {
            this.lock = new ReentrantLock();
        } else {
            this.lock = null;
        }
    }
}
