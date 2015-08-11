/**
 * Copyright 2014 Micheal Sean Gilligan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.msgilligan.bitcoinj.spring.service;

import org.bitcoinj.net.discovery.PeerDiscovery;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

import org.bitcoinj.core.*;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.security.Principal;
import java.util.List;

/**
 * A Service for maintaining Bitcoin peers
 */
@Named
public class PeerService {
    private static final String userAgentName = "PeerList";
    private static final String appVersion = "0.1";
    private NetworkParameters netParams;
    private PeerGroup peerGroup;
    private final SimpMessageSendingOperations messagingTemplate;

    @Inject
    public PeerService(NetworkParameters params,
                       PeerDiscovery peerDiscovery,
                       SimpMessageSendingOperations messagingTemplate) {
        this.netParams = params;
        this.peerGroup = new PeerGroup(params);
        this.messagingTemplate = messagingTemplate;
        peerGroup.setUserAgent(userAgentName, appVersion);
        peerGroup.addPeerDiscovery(peerDiscovery);
    }

    @PostConstruct
    public void start() {
        peerGroup.startAsync();
        peerGroup.addEventListener(new MyPeerEventListener() );
    }

    public NetworkParameters getNetworkParameters() {
        return this.netParams;
    }

    public Integer getBlockCount() {
        return peerGroup.getMostCommonChainHeight();
    }

    public Integer getConnectionCount() {
        return peerGroup.numConnectedPeers();
    }

    public List<Peer> getPeers() {
        List<Peer> peers = peerGroup.getConnectedPeers();
        return peers;
    }

    public void listPeers(Principal principal) {
        List<Peer> peers = peerGroup.getConnectedPeers();
        this.messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/peers", peers);
    }

    void onPGTransaction(Peer peer, Transaction tx) {
        this.messagingTemplate.convertAndSend("/topic/tx", tx);
    }

    private class MyPeerEventListener extends AbstractPeerEventListener {
        @Override
        public void onTransaction(Peer peer, Transaction tx) {
            System.out.println("Got transaction: " + tx.getHashAsString());
            onPGTransaction(peer, tx);
        }
    }


}