const { expect } = require("chai");
const { 
    loadFixture,
    deployEscrow, 
    EscrowStates, 
    BUYER_DEPOSIT_MULTIPLIER, 
    ONE_WEI 
} = require("./utils");

describe("Buyer Deposit", function() {
    it("Should revert if the state is not AWAITING_DEPOSIT", async function () {
        const { escrow, buyer, seller, itemPrice } = await loadFixture(deployEscrow);
        const buyerDepositAmount = itemPrice * BUYER_DEPOSIT_MULTIPLIER;
        const sellerDepositAmount = itemPrice;

        //Change to a different state from AWAITING_DEPOSIT
        await escrow.connect(seller).sellerDeposit({ value: sellerDepositAmount });
        await escrow.connect(buyer).buyerDeposit({ value: buyerDepositAmount });

        const depositAmount = itemPrice * BUYER_DEPOSIT_MULTIPLIER;
        await expect(escrow.connect(buyer).buyerDeposit({ value: depositAmount }))
            .to.be.revertedWith("Invalid state for this action");
    });

    it("Should revert if the buyer has already deposited", async function () {
        const { escrow, buyer, itemPrice } = await loadFixture(deployEscrow);
        const buyerDepositAmount = itemPrice * BUYER_DEPOSIT_MULTIPLIER;

        // First deposit from buyer
        await escrow.connect(buyer).buyerDeposit({ value: buyerDepositAmount });

        // Attempt a second deposit from buyer
        await expect(escrow.connect(buyer).buyerDeposit({ value: buyerDepositAmount }))
            .to.be.revertedWith("Buyer has already deposited");
    });

    it("Should not allow non-buyer to deposit", async function () {
        const { escrow, seller, itemPrice } = await loadFixture(deployEscrow);
        const depositAmount = itemPrice;

        await expect(escrow.connect(seller).buyerDeposit({
            value: depositAmount
        }))
            .to.be.revertedWith("Only buyer can execute this");
    });

    it("Should revert if buyer deposits 1 wei above the correct amount", async function () {
        const { escrow, buyer, itemPrice } = await loadFixture(deployEscrow);
        const depositAmountAbove = (itemPrice * BUYER_DEPOSIT_MULTIPLIER) + ONE_WEI;

        await expect(escrow.connect(buyer).buyerDeposit({
            value: depositAmountAbove
        }))
            .to.be.revertedWith("Amount sent must be exactly equal to 2x the item price");
    });

    it("Should revert if buyer deposits 1 wei below the correct amount", async function () {
        const { escrow, buyer, itemPrice } = await loadFixture(deployEscrow);
        const depositAmountBelow = (itemPrice * BUYER_DEPOSIT_MULTIPLIER) - ONE_WEI;

        await expect(escrow.connect(buyer).buyerDeposit({
            value: depositAmountBelow
        }))
            .to.be.revertedWith("Amount sent must be exactly equal to 2x the item price");
    });

    it("Should allow buyer to deposit correct amount", async function () {
        const { escrow, buyer, itemPrice } = await loadFixture(deployEscrow);
        const depositAmount = itemPrice * BUYER_DEPOSIT_MULTIPLIER;
    
        await escrow.connect(buyer).buyerDeposit({ value: depositAmount });
    
        const buyerBalance = await escrow.userBalances(buyer.address);
        expect(buyerBalance).to.equal(depositAmount);
    });
    
    it("Should remain in AWAITING_DEPOSIT state if only buyer has deposited", async function () {
        const { escrow, buyer, itemPrice } = await loadFixture(deployEscrow);
        const buyerDepositAmount = itemPrice * BUYER_DEPOSIT_MULTIPLIER;

        await escrow.connect(buyer).buyerDeposit({ value: buyerDepositAmount });

        const currentState = await escrow.currentState();
        expect(currentState).to.equal(EscrowStates.AWAITING_DEPOSIT);
    });

    it("Should transition to AWAITING_DELIVERY after both seller and buyer deposit", async function () {
        const { escrow, buyer, seller, itemPrice } = await loadFixture(deployEscrow);
        const buyerDepositAmount = itemPrice * BUYER_DEPOSIT_MULTIPLIER;
        const sellerDepositAmount = itemPrice;

        await escrow.connect(seller).sellerDeposit({ value: sellerDepositAmount });
        await escrow.connect(buyer).buyerDeposit({ value: buyerDepositAmount });

        const currentState = await escrow.currentState();
        expect(currentState).to.equal(EscrowStates.AWAITING_DELIVERY);
    });

    it("Should emit BuyerDeposited event with correct parameters", async function () {
        const { escrow, buyer, itemPrice } = await loadFixture(deployEscrow);
        const depositAmount = itemPrice * BUYER_DEPOSIT_MULTIPLIER;
    
        await expect(escrow.connect(buyer).buyerDeposit({
            value: depositAmount
        }))
            .to.emit(escrow, 'BuyerDeposited')
            .withArgs(buyer.address, depositAmount);
    });    
});
