package com.bloxbean.example.devkit;

import com.bloxbean.cardano.client.account.Account;
import com.bloxbean.cardano.client.api.model.Utxo;
import com.bloxbean.cardano.client.backend.api.*;
import com.bloxbean.cardano.client.common.model.Networks;

import java.util.List;
import java.util.Optional;

public class BaseTest {
    String INDEXER_URL = "http://localhost:8080/api/v1/";

    //addr_test1qqxnp3khzm7kcj9t23hskehat7428ghsenk0pfew4rqy5v9frnmht7uwrl073q4jvq20z82kh4rksyns540azhndqexqpvhgqr
    String sender1Mnemonic = "kit color frog trick speak employ suit sort bomb goddess jewel primary spoil fade person useless measure manage warfare reduce few scrub beyond era";
    Account sender1 = new Account(Networks.testnet(), sender1Mnemonic);
    String sender1Addr = sender1.baseAddress();

    //addr_test1qrs2a2hjfs2wt8r3smzwmptezmave3yjgws068hp0qsflmcypglx0rl69tp49396282ns02caz4cx7a2n290h2df0j3qjku4dy
    String sender2Mnemonic = "essence pilot click armor alpha noise mixture soldier able advice multiply inject ticket pride airport uncover honey desert curtain sun true toast valve half";
    Account sender2 = new Account(Networks.testnet(), sender2Mnemonic);
    String sender2Addr = sender2.baseAddress();

    String receiver1Addr = "addr_test1qqwpl7h3g84mhr36wpetk904p7fchx2vst0z696lxk8ujsjyruqwmlsm344gfux3nsj6njyzj3ppvrqtt36cp9xyydzqzumz82";
    String receiver2Addr = "addr_test1qqqvjp4ffcdqg3fmx0k8rwamnn06wp8e575zcv8d0m3tjn2mmexsnkxp7az774522ce4h3qs4tjp9rxjjm46qf339d9sk33rqn";

    protected void checkIfUtxoAvailable(BackendService backendService, String txHash, String address) {
        Optional<Utxo> utxo = Optional.empty();
        int count = 0;
        while (utxo.isEmpty()) {
            if (count++ >= 20)
                break;
            List<Utxo> utxos = new DefaultUtxoSupplier(backendService.getUtxoService()).getAll(address);
            utxo = utxos.stream().filter(u -> u.getTxHash().equals(txHash))
                    .findFirst();
            System.out.println("Try to get new output... txhash: " + txHash);
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
            }
        }
    }

}
