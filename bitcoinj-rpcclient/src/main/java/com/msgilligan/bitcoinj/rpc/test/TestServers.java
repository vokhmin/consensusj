package com.msgilligan.bitcoinj.rpc.test;

/**
 * Credentials to server under test.
 */
public class TestServers {
    private static final TestServers INSTANCE = new TestServers();
    private final String rpcTestUser = System.getProperty("omni.test.rpcTestUser", "bitcoin");
    private final String rpcTestPassword = System.getProperty("omni.test.rpcTestPassword", "password");

    public static TestServers getInstance() {
        return INSTANCE;
    }

    public String getRpcTestUser() {
        return rpcTestUser;
    }

    public String getRpcTestPassword() {
        return rpcTestPassword;
    }
}
