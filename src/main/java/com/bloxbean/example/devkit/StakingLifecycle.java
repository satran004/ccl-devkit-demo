package com.bloxbean.example.devkit;

import com.bloxbean.cardano.client.address.Address;
import com.bloxbean.cardano.client.address.AddressProvider;
import com.bloxbean.cardano.client.api.exception.ApiException;
import com.bloxbean.cardano.client.api.model.Result;
import com.bloxbean.cardano.client.backend.api.BackendService;
import com.bloxbean.cardano.client.backend.blockfrost.service.BFBackendService;
import com.bloxbean.cardano.client.backend.model.AccountInformation;
import com.bloxbean.cardano.client.cip.cip20.MessageMetadata;
import com.bloxbean.cardano.client.function.helper.SignerProviders;
import com.bloxbean.cardano.client.quicktx.QuickTxBuilder;
import com.bloxbean.cardano.client.quicktx.Tx;
import com.bloxbean.cardano.client.util.JsonUtil;

import java.math.BigInteger;

public class StakingLifecycle extends BaseTest {
    BackendService backendService = new BFBackendService(INDEXER_URL, "");

    void stakeRegistration() {
        QuickTxBuilder quickTxBuilder = new QuickTxBuilder(backendService);
        Tx tx = new Tx()
                .registerStakeAddress(sender1Addr)
                .attachMetadata(MessageMetadata.create().add("This is a stake registration tx"))
                .from(sender1Addr);

        Result<String> result = quickTxBuilder.compose(tx)
                .withSigner(SignerProviders.signerFrom(sender1))
                .withTxInspector((txn) -> System.out.println(JsonUtil.getPrettyJson(txn)))
                .completeAndWait(msg -> System.out.println(msg));

        System.out.println(result);
    }

    void stakeDeRegistration() {
        Tx tx = new Tx()
                .deregisterStakeAddress(AddressProvider.getStakeAddress(new Address(sender1Addr)))
                .attachMetadata(MessageMetadata.create().add("This is a stake deregistration tx"))
                .from(sender1Addr);

        QuickTxBuilder quickTxBuilder = new QuickTxBuilder(backendService);
        Result<String> result = quickTxBuilder.compose(tx)
                .withSigner(SignerProviders.signerFrom(sender1))
                .withSigner(SignerProviders.stakeKeySignerFrom(sender1))
                .withTxInspector((txn) -> System.out.println(JsonUtil.getPrettyJson(txn)))
                .completeAndWait(msg -> System.out.println(msg));

        System.out.println(result);
    }

    void stakeDelegation() {
        QuickTxBuilder quickTxBuilder = new QuickTxBuilder(backendService);
        Tx tx = new Tx()
                .delegateTo(sender1Addr, "pool1wvqhvyrgwch4jq9aa84hc8q4kzvyq2z3xr6mpafkqmx9wce39zy")
                .attachMetadata(MessageMetadata.create().add("This is a stake delegation tx"))
                .from(sender1Addr);

        Result<String> result = quickTxBuilder.compose(tx)
                .withSigner(SignerProviders.signerFrom(sender1))
                .withSigner(SignerProviders.stakeKeySignerFrom(sender1))
                .withTxInspector((txn) -> System.out.println(JsonUtil.getPrettyJson(txn)))
                .completeAndWait(msg -> System.out.println(msg));

        System.out.println(result);
    }

    void rewardWithdrawl() throws ApiException {
        //stake_test1uz53eam4lw8plhlgs2exq983r4tt63mgzfc22h73teksvnq5hwnfs
        String stakeAddress = AddressProvider.getStakeAddress(new Address(sender1Addr)).toBech32();
        AccountInformation accountInformation = backendService.getAccountService().getAccountInformation(stakeAddress).getValue();
        System.out.println(accountInformation);
        if (accountInformation.getWithdrawableAmount() == null) {
            System.out.println("No rewards to withdraw");
        }

        System.out.println("Address: " + stakeAddress);
        System.out.println("Withdrawable amount: " + accountInformation.getWithdrawableAmount());
        BigInteger rewardAmt = new BigInteger(accountInformation.getWithdrawableAmount());

        QuickTxBuilder quickTxBuilder = new QuickTxBuilder(backendService);
        Tx tx = new Tx()
                .withdraw(stakeAddress, rewardAmt)
                .attachMetadata(MessageMetadata.create().add("This is a withdraw tx"))
                .from(sender1Addr);

        Result<String> result = quickTxBuilder.compose(tx)
                .withSigner(SignerProviders.signerFrom(sender1))
                .withSigner(SignerProviders.stakeKeySignerFrom(sender1))
                .completeAndWait(msg -> System.out.println(msg));

        System.out.println(result);
    }

    public static void main(String[] args) throws Exception {
        StakingLifecycle stakingLifecycle = new StakingLifecycle();
        stakingLifecycle.stakeRegistration();
//        stakingLifecycle.stakeDelegation();
//        stakingLifecycle.rewardWithdrawl();
//        stakingLifecycle.stakeDeRegistration();
    }
}
