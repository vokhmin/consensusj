package com.msgilligan.bitcoinj.spring.service;

import static java.util.Collections.EMPTY_LIST;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.consensusj.jsonrpc.RPCClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

public class CustomRPCClient extends RPCClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomRPCClient.class);

    /**
     * Construct a JSON-RPC client from URI, username, and password
     * <p>
     * Typically you'll want to use {@link BitcoinClient} or one of its subclasses
     *
     * @param server      server URI should not contain username/password
     * @param rpcuser     username for the RPC HTTP connection
     * @param rpcpassword password for the RPC HTTP connection
     */
    public CustomRPCClient(URI server, String rpcuser, String rpcpassword) {
        super(server, rpcuser, rpcpassword);
    }

    public <R> R call(String method, List<Object> params) {
        try {
            return (R) send(method, params);
        } catch (IOException e) {
            LOGGER.warn("Unexpected call of a method {} with params {}", method, params, e);
            throw new RuntimeException(e);
        }
    }

    public <R> R call(String method, Object... params) {
        return (R) call(method, Arrays.asList(params));
    }

    public <R> R call(String method) {
        return (R) call(method, EMPTY_LIST);
    }

    public <R> R call(@NonNull Class<R> type, @NonNull String method) {
        return type.cast((R) call(method, EMPTY_LIST));
    }

}

