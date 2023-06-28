package com.bloxbean.example.devkit;

import com.bloxbean.cardano.client.api.model.Result;
import com.bloxbean.cardano.client.api.util.PolicyUtil;
import com.bloxbean.cardano.client.backend.api.BackendService;
import com.bloxbean.cardano.client.backend.blockfrost.service.BFBackendService;
import com.bloxbean.cardano.client.cip.cip25.NFT;
import com.bloxbean.cardano.client.cip.cip25.NFTMetadata;
import com.bloxbean.cardano.client.exception.CborSerializationException;
import com.bloxbean.cardano.client.function.helper.SignerProviders;
import com.bloxbean.cardano.client.quicktx.QuickTxBuilder;
import com.bloxbean.cardano.client.quicktx.Tx;
import com.bloxbean.cardano.client.transaction.spec.Asset;
import com.bloxbean.cardano.client.transaction.spec.Policy;

import java.math.BigInteger;
import java.util.List;

public class Minting extends BaseTest {
    BackendService backendService = new BFBackendService(INDEXER_URL, "");

    void minting() throws CborSerializationException {
        Policy policy = PolicyUtil.createMultiSigScriptAtLeastPolicy("test_policy", 1, 1);
        BigInteger qty = BigInteger.valueOf(1000);

        NFT nft = NFT.create()
                .assetName("TestNFT")
                .image("http://xyz.com");
        NFTMetadata nftMetadata = NFTMetadata.create();
        nftMetadata.addNFT(policy.getPolicyId(), nft);

        List<Asset> tokensToMint = List.of(
                new Asset("TestFT", qty),
                new Asset("TestNFT", BigInteger.ONE)
        );

        Tx tx = new Tx()
                .mintAssets(policy.getPolicyScript(), tokensToMint, sender1Addr)
                .attachMetadata(nftMetadata)
                .from(sender1Addr);

        QuickTxBuilder quickTxBuilder = new QuickTxBuilder(backendService);
        Result<String> result = quickTxBuilder.compose(tx)
                .withSigner(SignerProviders.signerFrom(sender1))
                .withSigner(SignerProviders.signerFrom(policy))
                .completeAndWait(System.out::println);

        System.out.println(result);
    }

    public static void main(String[] args) throws Exception {
        new Minting().minting();
    }
}
