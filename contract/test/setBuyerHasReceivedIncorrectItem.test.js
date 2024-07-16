const { expect } = require("chai");
const { 
    loadFixture,
    deployEscrow, 
    BUYER_DEPOSIT_MULTIPLIER,
    PERCENTAGE_RETURNED_TO_BUYER,
    EscrowStates
} = require("./utils");

describe("Set buyer has received incorrect item", function () {

    it("Should revert if called by non-buyer", async function () {
        const { escrow, seller } = await loadFixture(deployEscrow);

        await expect(escrow.connect(seller).setBuyerHasReceivedIncorrectItem())
            .to.be.revertedWith("Only buyer can execute this");
    });

    it("Should revert if current state is not ITEM_SENT", async function () {
        const { escrow, buyer } = await loadFixture(deployEscrow);

        // Assuming initial state is AWAITING_DEPOSIT
        await expect(escrow.connect(buyer).setBuyerHasReceivedIncorrectItem())
            .to.be.revertedWith("Invalid state for this action");
    });

    it("Should update the state to ITEM_INCORRECT", async function () {
        const { escrow, buyer, seller, itemPrice } = await loadFixture(deployEscrow);
        const buyerDepositAmount = itemPrice * BUYER_DEPOSIT_MULTIPLIER;
        const sellerDepositAmount = itemPrice;

        await escrow.connect(buyer).buyerDeposit({ value: buyerDepositAmount });
        await escrow.connect(seller).sellerDeposit({ value: sellerDepositAmount });
        await escrow.connect(seller).setSellerHasGivenItem();
        await escrow.connect(buyer).setBuyerHasReceivedIncorrectItem();

        const currentState = await escrow.currentState();
        expect(currentState).to.equal(EscrowStates.ITEM_INCORRECT);
    });

    it("Should update the buyer's internal balance correctly", async function() {
        const { escrow, buyer, seller, itemPrice } = await loadFixture(deployEscrow);
        const buyerDepositAmount = itemPrice * BUYER_DEPOSIT_MULTIPLIER;
        const sellerDepositAmount = itemPrice;
    
        await escrow.connect(buyer).buyerDeposit({ value: buyerDepositAmount });
        await escrow.connect(seller).sellerDeposit({ value: sellerDepositAmount });
        await escrow.connect(seller).setSellerHasGivenItem();
    
        const initialInternalBuyerBalance = await escrow.userBalances(buyer.address);
        const amountReturnedToBuyer = (initialInternalBuyerBalance * PERCENTAGE_RETURNED_TO_BUYER) / 100n;
    
        await escrow.connect(buyer).setBuyerHasReceivedIncorrectItem();
    
        const finalInternalBuyerBalance = await escrow.userBalances(buyer.address);

        expect(finalInternalBuyerBalance).to.equal(initialInternalBuyerBalance - amountReturnedToBuyer);
    });
    
    it("Should emit IncorrectItemReceived event with correct parameters", async function () {
        const { escrow, buyer, seller, itemPrice } = await loadFixture(deployEscrow);
        const buyerDepositAmount = itemPrice * BUYER_DEPOSIT_MULTIPLIER;
        const sellerDepositAmount = itemPrice;

        await escrow.connect(buyer).buyerDeposit({ value: buyerDepositAmount });
        await escrow.connect(seller).sellerDeposit({ value: sellerDepositAmount });
        await escrow.connect(seller).setSellerHasGivenItem();

        const buyerBalance = await escrow.userBalances(buyer.address);
        const amountReturnedToBuyer = (buyerBalance * PERCENTAGE_RETURNED_TO_BUYER) / 100n;

        await expect(escrow.connect(buyer).setBuyerHasReceivedIncorrectItem())
            .to.emit(escrow, 'IncorrectItemReceived')
            .withArgs(buyer.address, amountReturnedToBuyer);
    });

})