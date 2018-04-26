package com.msgilligan.bitcoinj.rpc

import com.msgilligan.bitcoinj.BaseRegTestSpec
import org.bitcoinj.core.Address
import org.bitcoinj.core.Coin
import spock.lang.Shared
import spock.lang.Stepwise

/**
 * Tests of creating and sending raw transactions via RPC
 */
@Stepwise
class ParticularBlockchainSpec extends BaseRegTestSpec {
    final static Coin fundingAmount = 10.btc
    final static Coin sendingAmount = 1.btc

    @Shared
    Address funder
    @Shared
    Address destinationAddress
    @Shared
    Address address100
    @Shared
    Address address101
    @Shared
    Address address102
    @Shared
    Address address103
    @Shared
    String[] txs
    @Shared
    String tx1Qaz
    @Shared
    String tx1Qax
    @Shared
    String tx1Qac

    def "Fund address as intermediate"() {
        when: "a new address is created"
        funder = getNewAddress()

        and: "coins are sent to the new address from a random source"
        sendToAddress(funder, fundingAmount)

        and: "a new block is mined"
        generate()

        then: "the address should have that balance"
        def balance = getBitcoinBalance(funder)
        balance == fundingAmount
    }

    def "Create addresses and the first unsigned transaction"() {
        when: "a new addresses 100 - 103 are created"
        address100 = getNewAddress("destinationAddress")
//        address101 = getNewAddress("destinationAddress")
//        address102 = getNewAddress("destinationAddress")
//        address103 = getNewAddress("destinationAddress")

        and: "we create a transaction, spending from #fundingAddress to new addresses"
        tx1Qaz = createRawTransaction(funder, address100, 1.1.btc)
//        tx1Qax = createRawTransaction(funder, [(address100): 1.1.btc, (address101): 1.2.btc,])
//        tx1Qac = createRawTransaction(funder, (address101), 1.3.btc)

        then: "there should be a raw transaction"
        [tx1Qaz].forEach {
//        [tx1Qac, tx1Qax, tx1Qaz].forEach({
            it != null
            it.size() > 0
        }
    }

    def "Sign unsigned raw transaction"() {
        when: "the transaction is signed"
        println "Sign a transaction: $tx1Qaz"
        def result = [tx1Qaz].collect { signRawTransaction(it) }
//        def result = [tx1Qaz, tx1Qaz, tx1Qac].collect({ signRawTransaction(it)})
        txs = result.collect { it["hex"] }

        then: "all inputs should be signed"
        result.forEach({ it["complete"] == true })
    }

    def "Broadcast signed raw transaction"() {
        when: "the transaction is sent"
        println "Send a raw transaction: $txs[0]"
        def txid = sendRawTransaction(txs[0])

        then: "there should be a transaction hash"
        txid != null

        when: "a new block is mined"
        generate()

        and: "we get info about the transaction"
        def broadcastedTransaction = getRawTransactionInfo(txid)

        then: "the transaction should have 1 confirmation"
        broadcastedTransaction.confirmations == 1

        and: "#funder address has a remainder of coins minus transaction fees"
        def balanceRemaining = getBitcoinBalance(funder)
        balanceRemaining == fundingAmount - 1.1.btc - stdTxFee
//        balanceRemaining == fundingAmount - 1.1.btc - 1.1.btc - 1.2.btc - 1.3.btc

        and: "#destinationAddress has a balance matching the spent amount"
        def balanceDestination = getBitcoinBalance(address100)
        balanceDestination == 1.1.btc
    }

    def "Send Bitcoin"() {
        when: "a new address is created"
        def newAddress = getNewAddress()

        and: "coins are sent to the new address from #destinationAddress"
        Coin amount = sendingAmount - stdTxFee
        sendBitcoin(destinationAddress, newAddress, amount)

        and: "a new block is mined"
        generate()

        then: "the sending address should be empty"
        def balanceSource = getBitcoinBalance(destinationAddress)
        balanceSource == 0.btc

        and: "the new adress should have the amount sent to"
        def balance = getBitcoinBalance(newAddress)
        balance == amount
    }
}
