package com.msgilligan.bitcoinj.spring.controller;

/**
 * REST and WebSocket (STOMP) Spring MVC Peer Controller
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.msgilligan.bitcoinj.spring.service.CustomRPCClient;


@RestController
public class ExplorerController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExplorerController.class);

    private final CustomRPCClient rpcClient;

    public ExplorerController(CustomRPCClient rpcClient) {
        this.rpcClient = rpcClient;
    }

    @RequestMapping("/getblockcount")
    public Long getBlockCount() {
        return rpcClient.call(Number.class, "getblockcount").longValue();
    }

    @RequestMapping("/getblockhash")
    public String getBlockHash() {
        return rpcClient.call("getblockhash");
    }

}