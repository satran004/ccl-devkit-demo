package com.bloxbean.example.devkit;

import com.bloxbean.cardano.aiken.AikenTransactionEvaluator;
import com.bloxbean.cardano.client.api.model.Result;
import com.bloxbean.cardano.client.backend.api.BackendService;
import com.bloxbean.cardano.client.backend.blockfrost.service.BFBackendService;
import com.bloxbean.cardano.client.exception.CborSerializationException;
import com.bloxbean.cardano.client.function.helper.SignerProviders;
import com.bloxbean.cardano.client.plutus.spec.BigIntPlutusData;
import com.bloxbean.cardano.client.plutus.spec.PlutusScript;
import com.bloxbean.cardano.client.plutus.util.PlutusUtil;
import com.bloxbean.cardano.client.quicktx.QuickTxBuilder;
import com.bloxbean.cardano.client.quicktx.ScriptTx;
import com.bloxbean.cardano.client.transaction.spec.Asset;
import java.math.BigInteger;


public class MultiScriptMinting extends QuickTxBaseTest {
    BackendService backendService = new BFBackendService(INDEXER_URL, "");

    void scriptMinting() throws CborSerializationException {
        String aikenCompiledCode1 = "581801000032223253330043370e00290010a4c2c6eb40095cd1"; //redeemer = 1
        PlutusScript plutusScript1 = PlutusUtil.getPlutusV2Script(aikenCompiledCode1);

        String aikenCompileCode2 = "581801000032223253330043370e00290020a4c2c6eb40095cd1"; //redeemer = 2
        PlutusScript plutusScript2 = PlutusUtil.getPlutusV2Script(aikenCompileCode2);

        String aikenCompileCode3 = "581801000032223253330043370e00290030a4c2c6eb40095cd1";
        PlutusScript plutusScript3 = PlutusUtil.getPlutusV2Script(aikenCompileCode3);

        Asset asset1 = new Asset("PlutusMintToken-1", BigInteger.valueOf(8000));
        Asset asset2 = new Asset("PlutusMintToken-2", BigInteger.valueOf(5000));
        Asset asset3 = new Asset("PlutusMintToken-3", BigInteger.valueOf(2000));

        System.out.println("policy 1: " + plutusScript1.getPolicyId());
        System.out.println("policy 2: " + plutusScript2.getPolicyId());

        ScriptTx scriptTx = new ScriptTx()
                .mintAsset(plutusScript1, asset1, BigIntPlutusData.of(1), receiver1Addr)
                .mintAsset(plutusScript2, asset2, BigIntPlutusData.of(2), sender1Addr)
                .mintAsset(plutusScript3, asset3, BigIntPlutusData.of(3), receiver1Addr)
                .withChangeAddress(sender2Addr);

        QuickTxBuilder quickTxBuilder = new QuickTxBuilder(backendService);
        Result<String> result1 = quickTxBuilder.compose(scriptTx)
                .feePayer(sender2Addr)
                .withSigner(SignerProviders.signerFrom(sender2))
                .withTxEvaluator(new AikenTransactionEvaluator(backendService))
                .completeAndWait(System.out::println);
    }

    public static void main(String[] args) throws Exception {
        new MultiScriptMinting().scriptMinting();
    }
}
