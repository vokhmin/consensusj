package com.msgilligan.bitcoinj.spring.config;

import java.net.URI;
import java.net.URISyntaxException;

import org.consensusj.jsonrpc.RPCClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.msgilligan.bitcoinj.spring.controller.ExplorerController;

/**
 *
 */
@Configuration
@EnableWebMvc
public class WebConfig extends WebMvcAutoConfiguration {

    @Value("bitcoin.rpc.url")
    private String apiUri = "http://localhost:18332";

    @Value("bitcoin.rpc.user")
    private String user = "bitcoin";

    @Value("bitcoin.rpc.password")
    private String password = "password";

    @Bean
    private RPCClient rpcClient() {
        try {
            final URI uri = new URI(apiUri);
            return new RPCClient(uri, user, password);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    private ExplorerController controller() {
        return new ExplorerController(rpcClient());
    }

}
