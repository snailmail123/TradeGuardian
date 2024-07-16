// SPDX-License-Identifier: MIT
pragma solidity 0.8.20;

import "@openzeppelin/contracts/utils/ReentrancyGuard.sol";

contract Escrow is ReentrancyGuard {
    // The buyer deposits 2x the item price
    uint256 private constant BUYER_DEPOSIT_MULTIPLIER = 2;

    // Amount of the deposit returned to the buyer (25% of total deposit)
    uint256 private constant PERCENTAGE_RETURNED_TO_BUYER = 25;

    // 1% fee of item price if successful transaction between both parties
    uint256 private constant FEE_PERCENTAGE = 1;

    uint256 private constant TWO_HOURS_IN_SECS = 7200;

    mapping(address => uint256) public userBalances;
    address public feeRecipient;
    address public seller;
    address public buyer;
    uint256 public itemPrice;
    uint256 public contractCreationDate;
    uint256 public deadlineForInitialDeposits;
    bool private hasSellerSettled = false;
    bool private hasBuyerSettled = false;

    enum EscrowState {
        AWAITING_DEPOSIT,
        AWAITING_DELIVERY,
        ITEM_SENT,
        ITEM_INCORRECT,
        ITEM_RECEIVED,
        SETTLED,
        TRADE_CANCELED
    }

    EscrowState public currentState = EscrowState.AWAITING_DEPOSIT;

    event SellerDeposited(address indexed seller, uint256 amount);
    event BuyerDeposited(address indexed buyer, uint256 amount);
    event ItemSent(address indexed seller);

    event CorrectItemReceived(
        address indexed buyer,
        address indexed seller,
        uint256 amountToFeeRecipient,
        uint256 amountToSeller,
        uint256 amountToBuyer
    );

    event IncorrectItemReceived(
        address indexed buyer,
        uint256 amountHeldForSettlement
    );

    event Settled(
        address indexed buyer,
        address indexed seller,
        uint256 buyerAmount,
        uint256 sellerAmount
    );

    event TradeCanceled(address indexed user, uint256 amount);

    constructor(
        address _feeRecipient,
        address _seller,
        address _buyer,
        uint256 _itemPrice
    ) {
        feeRecipient = _feeRecipient;
        seller = _seller;
        buyer = _buyer;
        itemPrice = _itemPrice;
        contractCreationDate = block.timestamp;
        deadlineForInitialDeposits = contractCreationDate + TWO_HOURS_IN_SECS;
    }

    modifier onlyBuyer() {
        require(msg.sender == buyer, "Only buyer can execute this");
        _;
    }

    modifier onlySeller() {
        require(msg.sender == seller, "Only seller can execute this");
        _;
    }

    modifier onlyParties() {
        require(
            msg.sender == buyer || msg.sender == seller,
            "You are not the buyer or seller."
        );
        _;
    }

    function sellerDeposit() external payable onlySeller {
        require(
            currentState == EscrowState.AWAITING_DEPOSIT,
            "Invalid state for this action"
        );

        require(userBalances[seller] == 0, "Seller has already deposited");

        uint256 depositAmount = msg.value;
        uint256 requiredDepositAmount = itemPrice;

        require(
            depositAmount == requiredDepositAmount,
            "Amount sent must be exactly equal to the item price"
        );

        userBalances[seller] = depositAmount;

        if (userBalances[buyer] > 0) {
            currentState = EscrowState.AWAITING_DELIVERY;
        }

        emit SellerDeposited(seller, depositAmount);
    }

    function buyerDeposit() external payable onlyBuyer {
        require(
            currentState == EscrowState.AWAITING_DEPOSIT,
            "Invalid state for this action"
        );

        require(userBalances[buyer] == 0, "Buyer has already deposited");

        uint256 depositAmount = msg.value;
        uint256 requiredDepositAmount = itemPrice * BUYER_DEPOSIT_MULTIPLIER;

        require(
            depositAmount == requiredDepositAmount,
            "Amount sent must be exactly equal to 2x the item price"
        );

        userBalances[buyer] = depositAmount;

        if (userBalances[seller] > 0) {
            currentState = EscrowState.AWAITING_DELIVERY;
        }

        emit BuyerDeposited(buyer, depositAmount);
    }

    function withdraw() external onlyParties nonReentrant {
        require(
            currentState == EscrowState.AWAITING_DEPOSIT,
            "Not in the AWAITING_DEPOSIT state"
        );

        require(
            block.timestamp >= deadlineForInitialDeposits,
            "Cannot withdraw before two hours after contract creation"
        );

        uint256 amount = userBalances[msg.sender];
        require(amount > 0, "No funds to withdraw");

        currentState = EscrowState.TRADE_CANCELED;

        userBalances[msg.sender] = 0;
        payable(msg.sender).transfer(amount);

        emit TradeCanceled(msg.sender, amount);
    }

    function setSellerHasGivenItem() external onlySeller {
        require(
            currentState == EscrowState.AWAITING_DELIVERY,
            "Invalid state for this action"
        );

        currentState = EscrowState.ITEM_SENT;

        emit ItemSent(seller);
    }

    function setBuyerHasReceivedCorrectItem() external onlyBuyer nonReentrant {
        require(
            currentState == EscrowState.ITEM_SENT,
            "Invalid state for this action"
        );

        currentState = EscrowState.ITEM_RECEIVED;

        uint256 totalFee = (itemPrice * FEE_PERCENTAGE) / 100;
        uint256 feePerParty = totalFee / 2;

        uint256 sellerTotalBeforeFee = itemPrice + userBalances[seller];
        uint256 amountToSeller = sellerTotalBeforeFee - feePerParty;

        uint256 amountToBuyer = userBalances[buyer] - itemPrice - feePerParty;

        userBalances[seller] = 0;
        userBalances[buyer] = 0;

        payable(feeRecipient).transfer(totalFee);
        payable(seller).transfer(amountToSeller);
        payable(buyer).transfer(amountToBuyer);

        emit CorrectItemReceived(
            buyer,
            seller,
            totalFee,
            amountToSeller,
            amountToBuyer
        );
    }

    function setBuyerHasReceivedIncorrectItem()
        external
        onlyBuyer
        nonReentrant
    {
        require(
            currentState == EscrowState.ITEM_SENT,
            "Invalid state for this action"
        );

        currentState = EscrowState.ITEM_INCORRECT;

        uint256 amountReturnedToBuyer = (userBalances[buyer] *
            PERCENTAGE_RETURNED_TO_BUYER) / 100;

        // Check to prevent underflows
        require(
            userBalances[buyer] >= amountReturnedToBuyer,
            "Insufficient balance"
        );

        uint256 buyerUpdatedBalance = userBalances[buyer] -
            amountReturnedToBuyer;
        userBalances[buyer] = buyerUpdatedBalance;

        payable(buyer).transfer(amountReturnedToBuyer);

        emit IncorrectItemReceived(buyer, amountReturnedToBuyer);
    }

    function settle() external onlyParties nonReentrant {
        require(
            currentState == EscrowState.ITEM_INCORRECT,
            "Invalid state for settlement"
        );

        if (msg.sender == buyer) {
            require(!hasBuyerSettled, "The buyer has already settled.");
            hasBuyerSettled = true;
        } else if (msg.sender == seller) {
            require(!hasSellerSettled, "The seller has already settled.");
            hasSellerSettled = true;
        }

        if (hasBuyerSettled && hasSellerSettled) {
            currentState = EscrowState.SETTLED;

            uint256 sellerBalance = userBalances[seller];
            uint256 buyerBalance = userBalances[buyer];

            userBalances[seller] = 0;
            userBalances[buyer] = 0;

            payable(seller).transfer(sellerBalance);
            payable(buyer).transfer(buyerBalance);

            emit Settled(buyer, seller, buyerBalance, sellerBalance);
        }
    }
}
