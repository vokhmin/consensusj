package com.msgilligan.bitcoinj.spring.controller;

/**
 * REST and WebSocket (STOMP) Spring MVC Peer Controller
 */

import static java.util.Collections.EMPTY_LIST;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.consensusj.jsonrpc.RPCClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ExplorerController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExplorerController.class);

    private final RPCClient rpcClient;

    public ExplorerController(RPCClient rpcClient) {
        this.rpcClient = rpcClient;
    }

    @RequestMapping("/getblockcount")
    public Long getBlockCount() {
        return call("getblockcount");
    }

    @RequestMapping("/getblockhash")
    public String getBlockHash() {
        return call("getblockhash");
    }

    // Helper methods ...

    public <R> R call(String method, List<Object> params) {
        try {
            return (R) rpcClient.send(method, params);
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
}
