package com.penguinstudios.tradeguardian.contract;

import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.reactivex.Flowable;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 1.5.0.
 */
@SuppressWarnings("rawtypes")
public class Escrow extends Contract {
    public static String BINARY;

    public static void loadContract(String bin) {
        BINARY = bin;
    }

    public static final String FUNC_BUYER = "buyer";

    public static final String FUNC_BUYERDEPOSIT = "buyerDeposit";

    public static final String FUNC_CONTRACTCREATIONDATE = "contractCreationDate";

    public static final String FUNC_CURRENTSTATE = "currentState";

    public static final String FUNC_DEADLINEFORINITIALDEPOSITS = "deadlineForInitialDeposits";

    public static final String FUNC_DESCRIPTION = "description";

    public static final String FUNC_FEERECIPIENT = "feeRecipient";

    public static final String FUNC_HASBUYERSETTLED = "hasBuyerSettled";

    public static final String FUNC_HASSELLERSETTLED = "hasSellerSettled";

    public static final String FUNC_ITEMPRICE = "itemPrice";

    public static final String FUNC_SELLER = "seller";

    public static final String FUNC_SELLERDEPOSIT = "sellerDeposit";

    public static final String FUNC_SETBUYERHASRECEIVEDCORRECTITEM = "setBuyerHasReceivedCorrectItem";

    public static final String FUNC_SETBUYERHASRECEIVEDINCORRECTITEM = "setBuyerHasReceivedIncorrectItem";

    public static final String FUNC_SETSELLERHASGIVENITEM = "setSellerHasGivenItem";

    public static final String FUNC_SETTLE = "settle";

    public static final String FUNC_USERBALANCES = "userBalances";

    public static final String FUNC_WITHDRAW = "withdraw";

    public static final Event BUYERDEPOSITED_EVENT = new Event("BuyerDeposited",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {
            }, new TypeReference<Uint256>() {
            }));
    ;

    public static final Event CORRECTITEMRECEIVED_EVENT = new Event("CorrectItemReceived",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {
            }, new TypeReference<Address>(true) {
            }, new TypeReference<Uint256>() {
            }, new TypeReference<Uint256>() {
            }, new TypeReference<Uint256>() {
            }));
    ;

    public static final Event INCORRECTITEMRECEIVED_EVENT = new Event("IncorrectItemReceived",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {
            }, new TypeReference<Uint256>() {
            }));
    ;

    public static final Event ITEMSENT_EVENT = new Event("ItemSent",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {
            }));
    ;

    public static final Event SELLERDEPOSITED_EVENT = new Event("SellerDeposited",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {
            }, new TypeReference<Uint256>() {
            }));
    ;

    public static final Event SETTLED_EVENT = new Event("Settled",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {
            }, new TypeReference<Address>(true) {
            }, new TypeReference<Uint256>() {
            }, new TypeReference<Uint256>() {
            }));
    ;

    public static final Event TRADECANCELED_EVENT = new Event("TradeCanceled",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {
            }, new TypeReference<Uint256>() {
            }));
    ;

    @Deprecated
    protected Escrow(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected Escrow(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected Escrow(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected Escrow(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static List<BuyerDepositedEventResponse> getBuyerDepositedEvents(TransactionReceipt transactionReceipt) {
        List<Log> logs = transactionReceipt.getLogs();
        List<BuyerDepositedEventResponse> responses = new ArrayList<>();

        for (Log log : logs) {
            EventValuesWithLog eventValues = staticExtractEventParametersWithLog(BUYERDEPOSITED_EVENT, log);
            if (eventValues == null) continue; // Skip if the log does not match the event signature

            BuyerDepositedEventResponse typedResponse = new BuyerDepositedEventResponse();
            typedResponse.log = log;
            typedResponse.buyer = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static BuyerDepositedEventResponse getBuyerDepositedEventFromLog(Log log) {
        EventValuesWithLog eventValues = staticExtractEventParametersWithLog(BUYERDEPOSITED_EVENT, log);
        BuyerDepositedEventResponse typedResponse = new BuyerDepositedEventResponse();
        typedResponse.log = log;
        typedResponse.buyer = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<BuyerDepositedEventResponse> buyerDepositedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getBuyerDepositedEventFromLog(log));
    }

    public Flowable<BuyerDepositedEventResponse> buyerDepositedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(BUYERDEPOSITED_EVENT));
        return buyerDepositedEventFlowable(filter);
    }

    public static List<CorrectItemReceivedEventResponse> getCorrectItemReceivedEvents(TransactionReceipt transactionReceipt) {
        List<Log> logs = transactionReceipt.getLogs();
        List<CorrectItemReceivedEventResponse> responses = new ArrayList<>();

        for (Log log : logs) {
            EventValuesWithLog eventValues = staticExtractEventParametersWithLog(CORRECTITEMRECEIVED_EVENT, log);
            if (eventValues == null) continue; // Skip if the log does not match the event signature

            CorrectItemReceivedEventResponse typedResponse = new CorrectItemReceivedEventResponse();
            typedResponse.log = log;
            typedResponse.buyer = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.seller = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.amountToFeeRecipient = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.amountToSeller = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.amountToBuyer = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static CorrectItemReceivedEventResponse getCorrectItemReceivedEventFromLog(Log log) {
        EventValuesWithLog eventValues = staticExtractEventParametersWithLog(CORRECTITEMRECEIVED_EVENT, log);
        CorrectItemReceivedEventResponse typedResponse = new CorrectItemReceivedEventResponse();
        typedResponse.log = log;
        typedResponse.buyer = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.seller = (String) eventValues.getIndexedValues().get(1).getValue();
        typedResponse.amountToFeeRecipient = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.amountToSeller = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
        typedResponse.amountToBuyer = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
        return typedResponse;
    }

    public Flowable<CorrectItemReceivedEventResponse> correctItemReceivedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getCorrectItemReceivedEventFromLog(log));
    }

    public Flowable<CorrectItemReceivedEventResponse> correctItemReceivedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(CORRECTITEMRECEIVED_EVENT));
        return correctItemReceivedEventFlowable(filter);
    }

    public static List<IncorrectItemReceivedEventResponse> getIncorrectItemReceivedEvents(TransactionReceipt transactionReceipt) {
        List<Log> logs = transactionReceipt.getLogs();
        List<IncorrectItemReceivedEventResponse> responses = new ArrayList<>();

        for (Log log : logs) {
            EventValuesWithLog eventValues = staticExtractEventParametersWithLog(INCORRECTITEMRECEIVED_EVENT, log);
            if (eventValues == null)
                continue; // If the log does not correspond to the event, skip it

            IncorrectItemReceivedEventResponse typedResponse = new IncorrectItemReceivedEventResponse();
            typedResponse.log = log;
            typedResponse.buyer = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.amountHeldForSettlement = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static IncorrectItemReceivedEventResponse getIncorrectItemReceivedEventFromLog(Log log) {
        EventValuesWithLog eventValues = staticExtractEventParametersWithLog(INCORRECTITEMRECEIVED_EVENT, log);
        IncorrectItemReceivedEventResponse typedResponse = new IncorrectItemReceivedEventResponse();
        typedResponse.log = log;
        typedResponse.buyer = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.amountHeldForSettlement = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<IncorrectItemReceivedEventResponse> incorrectItemReceivedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getIncorrectItemReceivedEventFromLog(log));
    }

    public Flowable<IncorrectItemReceivedEventResponse> incorrectItemReceivedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(INCORRECTITEMRECEIVED_EVENT));
        return incorrectItemReceivedEventFlowable(filter);
    }

    public static List<ItemSentEventResponse> getItemSentEvents(TransactionReceipt transactionReceipt) {
        List<Log> logs = transactionReceipt.getLogs();
        List<ItemSentEventResponse> responses = new ArrayList<>();

        for (Log log : logs) {
            EventValuesWithLog eventValues = staticExtractEventParametersWithLog(ITEMSENT_EVENT, log);
            if (eventValues == null)
                continue; // If the log does not correspond to the ITEMSENT_EVENT, skip it

            ItemSentEventResponse typedResponse = new ItemSentEventResponse();
            typedResponse.log = log; // Directly set the log from the iteration
            typedResponse.seller = (String) eventValues.getIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static ItemSentEventResponse getItemSentEventFromLog(Log log) {
        EventValuesWithLog eventValues = staticExtractEventParametersWithLog(ITEMSENT_EVENT, log);
        ItemSentEventResponse typedResponse = new ItemSentEventResponse();
        typedResponse.log = log;
        typedResponse.seller = (String) eventValues.getIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<ItemSentEventResponse> itemSentEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getItemSentEventFromLog(log));
    }

    public Flowable<ItemSentEventResponse> itemSentEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(ITEMSENT_EVENT));
        return itemSentEventFlowable(filter);
    }

    public static List<SellerDepositedEventResponse> getSellerDepositedEvents(TransactionReceipt transactionReceipt) {
        List<Log> logs = transactionReceipt.getLogs();
        List<SellerDepositedEventResponse> responses = new ArrayList<>();

        for (Log log : logs) {
            EventValuesWithLog eventValues = staticExtractEventParametersWithLog(SELLERDEPOSITED_EVENT, log);
            if (eventValues == null)
                continue; // Skip logs that don't match the SELLERDEPOSITED_EVENT

            SellerDepositedEventResponse typedResponse = new SellerDepositedEventResponse();
            typedResponse.log = log;
            typedResponse.seller = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static SellerDepositedEventResponse getSellerDepositedEventFromLog(Log log) {
        EventValuesWithLog eventValues = staticExtractEventParametersWithLog(SELLERDEPOSITED_EVENT, log);
        SellerDepositedEventResponse typedResponse = new SellerDepositedEventResponse();
        typedResponse.log = log;
        typedResponse.seller = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<SellerDepositedEventResponse> sellerDepositedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getSellerDepositedEventFromLog(log));
    }

    public Flowable<SellerDepositedEventResponse> sellerDepositedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(SELLERDEPOSITED_EVENT));
        return sellerDepositedEventFlowable(filter);
    }

    public static List<SettledEventResponse> getSettledEvents(TransactionReceipt transactionReceipt) {
        List<Log> logs = transactionReceipt.getLogs();
        List<SettledEventResponse> responses = new ArrayList<>();

        for (Log log : logs) {
            EventValuesWithLog eventValues = staticExtractEventParametersWithLog(SETTLED_EVENT, log);
            if (eventValues == null)
                continue; // Skip logs that do not correspond to the SETTLED_EVENT

            SettledEventResponse typedResponse = new SettledEventResponse();
            typedResponse.log = log;
            typedResponse.buyer = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.seller = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.buyerAmount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.sellerAmount = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static SettledEventResponse getSettledEventFromLog(Log log) {
        EventValuesWithLog eventValues = staticExtractEventParametersWithLog(SETTLED_EVENT, log);
        SettledEventResponse typedResponse = new SettledEventResponse();
        typedResponse.log = log;
        typedResponse.buyer = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.seller = (String) eventValues.getIndexedValues().get(1).getValue();
        typedResponse.buyerAmount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.sellerAmount = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<SettledEventResponse> settledEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getSettledEventFromLog(log));
    }

    public Flowable<SettledEventResponse> settledEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(SETTLED_EVENT));
        return settledEventFlowable(filter);
    }

    public static List<TradeCanceledEventResponse> getTradeCanceledEvents(TransactionReceipt transactionReceipt) {
        List<Log> logs = transactionReceipt.getLogs();
        List<TradeCanceledEventResponse> responses = new ArrayList<>();

        for (Log log : logs) {
            EventValuesWithLog eventValues = staticExtractEventParametersWithLog(TRADECANCELED_EVENT, log);
            if (eventValues == null)
                continue; // Skip logs that do not correspond to the TRADECANCELED_EVENT

            TradeCanceledEventResponse typedResponse = new TradeCanceledEventResponse();
            typedResponse.log = log;
            typedResponse.user = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static TradeCanceledEventResponse getTradeCanceledEventFromLog(Log log) {
        EventValuesWithLog eventValues = staticExtractEventParametersWithLog(TRADECANCELED_EVENT, log);
        TradeCanceledEventResponse typedResponse = new TradeCanceledEventResponse();
        typedResponse.log = log;
        typedResponse.user = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<TradeCanceledEventResponse> tradeCanceledEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getTradeCanceledEventFromLog(log));
    }

    public Flowable<TradeCanceledEventResponse> tradeCanceledEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(TRADECANCELED_EVENT));
        return tradeCanceledEventFlowable(filter);
    }

    public RemoteFunctionCall<String> buyer() {
        final Function function = new Function(FUNC_BUYER,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {
                }));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> buyerDeposit(BigInteger weiValue) {
        final Function function = new Function(
                FUNC_BUYERDEPOSIT,
                Arrays.<Type>asList(),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteFunctionCall<BigInteger> contractCreationDate() {
        final Function function = new Function(FUNC_CONTRACTCREATIONDATE,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> currentState() {
        final Function function = new Function(FUNC_CURRENTSTATE,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> deadlineForInitialDeposits() {
        final Function function = new Function(FUNC_DEADLINEFORINITIALDEPOSITS,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<String> description() {
        final Function function = new Function(FUNC_DESCRIPTION,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {
                }));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> feeRecipient() {
        final Function function = new Function(FUNC_FEERECIPIENT,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {
                }));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<Boolean> hasBuyerSettled() {
        final Function function = new Function(FUNC_HASBUYERSETTLED,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<Boolean> hasSellerSettled() {
        final Function function = new Function(FUNC_HASSELLERSETTLED,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<BigInteger> itemPrice() {
        final Function function = new Function(FUNC_ITEMPRICE,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<String> seller() {
        final Function function = new Function(FUNC_SELLER,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {
                }));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> sellerDeposit(BigInteger weiValue) {
        final Function function = new Function(
                FUNC_SELLERDEPOSIT,
                Arrays.<Type>asList(),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteFunctionCall<TransactionReceipt> setBuyerHasReceivedCorrectItem() {
        final Function function = new Function(
                FUNC_SETBUYERHASRECEIVEDCORRECTITEM,
                Arrays.<Type>asList(),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> setBuyerHasReceivedIncorrectItem() {
        final Function function = new Function(
                FUNC_SETBUYERHASRECEIVEDINCORRECTITEM,
                Arrays.<Type>asList(),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> setSellerHasGivenItem() {
        final Function function = new Function(
                FUNC_SETSELLERHASGIVENITEM,
                Arrays.<Type>asList(),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> settle() {
        final Function function = new Function(
                FUNC_SETTLE,
                Arrays.<Type>asList(),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> userBalances(String param0) {
        final Function function = new Function(FUNC_USERBALANCES,
                Arrays.<Type>asList(new Address(160, param0)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> withdraw() {
        final Function function = new Function(
                FUNC_WITHDRAW,
                Arrays.<Type>asList(),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static Escrow load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new Escrow(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static Escrow load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Escrow(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static Escrow load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new Escrow(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static Escrow load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new Escrow(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<Escrow> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider, String _feeRecipient, String _seller, String _buyer, BigInteger _itemPrice, String _description) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new Address(160, _feeRecipient),
                new Address(160, _seller),
                new Address(160, _buyer),
                new Uint256(_itemPrice),
                new Utf8String(_description)));
        return deployRemoteCall(Escrow.class, web3j, credentials, contractGasProvider, BINARY, encodedConstructor);
    }

    public static RemoteCall<Escrow> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider, String _feeRecipient, String _seller, String _buyer, BigInteger _itemPrice, String _description) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new Address(160, _feeRecipient),
                new Address(160, _seller),
                new Address(160, _buyer),
                new Uint256(_itemPrice),
                new Utf8String(_description)));
        return deployRemoteCall(Escrow.class, web3j, transactionManager, contractGasProvider, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<Escrow> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String _feeRecipient, String _seller, String _buyer, BigInteger _itemPrice, String _description) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new Address(160, _feeRecipient),
                new Address(160, _seller),
                new Address(160, _buyer),
                new Uint256(_itemPrice),
                new Utf8String(_description)));
        return deployRemoteCall(Escrow.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<Escrow> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, String _feeRecipient, String _seller, String _buyer, BigInteger _itemPrice, String _description) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new Address(160, _feeRecipient),
                new Address(160, _seller),
                new Address(160, _buyer),
                new Uint256(_itemPrice),
                new Utf8String(_description)));
        return deployRemoteCall(Escrow.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static class BuyerDepositedEventResponse extends BaseEventResponse {
        public String buyer;

        public BigInteger amount;
    }

    public static class CorrectItemReceivedEventResponse extends BaseEventResponse {
        public String buyer;

        public String seller;

        public BigInteger amountToFeeRecipient;

        public BigInteger amountToSeller;

        public BigInteger amountToBuyer;
    }

    public static class IncorrectItemReceivedEventResponse extends BaseEventResponse {
        public String buyer;

        public BigInteger amountHeldForSettlement;
    }

    public static class ItemSentEventResponse extends BaseEventResponse {
        public String seller;
    }

    public static class SellerDepositedEventResponse extends BaseEventResponse {
        public String seller;

        public BigInteger amount;
    }

    public static class SettledEventResponse extends BaseEventResponse {
        public String buyer;

        public String seller;

        public BigInteger buyerAmount;

        public BigInteger sellerAmount;
    }

    public static class TradeCanceledEventResponse extends BaseEventResponse {
        public String user;

        public BigInteger amount;
    }
}
