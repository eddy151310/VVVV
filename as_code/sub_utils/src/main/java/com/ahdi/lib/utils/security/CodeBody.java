package com.ahdi.lib.utils.security;

public interface CodeBody {

    byte[] encode(byte[] bytes);

    byte[] decode(byte[] bytes);
}
