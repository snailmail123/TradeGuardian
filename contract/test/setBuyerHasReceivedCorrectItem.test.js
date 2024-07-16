const { expect } = require("chai");
const {
    loadFixture,
    deployEscrow,
    BUYER_DEPOSIT_MULTIPLIER,
    FEE_PERCENTAGE,
    EscrowStates
} = require("./utils");

describe("Set buyer has correct item", function () {

    it("Should revert if called by non-buyer", async function () {
        const { escrow, seller } = await loadFixture(deployEscrow);

        await expect(escrow.connect(seller).setBuyerHasReceivedCorrectItem())
            .to.be.revertedWith("Only buyer can execute this");
    });

    it("Should revert if current state is not ITEM_SENT", async function () {
        const { escrow, buyer } = await loadFixture(deployEscrow);

        // Assuming initial state is AWAITING_DEPOSIT
        await expect(escrow.connect(buyer).setBuyerHasReceivedCorrectItem())
            .to.be.revertedWith("Invalid state for this action");
    });

    it("Should update the state to ITEM_RECEIVED", async function () {
        const { escrow, buyer, seller, itemPrice } = await loadFixture(deployEscrow);
        const buyerDepositAmount = itemPrice * BUYER_DEPOSIT_MULTIPLIER;
        const sellerDepositAmount = itemPrice;

        await escrow.connect(buyer).buyerDeposit({ value: buyerDepositAmount });
        await escrow.connect(seller).sellerDeposit({ value: sellerDepositAmount });
        await escrow.connect(seller).setSellerHasGivenItem();
        await escrow.connect(buyer).setBuyerHasReceivedCorrectItem();

        const currentState = await escrow.currentState();
        expect(currentState).to.equal(EscrowStates.ITEM_RECEIVED);
    });

    it("Should transfer the correct amount to the seller", async function () {
        const { escrow, buyer, seller, itemPrice } = await loadFixture(deployEscrow);
        const buyerDepositAmount = itemPrice * BUYER_DEPOSIT_MULTIPLIER;
        const sellerDepositAmount = itemPrice;

        await escrow.connect(buyer).buyerDeposit({ value: buyerDepositAmount });
        await escrow.connect(seller).sellerDeposit({ value: sellerDepositAmount });

        await escrow.connect(seller).setSellerHasGivenItem();

        const feeAmount = (itemPrice * FEE_PERCENTAGE) / 100n;
        const feePerParty = feeAmount / 2n;
        const sellerBalance = await escrow.userBalances(seller.address);
        const expectedPayoutToSeller = sellerBalance + itemPrice - feePerParty;

        const sellerWalletBalanceBefore = await ethers.provider.getBalance(seller.address);

        await escrow.connect(buyer).setBuyerHasReceivedCorrectItem();

        const sellerWalletBalanceAfter = await ethers.provider.getBalance(seller.address);

        expect(sellerWalletBalanceAfter - sellerWalletBalanceBefore).to.equal(expectedPayoutToSeller);
    });

    it("Should transfer the correct amount to the buyer", async function () {
        const { escrow, buyer, seller, itemPrice } = await loadFixture(deployEscrow);
        const buyerDepositAmount = itemPrice * BUYER_DEPOSIT_MULTIPLIER;
        const sellerDepositAmount = itemPrice;

        await escrow.connect(buyer).buyerDeposit({ value: buyerDepositAmount });
        await escrow.connect(seller).sellerDeposit({ value: sellerDepositAmount });

        await escrow.connect(seller).setSellerHasGivenItem();

        const feeAmount = (itemPrice * FEE_PERCENTAGE) / 100n;
        const feePerParty = feeAmount / 2n;
        const buyerBalance = await escrow.userBalances(buyer.address);
        const expectedPayoutToBuyer = buyerBalance - itemPrice - feePerParty;

        const buyerWalletBalanceBefore = await ethers.provider.getBalance(buyer.address);

        const tx = await escrow.connect(buyer).setBuyerHasReceivedCorrectItem();
        const gasUsed = (await tx.wait()).gasUsed;
        const txCost = gasUsed * tx.gasPrice;

        const buyerWalletBalanceAfter = await ethers.provider.getBalance(buyer.address);

        expect(buyerWalletBalanceBefore + expectedPayoutToBuyer - txCost).to.equal(buyerWalletBalanceAfter);
    });

    it("Should transfer the correct amount to the fee recipient", async function () {
        const { escrow, buyer, seller, feeRecipient, itemPrice } = await loadFixture(deployEscrow);
        const buyerDepositAmount = itemPrice * BUYER_DEPOSIT_MULTIPLIER;
        const sellerDepositAmount = itemPrice;

        await escrow.connect(buyer).buyerDeposit({ value: buyerDepositAmount });
        await escrow.connect(seller).sellerDeposit({ value: sellerDepositAmount });

        await escrow.connect(seller).setSellerHasGivenItem();

        const feeAmount = (itemPrice * FEE_PERCENTAGE) / 100n;

        const feeRecipientBalanceBefore = await ethers.provider.getBalance(feeRecipient.address);

        await escrow.connect(buyer).setBuyerHasReceivedCorrectItem();

        const feeRecipientBalanceAfter = await ethers.provider.getBalance(feeRecipient.address);

        expect(feeRecipientBalanceBefore + feeAmount).to.equal(feeRecipientBalanceAfter);
    });

    it("Should emit CorrectItemReceived event with correct parameters", async function () {
        const { escrow, buyer, seller, itemPrice } = await loadFixture(deployEscrow);
        const buyerDepositAmount = itemPrice * BUYER_DEPOSIT_MULTIPLIER;
        const sellerDepositAmount = itemPrice;

        await escrow.connect(buyer).buyerDeposit({ value: buyerDepositAmount });
        await escrow.connect(seller).sellerDeposit({ value: sellerDepositAmount });
        await escrow.connect(seller).setSellerHasGivenItem();

        const totalFeeAmount = (itemPrice * FEE_PERCENTAGE) / 100n;
        const feePerParty = totalFeeAmount / 2n;

        const sellerTotalBeforeFee = itemPrice + await escrow.userBalances(seller.address);
        const expectedPayoutAmountToSeller = sellerTotalBeforeFee - feePerParty;

        const buyerBalance = await escrow.userBalances(buyer.address);
        const expectedAmountReturnedToBuyer = buyerBalance - itemPrice - feePerParty;

        await expect(escrow.connect(buyer).setBuyerHasReceivedCorrectItem())
            .to.emit(escrow, 'CorrectItemReceived')
            .withArgs(buyer.address, seller.address, totalFeeAmount, expectedPayoutAmountToSeller, expectedAmountReturnedToBuyer);
    });
});